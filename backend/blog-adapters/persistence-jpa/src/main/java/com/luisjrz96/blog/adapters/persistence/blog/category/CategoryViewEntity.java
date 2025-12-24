package com.luisjrz96.blog.adapters.persistence.blog.category;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category_view")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryViewEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = 100)
  private String id;

  @Column(name = "name", nullable = false, columnDefinition = "text")
  private String name;

  @Column(name = "slug", nullable = false, columnDefinition = "text")
  private String slug;

  @Column(name = "default_image", columnDefinition = "text")
  private String defaultImage;

  @Column(name = "status", nullable = false, length = 20)
  private String status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "archived_at")
  private Instant archivedAt;

  @Column(name = "last_version_applied", nullable = false)
  private Integer lastVersionApplied;

  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) return false;
    CategoryViewEntity that = (CategoryViewEntity) obj;
    return Objects.equals(that.id, this.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
