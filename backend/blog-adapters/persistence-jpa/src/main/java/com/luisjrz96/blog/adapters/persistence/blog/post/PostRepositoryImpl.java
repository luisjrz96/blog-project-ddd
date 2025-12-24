package com.luisjrz96.blog.adapters.persistence.blog.post;

import java.util.List;

import org.springframework.stereotype.Service;

import com.luisjrz96.blog.application.blog.post.port.PostRepository;
import com.luisjrz96.blog.application.shared.VersionedEvent;
import com.luisjrz96.blog.application.shared.port.EventStore;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.post.Post;
import com.luisjrz96.blog.domain.blog.post.PostId;

@Service
public class PostRepositoryImpl implements PostRepository {

  private final EventStore eventStore;
  private final PostProjectionHandler projector;

  public PostRepositoryImpl(EventStore eventStore, PostProjectionHandler projector) {
    this.eventStore = eventStore;
    this.projector = projector;
  }

  @Override
  public Post load(PostId id) {
    List<VersionedEvent> history = eventStore.load("Post", id.value());

    Post post = new Post();
    int lastVersion = 0;

    for (VersionedEvent ve : history) {
      post.replayEvent(ve.event());
      lastVersion = ve.version();
    }

    post.setVersion(lastVersion);
    return post;
  }

  @Override
  public void save(Post post) {
    List<DomainEvent> newEvents = post.getUncommittedEvents();
    if (newEvents.isEmpty()) {
      return;
    }

    List<VersionedEvent> persisted =
        eventStore.append("Post", post.getId().value(), post.getVersion(), newEvents);

    for (VersionedEvent ve : persisted) {
      projector.project(ve.event(), ve.version());
    }

    int lastVersion = persisted.getLast().version();
    post.setVersion(lastVersion);
    post.markEventsAsCommitted();
  }
}
