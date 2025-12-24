package com.luisjrz96.blog.adapters.persistence.blog.category;

import java.sql.Timestamp;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.luisjrz96.blog.adapters.persistence.exception.UnknownEventException;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;
import com.luisjrz96.blog.domain.blog.category.events.CategoryArchived;
import com.luisjrz96.blog.domain.blog.category.events.CategoryCreated;
import com.luisjrz96.blog.domain.blog.category.events.CategoryUpdated;

@Service
public class CategoryProjectionHandler {

  private final JdbcTemplate jdbc;

  public CategoryProjectionHandler(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public void project(DomainEvent event, int newVersion) {
    switch (event) {
      case CategoryCreated e -> applyCreated(e, newVersion);
      case CategoryUpdated e -> applyUpdated(e, newVersion);
      case CategoryArchived e -> applyArchived(e, newVersion);
      default -> throw new UnknownEventException(event.getClass().getName());
    }
  }

  private void applyCreated(CategoryCreated e, int newVersion) {
    jdbc.update(
        """
            INSERT INTO category_view
                (id, name, slug, default_image, status, created_at, updated_at, archived_at, last_version_applied)
                VALUES (?, ?, ?, ?, ?, ?, NULL, NULL, ?)
                ON CONFLICT (id) DO NOTHING
            """,
        String.valueOf(e.id().value()),
        e.name().value(),
        e.slug().value(),
        e.defaultImage().value(),
        CategoryStatus.ACTIVE.name(),
        Timestamp.from(e.createdAt()),
        newVersion);
  }

  private void applyUpdated(CategoryUpdated e, int newVersion) {
    jdbc.update(
        """
            UPDATE category_view
                SET name = ?,
                    slug = ?,
                    default_image = ?,
                    updated_at = ?,
                    last_version_applied = ?
                WHERE id = ?
                AND last_version_applied < ?
            """,
        e.name().value(),
        e.slug().value(),
        e.defaultImage().value(),
        Timestamp.from(e.updatedAt()),
        newVersion,
        String.valueOf(e.id().value()),
        newVersion);
  }

  private void applyArchived(CategoryArchived e, int newVersion) {
    jdbc.update(
        """
            UPDATE category_view
                SET status = 'ARCHIVED',
                    archived_at = ?,
                    last_version_applied = ?
                WHERE id = ?
                AND last_version_applied < ?
            """,
        Timestamp.from(e.archivedAt()),
        newVersion,
        String.valueOf(e.id().value()),
        newVersion);
  }
}
