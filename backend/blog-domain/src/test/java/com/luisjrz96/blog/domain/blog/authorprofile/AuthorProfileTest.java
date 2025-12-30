package com.luisjrz96.blog.domain.blog.authorprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.authorprofile.events.AuthorProfileCreated;
import com.luisjrz96.blog.domain.blog.authorprofile.events.AuthorProfileUpdated;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.SocialLink;
import com.luisjrz96.blog.domain.shared.SocialNetwork;
import com.luisjrz96.blog.domain.shared.Url;

class AuthorProfileTest {

  private AuthorId someAuthorId() {
    return new AuthorId(UUID.randomUUID());
  }

  private Markdown someBio() {
    return new Markdown("Hi, this is a new java post");
  }

  private ImageUrl someAvatar() {
    return new ImageUrl("https://blog.com/images/avatar.png");
  }

  private Url someResumeUrl() {
    return new Url("https://blog.com/resumes/resume.pdf");
  }

  private Url somePortfolioUrl() {
    return new Url("https://github.com/sjakjsaks");
  }

  private List<SocialLink> someSocialLinks() {
    return List.of(
        new SocialLink(SocialNetwork.GITHUB, new Url("https://github.com/jhondoe")),
        new SocialLink(SocialNetwork.LINKEDIN, new Url("https://linkedin.com/in/jhondoe")));
  }

  @Test
  void create_shouldInitializeProfile_andEmitAuthorProfileCreated() {
    // given
    AuthorId authorId = someAuthorId();
    Markdown bio = someBio();
    ImageUrl avatar = someAvatar();
    Url resumeUrl = someResumeUrl();
    Url portfolioUrl = somePortfolioUrl();
    List<SocialLink> links = someSocialLinks();

    // when
    AuthorProfile profile =
        AuthorProfile.create(authorId, bio, avatar, resumeUrl, portfolioUrl, links);

    // then: state in aggregate
    assertEquals(authorId, profile.getAuthorId());
    assertEquals(bio, profile.getBio());
    assertEquals(avatar, profile.getAvatar());
    assertEquals(resumeUrl, profile.getResumeUrl());
    assertEquals(portfolioUrl, profile.getPortfolioUrl());
    assertEquals(links, profile.getSocialLinks());

    assertNotNull(profile.getCreatedAt(), "createdAt should be set on creation");
    assertNull(profile.getUpdatedAt(), "updatedAt should be null for a fresh profile");

    // then: uncommitted events
    var events = profile.getUncommittedEvents();
    assertEquals(1, events.size(), "create() should emit exactly one event");
    assertInstanceOf(AuthorProfileCreated.class, events.getFirst());

    var created = (AuthorProfileCreated) events.getFirst();
    assertEquals(authorId, created.authorId());
    assertEquals(bio, created.bio());
    assertEquals(avatar, created.avatar());
    assertEquals(resumeUrl, created.resumeUrl());
    assertEquals(portfolioUrl, created.portfolioUrl());
    assertEquals(links, created.socialLinks());
    assertNotNull(created.createdAt(), "AuthorProfileCreated should carry createdAt timestamp");
  }

