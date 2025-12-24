package com.luisjrz96.blog.application.blog.post.command.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.category.port.CategoryLookup;
import com.luisjrz96.blog.application.blog.post.command.UpdatePostCommand;
import com.luisjrz96.blog.application.blog.post.port.PostRepository;
import com.luisjrz96.blog.application.blog.tag.port.TagLookup;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.security.Role;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.blog.post.Post;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Summary;
import com.luisjrz96.blog.domain.shared.Title;

class UpdatePostHandlerTest {

  @Test
  void handle_shouldLoadUpdateAndSavePost_whenActorIsAdmin() {
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
    AuthorId authorId = new AuthorId(UUID.randomUUID());
    Actor admin = new Actor(String.valueOf(authorId.value()), Set.of(Role.ROLE_ADMIN.name()));
    PostId postId = new PostId(UUID.randomUUID());
    Post currentPost =
        Post.create(
            authorId,
            new Title("Sample Post"),
            new Summary("Summary"),
            new Markdown("Blog info"),
            new CategoryId(UUID.randomUUID()),
            List.of(new TagId(UUID.randomUUID())),
            new ImageUrl("https://image.url/sample.png"));
    when(userProvider.getCurrentUser()).thenReturn(admin);
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });
    when(repo.load(postId)).thenReturn(currentPost);

    UpdatePostHandler handler =
        new UpdatePostHandler(tx, userProvider, repo, tagLookup, categoryLookup);

    UpdatePostCommand cmd =
        new UpdatePostCommand(
            postId,
            new Title("Updated Post"),
            new Summary("Updated Summary"),
            new Markdown("Updated Blog info"),
            categoryId,
            List.of(tagId),
            new ImageUrl("https://image.url/updated.png"));

    handler.handle(cmd);

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
  }

  @Test
  void handle_shouldLoadUpdateAndSavePost_whenActorIsNotAdmin() {
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
    AuthorId authorId = new AuthorId(UUID.randomUUID());
    Actor admin = new Actor(String.valueOf(authorId.value()), Set.of(Role.ROLE_USER.name()));
    PostId postId = new PostId(UUID.randomUUID());
    Post currentPost =
        Post.create(
            authorId,
            new Title("Sample Post"),
            new Summary("Summary"),
            new Markdown("Blog info"),
            new CategoryId(UUID.randomUUID()),
            List.of(new TagId(UUID.randomUUID())),
            new ImageUrl("https://image.url/sample.png"));
    when(userProvider.getCurrentUser()).thenReturn(admin);
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });
    when(repo.load(postId)).thenReturn(currentPost);

    UpdatePostHandler handler =
        new UpdatePostHandler(tx, userProvider, repo, tagLookup, categoryLookup);

    UpdatePostCommand cmd =
        new UpdatePostCommand(
            postId,
            new Title("Updated Post"),
            new Summary("Updated Summary"),
            new Markdown("Updated Blog info"),
            categoryId,
            List.of(tagId),
            new ImageUrl("https://image.url/updated.png"));

    assertThrows(ApplicationUnauthorizedException.class, () -> handler.handle(cmd));
  }
}
