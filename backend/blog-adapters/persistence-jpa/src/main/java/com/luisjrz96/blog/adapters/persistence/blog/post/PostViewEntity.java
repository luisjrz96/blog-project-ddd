package com.luisjrz96.blog.adapters.persistence.blog.post;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.luisjrz96.blog.adapters.persistence.blog.category.CategoryViewEntity;
import com.luisjrz96.blog.adapters.persistence.blog.tag.TagViewEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_view")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostViewEntity {

  @Id private String id;

  @Column(name = "author_id", nullable = false)
  private String authorId;

  @Column(name = "title", nullable = false, columnDefinition = "text")
  private String title;

  @Column(name = "slug", nullable = false, columnDefinition = "text")
  private String slug;

  @Column(name = "summary", nullable = false, columnDefinition = "text")
  private String summary;

  @Column(name = "body_markdown", nullable = false, columnDefinition = "text")
  private String body;

  @Column(name = "cover_image", nullable = false, columnDefinition = "text")
  private String coverImage;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryViewEntity category;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "post_view_tags",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<TagViewEntity> tags = new HashSet<>();

  @Column(name = "status", nullable = false, columnDefinition = "text")
  private String status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "published_at")
  private Instant publishedAt;

  @Column(name = "archived_at")
  private Instant archivedAt;

  @Column(name = "last_version_applied", nullable = false)
  private Integer lastVersionApplied;

  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) return false;
    PostViewEntity that = (PostViewEntity) obj;
    return Objects.equals(that.id, this.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
