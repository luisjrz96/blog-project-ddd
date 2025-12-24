package com.luisjrz96.blog.application.blog.tag.command.handler;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.tag.command.ArchiveTagCommand;
import com.luisjrz96.blog.application.blog.tag.port.TagRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.security.Role;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.tag.Tag;
import com.luisjrz96.blog.domain.blog.tag.TagId;

class ArchiveTagHandlerTest {

  @Test
  void handle_shouldLoadArchiveAndSaveTag_whenActorIsAdmin() {
    // given
    TagRepository repo = mock(TagRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);

    ArchiveTagHandler handler = new ArchiveTagHandler(tx, userProvider, repo);

    TagId id = new TagId(UUID.randomUUID());
    ArchiveTagCommand cmd = new ArchiveTagCommand(id);

    Actor admin = new Actor("admin-user", Set.of(Role.ROLE_ADMIN.name()));
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    when(userProvider.getCurrentUser()).thenReturn(admin);

    Tag tag = mock(Tag.class);
    when(repo.load(id)).thenReturn(tag);

    // when
    handler.handle(cmd);

    // then
    verify(userProvider, times(1)).getCurrentUser();
    verify(repo, times(1)).load(id);
    verify(tag, times(1)).archive();
    verify(repo, times(1)).save(tag);
    verifyNoMoreInteractions(userProvider, repo, tag);
  }

  @Test
  void handle_shouldThrowAndNotSaveTag_whenActorNotAdmin() {
    // given
    TagRepository repo = mock(TagRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);
    ArchiveTagHandler handler = new ArchiveTagHandler(tx, userProvider, repo);

    TagId id = new TagId(UUID.randomUUID());
    ArchiveTagCommand cmd = new ArchiveTagCommand(id);

    Actor viewer = new Actor("viewer-user", Set.of());
    when(userProvider.getCurrentUser()).thenReturn(viewer);
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    // when / then
    ApplicationUnauthorizedException ex =
        assertThrows(ApplicationUnauthorizedException.class, () -> handler.handle(cmd));

    assertTrue(ex.getMessage().contains("Only admins"));

    verify(userProvider, times(1)).getCurrentUser();
    verifyNoInteractions(repo);
  }
}
