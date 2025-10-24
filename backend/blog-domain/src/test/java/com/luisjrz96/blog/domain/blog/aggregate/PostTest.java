package com.luisjrz96.blog.domain.blog.aggregate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.luisjrz96.blog.domain.blog.vos.CategoryId;
import com.luisjrz96.blog.domain.blog.vos.CategoryName;
import com.luisjrz96.blog.domain.blog.vos.Markdown;
import com.luisjrz96.blog.domain.blog.vos.PostId;
import com.luisjrz96.blog.domain.blog.vos.TagId;
import com.luisjrz96.blog.domain.blog.vos.TagName;
import com.luisjrz96.blog.domain.shared.vos.AuthorId;
import com.luisjrz96.blog.domain.shared.vos.ImageUrl;
import com.luisjrz96.blog.domain.shared.vos.Slug;
import com.luisjrz96.blog.domain.shared.vos.Summary;
import com.luisjrz96.blog.domain.shared.vos.Title;
import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.domain.blog.entity.Category;
import com.luisjrz96.blog.domain.blog.entity.Tag;
import com.luisjrz96.blog.domain.exception.DomainException;

public class PostTest {

  @Test
  public void testValidPost() {
    Post post =
        new Post(
            new PostId(UUID.randomUUID()),
            new AuthorId(UUID.randomUUID()),
            new Title("Hello dear world"),
            new Summary("summary"),
            new Markdown("hello \n dear \nworld"),
            new Category(
                new CategoryId(UUID.randomUUID()),
                new CategoryName("happy"),
                new Slug("happy"),
                new ImageUrl("https://blog.com/images/cat1.png")),
            List.of(
                new Tag(new TagId(UUID.randomUUID()), new TagName("coding"), new Slug("coding"))),
            new ImageUrl("https://blog.com/images/post1.png"),
            Instant.now());

    assertEquals("hello-dear-world", post.getSlug().value());
  }

  @Test
  public void testInvalidPost_NullPostId() {
    assertThrows(
        DomainException.class,
        () -> {
          new Post(
              null,
              new AuthorId(UUID.randomUUID()),
              new Title("Hello world"),
              new Summary("summary"),
              new Markdown("hello \n dear \nworld"),
              new Category(
                  new CategoryId(UUID.randomUUID()),
                  new CategoryName("happy"),
                  new Slug("happy"),
                  new ImageUrl("https://blog.com/images/cat1.png")),
              List.of(
                  new Tag(new TagId(UUID.randomUUID()), new TagName("coding"), new Slug("coding"))),
              new ImageUrl("https://blog.com/images/post1.png"),
              Instant.now());
        });
  }

  @Test
  public void testInvalidPost_EmptyTitle() {
    assertThrows(
        DomainException.class,
        () -> {
          new Post(
              new PostId(UUID.randomUUID()),
              new AuthorId(UUID.randomUUID()),
              new Title(""),
              new Summary("summary"),
              new Markdown("hello \n dear \nworld"),
              new Category(
                  new CategoryId(UUID.randomUUID()),
                  new CategoryName("happy"),
                  new Slug("happy"),
                  new ImageUrl("https://blog.com/images/cat1.png")),
              List.of(
                  new Tag(new TagId(UUID.randomUUID()), new TagName("coding"), new Slug("coding"))),
              new ImageUrl("https://blog.com/images/post1.png"),
              Instant.now());
        });
  }
}