  @Test
  void update_shouldEmitAuthorProfileUpdated_andMutateStateThroughEvent() {
    // given: we first create a profile
    AuthorProfile profile =
        AuthorProfile.create(
            someAuthorId(),
            someBio(),
            someAvatar(),
            someResumeUrl(),
            somePortfolioUrl(),
            someSocialLinks());

    // clear uncommitted events from creation so we isolate the update behavior
    profile.markEventsAsCommitted();

    // when: we simulate an update use case
    Markdown newBio = new Markdown("Updated bio text.");
    ImageUrl newAvatar = new ImageUrl("https://cdn.example.com/new-avatar.png");
    Url newResume = new Url("https://example.com/resume_v2.pdf");
    Url newPortfolio = new Url("https://portfolio.example.com");
    List<SocialLink> newLinks =
        List.of(
            new SocialLink(SocialNetwork.YOUTUBE, new Url("https://youtube.com/jhondoe")),
            new SocialLink(SocialNetwork.GITHUB, new Url("https://github.com/jhondoe")));

    Instant beforeUpdate = Instant.now();

    // simulate domain method:
    profile.applyChange(
        new AuthorProfileUpdated(
            profile.getAuthorId(),
            newBio,
            newAvatar,
            newResume,
            newPortfolio,
            newLinks,
            beforeUpdate));

    // then: aggregate state got updated via apply(AuthorProfileUpdated)
    assertEquals(newBio, profile.getBio());
    assertEquals(newAvatar, profile.getAvatar());
    assertEquals(newResume, profile.getResumeUrl());
    assertEquals(newPortfolio, profile.getPortfolioUrl());
    assertEquals(newLinks, profile.getSocialLinks());
    assertEquals(beforeUpdate, profile.getUpdatedAt(), "updatedAt should come from event");

    // then: uncommitted events now contain exactly that update event
    var events = profile.getUncommittedEvents();
    assertEquals(1, events.size(), "update should emit exactly one event");
    assertInstanceOf(AuthorProfileUpdated.class, events.getFirst());

    AuthorProfileUpdated updated = (AuthorProfileUpdated) events.getFirst();
    assertEquals(profile.getAuthorId(), updated.authorId());
    assertEquals(newBio, updated.bio());
    assertEquals(newAvatar, updated.avatar());
    assertEquals(newResume, updated.resumeUrl());
    assertEquals(newPortfolio, updated.portfolioUrl());
    assertEquals(newLinks, updated.socialLinks());
    assertEquals(beforeUpdate, updated.updatedAt());
  }

  @Test
  void canRehydrateFromHistory_usingReplayEvent() {
    // given: pretend the event store returned historical events
    AuthorId authorId = someAuthorId();
    Markdown originalBio = someBio();
    ImageUrl originalAvatar = someAvatar();
    Url originalResume = someResumeUrl();
    Url originalPortfolio = somePortfolioUrl();
    List<SocialLink> originalLinks = someSocialLinks();
    Instant createdAt = Instant.parse("2025-10-24T10:00:00Z");

    AuthorProfileCreated created =
        new AuthorProfileCreated(
            authorId,
            originalBio,
            originalAvatar,
            originalResume,
            originalPortfolio,
            originalLinks,
            createdAt);

    Markdown newBio = new Markdown("Bio after update");
    ImageUrl newAvatar = new ImageUrl("https://cdn.example.com/new-avatar.png");
    Url newResume = new Url("https://example.com/resume_v2.pdf");
    Url newPortfolio = new Url("https://new-portfolio.example.com");
    List<SocialLink> newLinks =
        List.of(
            new SocialLink(SocialNetwork.GITHUB, new Url("https://github.com/jhondoe")),
            new SocialLink(SocialNetwork.YOUTUBE, new Url("https://youtube.com/jhondoe")));
    Instant updatedAt = Instant.parse("2025-10-24T12:30:00Z");

    AuthorProfileUpdated updated =
        new AuthorProfileUpdated(
            authorId, newBio, newAvatar, newResume, newPortfolio, newLinks, updatedAt);

    List<DomainEvent> history = List.of(created, updated);

    // when: we rebuild the aggregate from scratch by replaying the history
    AuthorProfile profile = new AuthorProfile();
    int version = 0;
    for (DomainEvent e : history) {
      profile.replayEvent(e);
      version++;
    }
    profile.setVersion(version);

    // then: final state should reflect the LAST event
    assertEquals(authorId, profile.getAuthorId());
    assertEquals(newBio, profile.getBio());
    assertEquals(newAvatar, profile.getAvatar());
    assertEquals(newResume, profile.getResumeUrl());
    assertEquals(newPortfolio, profile.getPortfolioUrl());
    assertEquals(newLinks, profile.getSocialLinks());

    assertEquals(createdAt, profile.getCreatedAt(), "createdAt should come from first event");
    assertEquals(updatedAt, profile.getUpdatedAt(), "updatedAt should come from last update event");

    assertEquals(version, profile.getVersion(), "version should equal number of applied events");

    // and since we used replayEvent(), there should be NO new uncommitted events
    assertTrue(
        profile.getUncommittedEvents().isEmpty(),
        "rehydration should not produce uncommitted events");
  }
}
