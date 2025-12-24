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

import com.luisjrz96.blog.application.blog.tag.command.UpdateTagCommand;
import com.luisjrz96.blog.application.blog.tag.port.TagRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.security.Role;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.tag.Tag;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;

class UpdateTagHandlerTest {

  @Test
  void handle_shouldLoadUpdateAndSaveTag_whenActorIsAdmin() {
    // given
    TagRepository repo = mock(TagRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);
    UpdateTagHandler handler = new UpdateTagHandler(tx, userProvider, repo);

    TagId id = new TagId(UUID.randomUUID());
    TagName newName = new TagName("Engineering");

    var cmd = new UpdateTagCommand(id, newName);

    Actor admin = new Actor("admin-user", Set.of(Role.ROLE_ADMIN.name()));
    when(userProvider.getCurrentUser()).thenReturn(admin);
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    Tag loadedTag = mock(Tag.class);
    when(repo.load(id)).thenReturn(loadedTag);

    // when
    handler.handle(cmd);

    // then
    verify(userProvider, times(1)).getCurrentUser();
    verify(repo, times(1)).load(id);
    verify(loadedTag, times(1)).update(newName);
    verify(repo, times(1)).save(loadedTag);
    verifyNoMoreInteractions(userProvider, repo, loadedTag);
  }

  @Test
  void handle_shouldThrowAndNotSaveTag_whenActorIsNotAdmin() {
    // given
    TagRepository repo = mock(TagRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);
    UpdateTagHandler handler = new UpdateTagHandler(tx, userProvider, repo);

    var cmd = new UpdateTagCommand(new TagId(UUID.randomUUID()), new TagName("Platform"));

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
