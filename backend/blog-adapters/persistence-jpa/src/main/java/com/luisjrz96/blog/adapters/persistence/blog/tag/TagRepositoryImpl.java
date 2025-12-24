package com.luisjrz96.blog.adapters.persistence.blog.tag;

import java.util.List;

import org.springframework.stereotype.Service;

import com.luisjrz96.blog.application.blog.tag.port.TagRepository;
import com.luisjrz96.blog.application.shared.VersionedEvent;
import com.luisjrz96.blog.application.shared.port.EventStore;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.tag.Tag;
import com.luisjrz96.blog.domain.blog.tag.TagId;

@Service
public class TagRepositoryImpl implements TagRepository {

  private final EventStore eventStore;
  private final TagProjectionHandler projector;

  public TagRepositoryImpl(EventStore eventStore, TagProjectionHandler projector) {
    this.eventStore = eventStore;
    this.projector = projector;
  }

  @Override
  public Tag load(TagId id) {
    List<VersionedEvent> history = eventStore.load("Tag", id.value());

    Tag tag = new Tag();
    int lastVersion = 0;

    for (VersionedEvent ve : history) {
      tag.replayEvent(ve.event());
      lastVersion = ve.version();
    }

    tag.setVersion(lastVersion);
    return tag;
  }

  @Override
  public void save(Tag tag) {
    List<DomainEvent> newEvents = tag.getUncommittedEvents();
    if (newEvents.isEmpty()) {
      return;
    }

    List<VersionedEvent> persisted =
        eventStore.append("Tag", tag.getId().value(), tag.getVersion(), newEvents);

    for (VersionedEvent ve : persisted) {
      projector.project(ve.event(), ve.version());
    }

    int lastVersion = persisted.getLast().version();
    tag.setVersion(lastVersion);
    tag.markEventsAsCommitted();
  }
}
