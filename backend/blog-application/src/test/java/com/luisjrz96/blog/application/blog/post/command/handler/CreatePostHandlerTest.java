package com.luisjrz96.blog.application.blog.post.command.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.category.port.CategoryLookup;
import com.luisjrz96.blog.application.blog.post.command.CreatePostCommand;
import com.luisjrz96.blog.application.blog.post.port.PostRepository;
import com.luisjrz96.blog.application.blog.tag.port.TagLookup;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.security.Role;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Summary;
import com.luisjrz96.blog.domain.shared.Title;

class CreatePostHandlerTest {

  @Test
  void handle_shouldCreateAndSavePost_whenActorIsAdmin() {
    // given
    PostRepository repo = mock(PostRepository.class);
    TagLookup tagLookup = mock(TagLookup.class);
    CategoryLookup categoryLookup = mock(CategoryLookup.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);

    CategoryId categoryId = new CategoryId(UUID.randomUUID());
    TagId tagId = new TagId(UUID.randomUUID());

    when(categoryLookup.isActive(categoryId)).thenReturn(true);
    when(tagLookup.findActiveTags(List.of(tagId))).thenReturn(List.of(tagId));
    Actor admin = new Actor(String.valueOf(UUID.randomUUID()), Set.of(Role.ROLE_ADMIN.name()));
    when(userProvider.getCurrentUser()).thenReturn(admin);
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    CreatePostHandler handler =
        new CreatePostHandler(tx, userProvider, repo, tagLookup, categoryLookup);

    var cmd =
        new CreatePostCommand(
            new Title("Backend Development 101"),
            new Summary("An introductory guide to backend development."),
            new Markdown("# Welcome to Backend Development"),
            categoryId,
            List.of(tagId),
            new ImageUrl("https://img/backend-dev.png"));

    // when
    PostId returnedId = handler.handle(cmd);

    // then
    verify(userProvider, times(1)).getCurrentUser();

    verify(repo, times(1))
        .save(
            argThat(
                post -> {
                  assertNotNull(post.getId(), "Category should have an id after create()");
                  assertEquals(cmd.title(), post.getTitle(), "Post title should match command");
                  assertEquals(
                      cmd.summary(), post.getSummary(), "Post summary should match command");
                  assertEquals(cmd.body(), post.getBody(), "Post body should match command");
                  assertEquals(
                      cmd.categoryId(),
                      post.getCategoryId(),
                      "Post categoryId should match command");
                  assertEquals(
                      cmd.tagIds(), post.getTagIds(), "Post tags should exclude invalid tags");
                  assertEquals(
                      cmd.coverImage(),
                      post.getCoverImage(),
                      "Post cover image should match command");
                  return true;
                }));

    verifyNoMoreInteractions(repo, userProvider);
    assertNotNull(returnedId);
  }

  @Test
  void handle_shouldThrowException_whenActorIsNotAdmin() {
    // given
    PostRepository repo = mock(PostRepository.class);
    TagLookup tagLookup = mock(TagLookup.class);
    CategoryLookup categoryLookup = mock(CategoryLookup.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);

    CategoryId categoryId = new CategoryId(UUID.randomUUID());
    TagId tagId = new TagId(UUID.randomUUID());

    when(categoryLookup.isActive(categoryId)).thenReturn(true);
    when(tagLookup.findActiveTags(List.of(tagId))).thenReturn(List.of(tagId));
    Actor user = new Actor(String.valueOf(UUID.randomUUID()), Set.of(Role.ROLE_USER.name()));
    when(userProvider.getCurrentUser()).thenReturn(user);
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    CreatePostHandler handler =
        new CreatePostHandler(tx, userProvider, repo, tagLookup, categoryLookup);

    var cmd =
        new CreatePostCommand(
            new Title("Backend Development 101"),
            new Summary("An introductory guide to backend development."),
            new Markdown("# Welcome to Backend Development"),
            categoryId,
            List.of(tagId),
            new ImageUrl("https://img/backend-dev.png"));
    // actor WITHOUT admin

    // when / then
    ApplicationUnauthorizedException ex =
        assertThrows(ApplicationUnauthorizedException.class, () -> handler.handle(cmd));

    assertTrue(ex.getMessage().contains("Only admins"), "should indicate lack of admin privileges");

    verify(userProvider, times(1)).getCurrentUser();
    verifyNoInteractions(repo);
  }
}
