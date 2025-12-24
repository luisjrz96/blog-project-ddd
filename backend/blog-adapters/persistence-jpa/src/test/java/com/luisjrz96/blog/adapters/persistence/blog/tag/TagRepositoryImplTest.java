package com.luisjrz96.blog.adapters.persistence.blog.tag;

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
import com.luisjrz96.blog.domain.blog.tag.Tag;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.events.TagCreated;
import com.luisjrz96.blog.domain.blog.tag.events.TagUpdated;

@ExtendWith(MockitoExtension.class)
class TagRepositoryImplTest {

  @Mock private EventStore eventStore;

  @Mock private TagProjectionHandler projector;

  @InjectMocks private TagRepositoryImpl repository;

  @Test
  void shouldLoadTagByReplayingEvents() {
    // given
    UUID uuid = UUID.randomUUID();
    TagId id = new TagId(uuid);

    DomainEvent created = mock(TagCreated.class);
    DomainEvent updated = mock(TagUpdated.class);

    List<VersionedEvent> history =
        List.of(new VersionedEvent(1, created), new VersionedEvent(2, updated));

    when(eventStore.load("Tag", id.value())).thenReturn(history);

    // when
    Tag tag = repository.load(id);

    assertEquals(2, tag.getVersion());
    verify(eventStore).load("Tag", id.value());
  }

  @Test
  void shouldNotSaveWhenThereAreNoUncommittedEvents() {
    // given
    Tag tag = mock(Tag.class);
    when(tag.getUncommittedEvents()).thenReturn(List.of());

    // when
    repository.save(tag);

    verifyNoInteractions(eventStore);
    verifyNoInteractions(projector);
  }

  @Test
  void shouldPersistEventsAndProjectThem() {
    // given
    UUID uuid = UUID.randomUUID();
    TagId id = new TagId(uuid);
    Tag tag = mock(Tag.class);

    DomainEvent event1 = mock(DomainEvent.class);
    DomainEvent event2 = mock(DomainEvent.class);

    when(tag.getId()).thenReturn(id);
    when(tag.getVersion()).thenReturn(1);
    when(tag.getUncommittedEvents()).thenReturn(List.of(event1, event2));

    List<VersionedEvent> persisted =
        List.of(new VersionedEvent(2, event1), new VersionedEvent(3, event2));

    when(eventStore.append("Tag", id.value(), 1, List.of(event1, event2))).thenReturn(persisted);

    // when
    repository.save(tag);

    // then
    verify(eventStore).append("Tag", id.value(), 1, List.of(event1, event2));
    verify(projector).project(event1, 2);
    verify(projector).project(event2, 3);
  }
}
