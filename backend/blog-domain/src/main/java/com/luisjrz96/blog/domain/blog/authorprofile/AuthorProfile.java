package com.luisjrz96.blog.domain.blog.authorprofile;

import java.time.Instant;
import java.util.List;

import com.luisjrz96.blog.domain.AggregateRoot;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.authorprofile.events.AuthorProfileCreated;
import com.luisjrz96.blog.domain.blog.authorprofile.events.AuthorProfileUpdated;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.exception.DomainException;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.SocialLink;
import com.luisjrz96.blog.domain.shared.Url;

public class AuthorProfile extends AggregateRoot {
  private AuthorId authorId;
  private Markdown bio;
  private ImageUrl avatar;
  private Url resumeUrl;
  private Url portfolioUrl;
  private List<SocialLink> socialLinks;
  private Instant createdAt;
  private Instant updatedAt;

  public AuthorProfile() {}

  public static AuthorProfile create(
      AuthorId authorId,
      Markdown bio,
      ImageUrl avatar,
      Url resumeUrl,
      Url portfolioUrl,
      List<SocialLink> socialLinks) {
    AuthorProfile authorProfile = new AuthorProfile();
    Instant now = Instant.now();
    authorProfile.applyChange(
        new AuthorProfileCreated(authorId, bio, avatar, resumeUrl, portfolioUrl, socialLinks, now));
    return authorProfile;
  }

  public void update(
      Markdown bio,
      ImageUrl avatar,
      Url resumeUrl,
      Url portfolioUrl,
      List<SocialLink> socialLinks) {
    Instant now = Instant.now();
    this.applyChange(
        new AuthorProfileUpdated(
            this.authorId, bio, avatar, resumeUrl, portfolioUrl, socialLinks, now));
  }

  private void apply(AuthorProfileCreated e) {
    this.authorId = e.authorId();
    this.bio = e.bio();
    this.avatar = e.avatar();
    this.resumeUrl = e.resumeUrl();
    this.portfolioUrl = e.portafolioUrl();
    this.socialLinks = e.socialLinks();
    this.createdAt = e.createdAt();
    this.updatedAt = null;
  }

  private void apply(AuthorProfileUpdated e) {
    this.authorId = e.authorId();
    this.bio = e.bio();
    this.avatar = e.avatar();
    this.resumeUrl = e.resumeUrl();
    this.portfolioUrl = e.portafolioUrl();
    this.socialLinks = e.socialLinks();
    this.updatedAt = e.updatedAt();
  }

  @Override
  protected void when(DomainEvent event) {
    switch (event) {
      case AuthorProfileCreated e -> apply(e);
      case AuthorProfileUpdated e -> apply(e);
      default -> throw new DomainException("Unhandled event type for AuthorProfile: " + event);
    }
  }

  public AuthorId getAuthorId() {
    return authorId;
  }

  public Markdown getBio() {
    return bio;
  }

  public ImageUrl getAvatar() {
    return avatar;
  }

  public Url getResumeUrl() {
    return resumeUrl;
  }

  public Url getPortfolioUrl() {
    return portfolioUrl;
  }

  public List<SocialLink> getSocialLinks() {
    return socialLinks;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
