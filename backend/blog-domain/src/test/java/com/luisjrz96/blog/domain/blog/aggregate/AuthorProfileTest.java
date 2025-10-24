package com.luisjrz96.blog.domain.blog.aggregate;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.domain.exception.DomainException;
import com.luisjrz96.blog.domain.blog.vos.Markdown;
import com.luisjrz96.blog.domain.blog.vos.SocialLink;
import com.luisjrz96.blog.domain.blog.vos.SocialNetwork;
import com.luisjrz96.blog.domain.shared.vos.AuthorId;
import com.luisjrz96.blog.domain.shared.vos.ImageUrl;
import com.luisjrz96.blog.domain.shared.vos.Url;

public class AuthorProfileTest {

  @Test
  public void testValidAuthorProfile() {
    AuthorProfile authorProfile =
        new AuthorProfile(
            new AuthorId(UUID.randomUUID()),
            new Markdown("hello"),
            new ImageUrl("https://blog.com/image/img1.png"),
            new Url("https://blog.com//resume/kjszksj.pdf"),
            new Url("https://blog.com/portfolio/jakjsk"),
            List.of(new SocialLink(SocialNetwork.GITHUB, new Url("https://github.com/kjksdj"))));
  }

  @Test
  public void testInvalidAuthorProfile_InvalidAuthorId() {
    assertThrows(
        DomainException.class,
        () -> {
          new AuthorProfile(
              null,
              new Markdown("hello"),
              new ImageUrl("https://blog.com/image/img1.png"),
              new Url("https://blog.com//resume/kjszksj.pdf"),
              new Url("https://blog.com/portfolio/jakjsk"),
              List.of(new SocialLink(SocialNetwork.GITHUB, new Url("https://github.com/kjksdj"))));
        });
  }

  @Test
  public void testInvalidAuthorProfile_InvalidImageUrl() {
    assertThrows(
        DomainException.class,
        () -> {
          new AuthorProfile(
              new AuthorId(UUID.randomUUID()),
              new Markdown("hello"),
              new ImageUrl("https://blog.com/image/img1.invalidformat"),
              new Url("https://blog.com//resume/kjszksj.pdf"),
              new Url("https://blog.com/portfolio/jakjsk"),
              List.of(new SocialLink(SocialNetwork.GITHUB, new Url("https://github.com/kjksdj"))));
        });
  }
}
