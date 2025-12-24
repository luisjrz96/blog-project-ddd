package com.luisjrz96.blog.adapters.persistence.blog.category;

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
import com.luisjrz96.blog.domain.blog.category.Category;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.events.CategoryCreated;
import com.luisjrz96.blog.domain.blog.category.events.CategoryUpdated;

@ExtendWith(MockitoExtension.class)
class CategoryRepositoryImplTest {

  @Mock private EventStore eventStore;

  @Mock private CategoryProjectionHandler projector;

  @InjectMocks private CategoryRepositoryImpl repository;

  @Test
  void shouldLoadCategoryByReplayingEvents() {
    // given
    UUID uuid = UUID.randomUUID();
    CategoryId id = new CategoryId(uuid);

    DomainEvent created = mock(CategoryCreated.class);
    DomainEvent updated = mock(CategoryUpdated.class);

    List<VersionedEvent> history =
        List.of(new VersionedEvent(1, created), new VersionedEvent(2, updated));

    when(eventStore.load("Category", id.value())).thenReturn(history);

    // when
    Category category = repository.load(id);

    // then
    assertEquals(2, category.getVersion());
    verify(eventStore).load("Category", id.value());
    // replay happens inside Category, we trust the aggregate
  }

  @Test
  void shouldNotSaveWhenThereAreNoUncommittedEvents() {
    // given
    Category category = mock(Category.class);
    when(category.getUncommittedEvents()).thenReturn(List.of());

    // when
    repository.save(category);

    // then
    verifyNoInteractions(eventStore);
    verifyNoInteractions(projector);
  }

  @Test
  void shouldPersistEventsAndProjectThem() {
    // given
    UUID uuid = UUID.randomUUID();
    CategoryId id = new CategoryId(uuid);
    Category category = mock(Category.class);

    DomainEvent event1 = mock(DomainEvent.class);
    DomainEvent event2 = mock(DomainEvent.class);

    when(category.getId()).thenReturn(id);
    when(category.getVersion()).thenReturn(1);
    when(category.getUncommittedEvents()).thenReturn(List.of(event1, event2));

    List<VersionedEvent> persisted =
        List.of(new VersionedEvent(2, event1), new VersionedEvent(3, event2));

    when(eventStore.append("Category", id.value(), 1, List.of(event1, event2)))
        .thenReturn(persisted);

    // when
    repository.save(category);

    // then
    verify(eventStore).append("Category", id.value(), 1, List.of(event1, event2));

    verify(projector).project(event1, 2);
    verify(projector).project(event2, 3);

    verify(category).setVersion(3);
    verify(category).markEventsAsCommitted();
  }
}
