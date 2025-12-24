package com.luisjrz96.blog.adapters.persistence.blog.post;

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
import com.luisjrz96.blog.domain.blog.post.Post;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.post.events.PostCreated;
import com.luisjrz96.blog.domain.blog.post.events.PostUpdated;
import com.luisjrz96.blog.domain.shared.Title;

@ExtendWith(MockitoExtension.class)
class PostRepositoryImplTest {

  @Mock private EventStore eventStore;

  @Mock private PostProjectionHandler projector;

  @InjectMocks private PostRepositoryImpl repository;

  @Test
  void shouldLoadPostByReplayingEvents() {
    // given
    UUID uuid = UUID.randomUUID();
    PostId id = new PostId(uuid);
    PostCreated created = mock(PostCreated.class);
    PostUpdated updated = mock(PostUpdated.class);
    List<VersionedEvent> history =
        List.of(new VersionedEvent(1, created), new VersionedEvent(2, updated));

    when(created.title()).thenReturn(new Title("First Post"));
    when(updated.title()).thenReturn(new Title("Updated Post"));
    when(eventStore.load("Post", id.value())).thenReturn(history);

    // when
    Post post = repository.load(id);

    // then
    assertEquals(2, post.getVersion());
    verify(eventStore).load("Post", id.value());
  }

  @Test
  void shouldNotSaveWhenThereAreNoUncommittedEvents() {
    // given
    Post post = mock(Post.class);
    when(post.getUncommittedEvents()).thenReturn(List.of());

    // when
    repository.save(post);

    // then
    verifyNoInteractions(eventStore);
    verifyNoInteractions(projector);
  }

  @Test
  void shouldPersistNewEventsAndProjectThem() {
    // given
    UUID uuid = UUID.randomUUID();
    PostId id = new PostId(uuid);
    Post post = mock(Post.class);

    DomainEvent event1 = mock(DomainEvent.class);
    DomainEvent event2 = mock(DomainEvent.class);
    List<DomainEvent> newEvents = List.of(event1, event2);

    when(post.getUncommittedEvents()).thenReturn(newEvents);
    when(post.getId()).thenReturn(id);
    when(post.getVersion()).thenReturn(1);

    VersionedEvent ve1 = new VersionedEvent(2, event1);
    VersionedEvent ve2 = new VersionedEvent(3, event2);
    List<VersionedEvent> persisted = List.of(ve1, ve2);

    when(eventStore.append("Post", id.value(), 1, newEvents)).thenReturn(persisted);

    // when
    repository.save(post);

    // then
    verify(eventStore).append("Post", id.value(), 1, newEvents);
    verify(projector).project(event1, 2);
    verify(projector).project(event2, 3);
  }
}
