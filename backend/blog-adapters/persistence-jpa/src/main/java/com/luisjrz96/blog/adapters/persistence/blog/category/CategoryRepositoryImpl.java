package com.luisjrz96.blog.adapters.persistence.blog.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.luisjrz96.blog.application.blog.category.port.CategoryRepository;
import com.luisjrz96.blog.application.shared.VersionedEvent;
import com.luisjrz96.blog.application.shared.port.EventStore;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.category.Category;
import com.luisjrz96.blog.domain.blog.category.CategoryId;

@Service
public class CategoryRepositoryImpl implements CategoryRepository {

  private final EventStore eventStore;
  private final CategoryProjectionHandler projector;

  public CategoryRepositoryImpl(EventStore eventStore, CategoryProjectionHandler projector) {
    this.eventStore = eventStore;
    this.projector = projector;
  }

  @Override
  public Category load(CategoryId id) {
    List<VersionedEvent> history = eventStore.load("Category", id.value());

    Category category = new Category();
    int lastVersion = 0;

    for (VersionedEvent ve : history) {
      category.replayEvent(ve.event());
      lastVersion = ve.version();
    }

    category.setVersion(lastVersion);
    return category;
  }

  @Override
  public void save(Category category) {
    List<DomainEvent> newEvents = category.getUncommittedEvents();
    if (newEvents.isEmpty()) {
      return;
    }

    List<VersionedEvent> persisted =
        eventStore.append("Category", category.getId().value(), category.getVersion(), newEvents);

    for (VersionedEvent ve : persisted) {
      projector.project(ve.event(), ve.version());
    }

    int lastVersion = persisted.getLast().version();
    category.setVersion(lastVersion);
    category.markEventsAsCommitted();
  }
}
