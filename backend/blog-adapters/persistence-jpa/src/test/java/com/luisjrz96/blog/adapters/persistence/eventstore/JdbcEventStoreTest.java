package com.luisjrz96.blog.adapters.persistence.eventstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.luisjrz96.blog.application.shared.VersionedEvent;
import com.luisjrz96.blog.domain.DomainEvent;

@ExtendWith(MockitoExtension.class)
class JdbcEventStoreTest {

  @Mock private JdbcTemplate jdbcTemplate;

  @Mock private DomainEventSerializer serializer;

  @InjectMocks private JdbcEventStore eventStore;

  @Test
  void testLoad_ShouldReturnListOfVersionedEvents() {
    // Given
    String aggregateType = "Category";
    Object aggregateId = "category1";

    DomainEvent domainEvent = mock(DomainEvent.class);

    when(serializer.deserialize("CategoryCreated", "{\"categoryId\":\"category1\"}"))
        .thenReturn(domainEvent);

    when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(aggregateId.toString())))
        .thenAnswer(
            invocation -> {
              RowMapper<VersionedEvent> mapper = invocation.getArgument(1);

              ResultSet rs = mock(ResultSet.class);

              when(rs.getInt("version")).thenReturn(1);
              when(rs.getString("event_type")).thenReturn("CategoryCreated");
              when(rs.getString("event_payload")).thenReturn("{\"categoryId\":\"category1\"}");

              VersionedEvent ve = mapper.mapRow(rs, 0);
              return List.of(Objects.requireNonNull(ve));
            });

    // When
    List<VersionedEvent> events = eventStore.load(aggregateType, aggregateId);

    // Then
    assertEquals(1, events.size());
    assertEquals(1, events.getFirst().version());
    assertEquals(domainEvent, events.getFirst().event());
  }

  @Test
  void testLoad_ShouldHandleNoEventsFound() {
    // Given
    String aggregateType = "Category";
    Object aggregateId = "category1";

    // Mock JdbcTemplate to return no rows (empty result set)
    when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(aggregateId.toString())))
        .thenReturn(Collections.emptyList());

    // When
    List<VersionedEvent> events = eventStore.load(aggregateType, aggregateId);

    // Then
    assertTrue(events.isEmpty());
  }

  @Test
  void testAppend_ShouldInsertEventsCorrectly() {
    // Given
    String aggregateType = "Category";
    Object aggregateId = "category1";
    List<DomainEvent> eventsToAppend = List.of(mock(DomainEvent.class), mock(DomainEvent.class));

    // Mock serializer to return event payload JSON
    when(serializer.serialize(any(DomainEvent.class))).thenReturn("{\"categoryId\":\"category1\"}");

    // When
    List<VersionedEvent> versionedEvents =
        eventStore.append(aggregateType, aggregateId, 0, eventsToAppend);

    // Then
    assertEquals(2, versionedEvents.size()); // Two events appended
    assertEquals(1, versionedEvents.get(0).version()); // First event version 1
    assertEquals(2, versionedEvents.get(1).version()); // Second event version 2

    // Verify that jdbcTemplate.update was called to insert the events
    verify(jdbcTemplate, times(2))
        .update(
            anyString(),
            eq(aggregateType),
            eq(aggregateId.toString()),
            anyInt(),
            anyString(),
            anyString());
  }

  @Test
  void testAppend_ShouldThrowExceptionIfVersionMismatch() {
    // Given
    String aggregateType = "Category";
    Object aggregateId = "category1";
    List<DomainEvent> eventsToAppend = List.of(mock(DomainEvent.class));

    // Mock serializer to return event payload JSON
    when(serializer.serialize(any(DomainEvent.class))).thenReturn("{\"categoryId\":\"category1\"}");

    // Simulate a version mismatch scenario, by returning a different version than expected
    doThrow(new org.springframework.dao.DataIntegrityViolationException("Version mismatch"))
        .when(jdbcTemplate)
        .update(
            anyString(),
            eq(aggregateType),
            eq(aggregateId.toString()),
            anyInt(),
            anyString(),
            anyString());

    // When & Then
    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              eventStore.append(
                  aggregateType,
                  aggregateId,
                  1,
                  eventsToAppend); // Expected version is 1, but insertion fails
            });

    assertTrue(exception.getMessage().contains("Version mismatch"));
  }

  @Test
  void testLoad_ShouldThrowExceptionForUnknownAggregateType() {
    // Given
    String unknownAggregateType = "Unknown";
    Object aggregateId = "category1";

    // When & Then
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              eventStore.load(unknownAggregateType, aggregateId);
            });

    assertTrue(exception.getMessage().contains("Unknown aggregateType Unknown"));
  }
}
