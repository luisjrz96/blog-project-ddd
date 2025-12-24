package com.luisjrz96.blog.adapters.persistence.eventstore;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.luisjrz96.blog.application.shared.VersionedEvent;
import com.luisjrz96.blog.application.shared.port.EventStore;
import com.luisjrz96.blog.domain.DomainEvent;

@Service
public class JdbcEventStore implements EventStore {

  private final JdbcTemplate jdbc;
  private final DomainEventSerializer serializer;

  public JdbcEventStore(JdbcTemplate jdbc, DomainEventSerializer serializer) {
    this.jdbc = jdbc;
    this.serializer = serializer;
  }

  @Override
  public List<VersionedEvent> load(String aggregateType, Object aggregateId) {
    String tableName = resolvePartitionTable(aggregateType);
    String sql =
        """
        SELECT version, event_type, event_payload
            FROM %s
            WHERE aggregate_id = ?
            ORDER BY version ASC
        """
            .formatted(tableName);

    return jdbc.query(
        sql,
        (rs, rowNum) -> {
          int version = rs.getInt("version");
          String eventType = rs.getString("event_type");
          String payloadJson = rs.getString("event_payload");

          DomainEvent event = serializer.deserialize(eventType, payloadJson);

          return new VersionedEvent(version, event);
        },
        aggregateId.toString());
  }

  @Override
  public List<VersionedEvent> append(
      String aggregateType, Object aggregateId, int expectedVersion, List<DomainEvent> newEvents) {
    String tableName = resolvePartitionTable(aggregateType);

    int version = expectedVersion;
    List<VersionedEvent> assigned = new java.util.ArrayList<>(newEvents.size());

    for (DomainEvent event : newEvents) {
      version++;

      String eventType = event.getClass().getSimpleName();
      String payloadJson = serializer.serialize(event);

      jdbc.update(
          """
          INSERT INTO %s
            (aggregate_type, aggregate_id, version, event_type, event_payload, created_at)
            VALUES (?, ?, ?, ?, CAST(? AS JSONB), NOW())
          """
              .formatted(tableName),
          aggregateType,
          aggregateId.toString(),
          version,
          eventType,
          payloadJson);

      assigned.add(new VersionedEvent(version, event));
    }

    return assigned;
  }

  private String resolvePartitionTable(String aggregateType) {
    return switch (aggregateType) {
      case "Category" -> "domain_events_category";
      case "Post" -> "domain_events_post";
      case "Tag" -> "domain_events_tag";
      case "AuthorProfile" -> "domain_events_author_profile";
      default -> throw new IllegalArgumentException("Unknown aggregateType " + aggregateType);
    };
  }
}
