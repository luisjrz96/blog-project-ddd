package com.luisjrz96.blog.adapters.persistence.blog.tag;

import java.sql.Timestamp;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.luisjrz96.blog.adapters.persistence.exception.UnknownEventException;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.tag.TagStatus;
import com.luisjrz96.blog.domain.blog.tag.events.TagArchived;
import com.luisjrz96.blog.domain.blog.tag.events.TagCreated;
import com.luisjrz96.blog.domain.blog.tag.events.TagUpdated;

@Service
public class TagProjectionHandler {
  private final JdbcTemplate jdbc;

  public TagProjectionHandler(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public void project(DomainEvent event, int newVersion) {
    switch (event) {
      case TagCreated e -> applyCreated(e, newVersion);
      case TagUpdated e -> applyUpdated(e, newVersion);
      case TagArchived e -> applyArchived(e, newVersion);
      default -> throw new UnknownEventException(event.getClass().getName());
    }
  }

  private void applyCreated(TagCreated e, int newVersion) {
    jdbc.update(
        """
        INSERT INTO tag_view
          (id, name, slug, status, created_at, updated_at, archived_at, last_version_applied)
          VALUES (?, ?, ?, ?, ?, NULL, NULL, ?)
          ON CONFLICT (id) DO NOTHING
        """,
        String.valueOf(e.id().value()),
        e.name().value(),
        e.slug().value(),
        String.valueOf(TagStatus.ACTIVE),
        Timestamp.from(e.createdAt()),
        newVersion);
  }

  private void applyUpdated(TagUpdated e, int newVersion) {
    jdbc.update(
        """
        UPDATE tag_view
          SET name = ?,
            slug = ?,
            updated_at = ?,
            last_version_applied = ?
          WHERE id = ? AND last_version_applied < ?
        """,
        e.name().value(),
        e.slug().value(),
        Timestamp.from(e.updatedAt()),
        newVersion,
        String.valueOf(e.id().value()),
        newVersion);
  }

  private void applyArchived(TagArchived e, int newVersion) {
    jdbc.update(
        """
        UPDATE tag_view
          SET status = 'ARCHIVED',
            archived_at = ?,
              last_version_applied = ?
          WHERE id = ? AND last_version_applied < ?
        """,
        Timestamp.from(e.archivedAt()),
        newVersion,
        String.valueOf(e.id().value()),
        newVersion);
  }
}
