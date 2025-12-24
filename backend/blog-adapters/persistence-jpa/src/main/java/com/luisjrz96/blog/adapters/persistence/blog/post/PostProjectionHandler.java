package com.luisjrz96.blog.adapters.persistence.blog.post;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.luisjrz96.blog.adapters.persistence.exception.UnknownEventException;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.post.PostStatus;
import com.luisjrz96.blog.domain.blog.post.events.PostArchived;
import com.luisjrz96.blog.domain.blog.post.events.PostCreated;
import com.luisjrz96.blog.domain.blog.post.events.PostPublished;
import com.luisjrz96.blog.domain.blog.post.events.PostUpdated;

@Service
public class PostProjectionHandler {

  private final JdbcTemplate jdbc;

  public PostProjectionHandler(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public void project(DomainEvent event, int newVersion) {
    switch (event) {
      case PostCreated e -> applyCreated(e, newVersion);
      case PostUpdated e -> applyUpdated(e, newVersion);
      case PostPublished e -> applyPublished(e, newVersion);
      case PostArchived e -> applyArchived(e, newVersion);
      default -> throw new UnknownEventException(event.getClass().getName());
    }
  }

  private void applyCreated(PostCreated e, int newVersion) {
    jdbc.update(
        """
        INSERT INTO post_view
          (id, author_id, title, slug, summary, body_markdown, cover_image,
           category_id, status, created_at, updated_at, published_at, archived_at, last_version_applied)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, NULL, ?)
        ON CONFLICT (id) DO NOTHING
        """,
        e.postId().value().toString(),
        e.authorId().value().toString(),
        e.title().value(),
        e.slug().value(),
        e.summary() != null ? e.summary().value() : null,
        e.body() != null ? e.body().value() : null,
        e.coverImage() != null ? e.coverImage().value() : null,
        e.categoryId() != null ? e.categoryId().value().toString() : null,
        PostStatus.DRAFT.name(),
        Timestamp.from(e.createdAt()),
        newVersion);

    syncTagsReplaceAll(e.postId().value().toString(), toStringIds(e.tagIds()));
  }

  private void applyUpdated(PostUpdated e, int newVersion) {
    int updated =
        jdbc.update(
            """
        UPDATE post_view
           SET title        = ?,
               slug         = ?,
               summary      = ?,
               body_markdown= ?,
               cover_image  = ?,
               category_id  = ?,
               updated_at   = ?,
               last_version_applied = ?
         WHERE id = ?
           AND last_version_applied < ?
        """,
            e.title().value(),
            e.slug().value(),
            e.summary() != null ? e.summary().value() : null,
            e.body() != null ? e.body().value() : null,
            e.coverImage() != null ? e.coverImage().value() : null,
            e.categoryId() != null ? String.valueOf(e.categoryId().value()) : null,
            Timestamp.from(e.updatedAt()),
            newVersion,
            String.valueOf(e.postId().value()),
            newVersion);

    if (updated > 0) {
      syncTagsReplaceAll(e.postId().value().toString(), toStringIds(e.tagIds()));
    }
  }

  private void applyPublished(PostPublished e, int newVersion) {
    jdbc.update(
        """
        UPDATE post_view
           SET status = 'PUBLISHED',
               published_at= ?,
               updated_at  = ?,
               last_version_applied = ?
         WHERE id = ?
           AND last_version_applied < ?
        """,
        Timestamp.from(e.publishedAt()),
        Timestamp.from(e.updatedAt()),
        newVersion,
        String.valueOf(e.postId().value()),
        newVersion);
  }

  private void applyArchived(PostArchived e, int newVersion) {
    jdbc.update(
        """
        UPDATE post_view
           SET status = 'ARCHIVED',
               archived_at = ?,
               last_version_applied = ?
         WHERE id = ?
           AND last_version_applied < ?
        """,
        Timestamp.from(e.archivedAt()),
        newVersion,
        String.valueOf(e.postId().value()),
        newVersion);
  }

  private void syncTagsReplaceAll(String postId, List<String> newTagIds) {
    jdbc.update("DELETE FROM post_view_tags WHERE post_id = ?", postId);

    if (newTagIds == null || newTagIds.isEmpty()) return;

    jdbc.batchUpdate(
        "INSERT INTO post_view_tags(post_id, tag_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
        new BatchPreparedStatementSetter() {
          private final List<String> items = new ArrayList<>(newTagIds);

          @Override
          @SuppressWarnings("NullableProblems")
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setString(1, postId);
            ps.setString(2, items.get(i));
          }

          @Override
          public int getBatchSize() {
            return items.size();
          }
        });
  }

  private static List<String> toStringIds(List<com.luisjrz96.blog.domain.blog.tag.TagId> ids) {
    if (ids == null) return List.of();
    return ids.stream().map(id -> id.value().toString()).toList();
  }
}
