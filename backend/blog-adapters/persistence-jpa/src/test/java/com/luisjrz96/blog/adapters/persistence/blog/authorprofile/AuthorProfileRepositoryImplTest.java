package com.luisjrz96.blog.adapters.persistence.blog.authorprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.luisjrz96.blog.application.shared.VersionedEvent;
import com.luisjrz96.blog.application.shared.port.EventStore;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.authorprofile.AuthorProfile;
import com.luisjrz96.blog.domain.blog.authorprofile.events.AuthorProfileCreated;
import com.luisjrz96.blog.domain.blog.authorprofile.events.AuthorProfileUpdated;
import com.luisjrz96.blog.domain.shared.AuthorId;

@ExtendWith(MockitoExtension.class)
class AuthorProfileRepositoryImplTest {

  @Mock private EventStore eventStore;
  @Mock private AuthorProfileProjectionHandler projector;
  @InjectMocks private AuthorProfileRepositoryImpl repository;

  @Test
  void shouldLoadAuthorProfileReplayingEvents() {
    // given
    UUID uuid = UUID.randomUUID();
    AuthorId authorId = new AuthorId(uuid);

    DomainEvent created = mock(AuthorProfileCreated.class);
    DomainEvent updated = mock(AuthorProfileUpdated.class);

    List<VersionedEvent> history =
        List.of(new VersionedEvent(1, created), new VersionedEvent(2, updated));
    when(eventStore.load("AuthorProfile", authorId.value())).thenReturn(history);

    // when
    AuthorProfile authorProfile = repository.load(authorId);

    // then
    assertEquals(2, authorProfile.getVersion());
    verify(eventStore).load("AuthorProfile", authorId.value());
  }

  @Test
  void shouldNotPersistWhenThereAreUncommitedEvents() {
    // given
    AuthorProfile authorProfile = mock(AuthorProfile.class);
    when(authorProfile.getUncommittedEvents()).thenReturn(List.of());

    // when
    repository.save(authorProfile);

    // then
    verifyNoInteractions(eventStore);
    verifyNoInteractions(projector);
  }

  @Test
  void shouldPersistEventsAndProjectThem() {
    // given
    UUID uuid = UUID.randomUUID();
    AuthorId authorId = new AuthorId(uuid);
    AuthorProfile authorProfile = mock(AuthorProfile.class);

    DomainEvent created = mock(DomainEvent.class);
    DomainEvent updated = mock(DomainEvent.class);

    when(authorProfile.getAuthorId()).thenReturn(authorId);
    when(authorProfile.getVersion()).thenReturn(0);
    when(authorProfile.getUncommittedEvents()).thenReturn(List.of(created, updated));

    List<VersionedEvent> persisted =
        List.of(new VersionedEvent(1, created), new VersionedEvent(2, updated));
    when(eventStore.append("AuthorProfile", authorId.value(), 0, List.of(created, updated)))
        .thenReturn(persisted);

    // when
    repository.save(authorProfile);

    // then
    verify(eventStore).append("AuthorProfile", authorId.value(), 0, List.of(created, updated));

    verify(projector).project(created, 1);
    verify(projector).project(updated, 2);
    verify(authorProfile).setVersion(2);
    verify(authorProfile).markEventsAsCommitted();
  }
}
