package com.luisjrz96.blog.adapters.persistence.blog.authorprofile;

import java.util.List;

import org.springframework.stereotype.Service;

import com.luisjrz96.blog.application.blog.authorprofile.port.AuthorProfileRepository;
import com.luisjrz96.blog.application.shared.VersionedEvent;
import com.luisjrz96.blog.application.shared.port.EventStore;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.authorprofile.AuthorProfile;
import com.luisjrz96.blog.domain.shared.AuthorId;

@Service
public class AuthorProfileRepositoryImpl implements AuthorProfileRepository {

  private final EventStore eventStore;
  private final AuthorProfileProjectionHandler projector;

  public AuthorProfileRepositoryImpl(
      EventStore eventStore, AuthorProfileProjectionHandler projector) {
    this.eventStore = eventStore;
    this.projector = projector;
  }

  @Override
  public AuthorProfile load(AuthorId id) {
    List<VersionedEvent> history = eventStore.load("AuthorProfile", id.value());

    AuthorProfile authorProfile = new AuthorProfile();
    int lastVersion = 0;

    for (VersionedEvent ve : history) {
      authorProfile.replayEvent(ve.event());
      lastVersion = ve.version();
    }
    authorProfile.setVersion(lastVersion);
    return authorProfile;
  }

  @Override
  public void save(AuthorProfile authorProfile) {
    List<DomainEvent> newEvents = authorProfile.getUncommittedEvents();
    if (newEvents.isEmpty()) {
      return;
    }

    List<VersionedEvent> persisted =
        eventStore.append(
            "AuthorProfile",
            authorProfile.getAuthorId().value(),
            authorProfile.getVersion(),
            newEvents);

    for (VersionedEvent ve : persisted) {
      projector.project(ve.event(), ve.version());
    }
    int lastVersion = persisted.getLast().version();
    authorProfile.setVersion(lastVersion);
    authorProfile.markEventsAsCommitted();
  }
}
