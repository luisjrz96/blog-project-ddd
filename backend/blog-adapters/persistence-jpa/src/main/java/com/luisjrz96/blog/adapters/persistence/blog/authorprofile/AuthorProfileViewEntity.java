package com.luisjrz96.blog.adapters.persistence.blog.authorprofile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Type;

import com.luisjrz96.blog.domain.shared.SocialLink;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "author_profile_view")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthorProfileViewEntity {

  @Id
  @Column(name = "author_id", nullable = false, updatable = false, length = 100)
  private String id;

  @Column(name = "bio_markdown", nullable = false, columnDefinition = "text")
  private String markdown;

  @Column(name = "avatar_url", nullable = false, columnDefinition = "text")
  private String avatarUrl;

  @Column(name = "resume_url", nullable = false, columnDefinition = "text")
  private String resumeUrl;

  @Column(name = "portfolio_url", nullable = false, columnDefinition = "text")
  private String portfolioUrl;

  @Type(JsonType.class)
  @Column(name = "social_links", nullable = false, columnDefinition = "jsonb")
  private List<SocialLink> socialLinks = new ArrayList<>();

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) return false;
    AuthorProfileViewEntity that = (AuthorProfileViewEntity) obj;
    return Objects.equals(that.id, this.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
