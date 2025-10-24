package com.luisjrz96.blog.domain.portfolio.aggregate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.domain.exception.DomainException;
import com.luisjrz96.blog.domain.portfolio.vos.ProjectId;
import com.luisjrz96.blog.domain.shared.vos.*;

public class ProjectTest {

  @Test
  public void testValidProject() {
    var project =
        new Project(
            new ProjectId(UUID.randomUUID()),
            new AuthorId(UUID.randomUUID()),
            new Title("hello world!"),
            new Summary("this is a summary"),
            new Url("https://github.com/repo"),
            null,
            Collections.emptyList());
    assertEquals("hello-world", project.getSlug().value());
  }

  @Test
  public void testInvalidProject_NullProjectId() {
    assertThrows(
        DomainException.class,
        () -> {
          new Project(
              null,
              new AuthorId(UUID.randomUUID()),
              new Title("hello world!"),
              new Summary("this is a summary"),
              new Url("https://github.com/repo"),
              null,
              Collections.emptyList());
        });
  }

  @Test
  public void testInvalidProject_emptyTitle() {
    assertThrows(
            DomainException.class,
            () -> {
              new Project(
                      null,
                      new AuthorId(UUID.randomUUID()),
                      new Title(""),
                      new Summary("this is a summary"),
                      new Url("https://github.com/repo"),
                      null,
                      Collections.emptyList());
            });
  }
}
