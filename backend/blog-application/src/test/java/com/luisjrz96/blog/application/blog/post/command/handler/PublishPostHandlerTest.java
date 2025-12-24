package com.luisjrz96.blog.application.blog.post.command.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.post.command.PublishPostCommand;
import com.luisjrz96.blog.application.blog.post.port.PostRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.security.Role;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.blog.post.Post;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.post.PostStatus;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Summary;
import com.luisjrz96.blog.domain.shared.Title;

class PublishPostHandlerTest {

  @Test
  void handle_shouldLoadArchiveAndSavePost_whenActorIsAdmin() {
    PostRepository repository = mock(PostRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);
    PostId postId = new PostId(UUID.randomUUID());
    AuthorId authorId = new AuthorId(UUID.randomUUID());
    Actor actor = new Actor(String.valueOf(authorId.value()), Set.of(Role.ROLE_ADMIN.name()));
    Post post =
        Post.create(
            authorId,
            new Title("Sample Post"),
            new Summary("Summary"),
            new Markdown("Blog info"),
            new CategoryId(UUID.randomUUID()),
            List.of(new TagId(UUID.randomUUID())),
            new ImageUrl("https://image.url/sample.png"));

    when(userProvider.getCurrentUser()).thenReturn(actor);
    when(repository.load(postId)).thenReturn(post);
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    PublishPostHandler publishPostHandler = new PublishPostHandler(tx, userProvider, repository);
    PublishPostCommand cmd = new PublishPostCommand(postId);
    publishPostHandler.handle(cmd);

    assertEquals(PostStatus.PUBLISHED, post.getStatus());
    assertNotNull(post.getPublishedAt());
  }

  @Test
  void handle_shouldThrowException_whenActorIsNotAdmin() {
    PostRepository repository = mock(PostRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);
    PostId postId = new PostId(UUID.randomUUID());
    AuthorId authorId = new AuthorId(UUID.randomUUID());
    Actor actor = new Actor(String.valueOf(authorId.value()), Set.of(Role.ROLE_USER.name()));
    Post post =
        Post.create(
            authorId,
            new Title("Sample Post"),
            new Summary("Summary"),
            new Markdown("Blog info"),
            new CategoryId(UUID.randomUUID()),
            List.of(new TagId(UUID.randomUUID())),
            new ImageUrl("https://image.url/sample.png"));

    when(userProvider.getCurrentUser()).thenReturn(actor);
    when(repository.load(postId)).thenReturn(post);
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    PublishPostHandler publishPostHandler = new PublishPostHandler(tx, userProvider, repository);
    PublishPostCommand cmd = new PublishPostCommand(postId);
    assertThrows(ApplicationUnauthorizedException.class, () -> publishPostHandler.handle(cmd));
  }
}
