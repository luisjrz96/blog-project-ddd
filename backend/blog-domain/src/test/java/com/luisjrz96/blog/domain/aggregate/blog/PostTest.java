package com.luisjrz96.blog.domain.aggregate.blog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.domain.entity.blog.Category;
import com.luisjrz96.blog.domain.entity.blog.Tag;
import com.luisjrz96.blog.domain.exception.DomainException;
import com.luisjrz96.blog.domain.vos.blog.*;
import com.luisjrz96.blog.domain.vos.shared.*;

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
