package com.luisjrz96.blog.application.blog.post.command.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.post.command.ArchivePostCommand;
import com.luisjrz96.blog.application.blog.post.port.PostRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.security.Role;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.post.Post;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.shared.AuthorId;

class ArchivePostHandlerTest {

  @Test
  void handle_shouldLoadArchiveAndSavePost_whenActorIsAdmin() {
    // given
    PostRepository repository = mock(PostRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);
    Actor admin = new Actor("99dcd32f-71f2-43f3-b1d3-b4298dfdc829", Set.of(Role.ROLE_ADMIN.name()));
    ArchivePostHandler handler = new ArchivePostHandler(tx, userProvider, repository);
    ArchivePostCommand archivePostCommand = new ArchivePostCommand(new PostId(UUID.randomUUID()));
    PostId postId = archivePostCommand.id();
    Post post = mock(Post.class);

    when(post.getAuthorId()).thenReturn(new AuthorId(UUID.fromString(admin.userId())));
    when(repository.load(archivePostCommand.id())).thenReturn(post);
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    when(userProvider.getCurrentUser()).thenReturn(admin);
    when(repository.load(postId)).thenReturn(post);
    handler.handle(archivePostCommand);

    // then
    verify(userProvider, times(1)).getCurrentUser();
    verify(repository, times(1)).load(postId);
    verify(post, times(1)).archive();
    verify(repository, times(1)).save(post);
    verifyNoMoreInteractions(userProvider, repository);
  }

  @Test
  void handle_shouldThrowAndNotSave_whenActorNotAdmin() {
    // given
    PostRepository repository = mock(PostRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);
    Actor admin = new Actor("99dcd32f-71f2-43f3-b1d3-b4298dfdc829", Set.of(Role.ROLE_USER.name()));
    ArchivePostHandler handler = new ArchivePostHandler(tx, userProvider, repository);
    ArchivePostCommand archivePostCommand = new ArchivePostCommand(new PostId(UUID.randomUUID()));
    Post post = mock(Post.class);

    when(post.getAuthorId()).thenReturn(new AuthorId(UUID.fromString(admin.userId())));
    when(repository.load(archivePostCommand.id())).thenReturn(post);
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    when(userProvider.getCurrentUser()).thenReturn(admin);

    Assertions.assertThrows(
        ApplicationUnauthorizedException.class, () -> handler.handle(archivePostCommand));
  }
}
