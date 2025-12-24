package com.luisjrz96.blog.application.blog.tag.command.handler;

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

import java.util.Set;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.tag.command.CreateTagCommand;
import com.luisjrz96.blog.application.blog.tag.port.TagRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.security.Role;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;

class CreateTagHandlerTest {

  @Test
  void handle_shouldCreateAndSaveTag_whenActorIsAdmin() {
    // given
    TagRepository repo = mock(TagRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);

    Actor admin = new Actor("admin-user", Set.of(Role.ROLE_ADMIN.name()));
    when(userProvider.getCurrentUser()).thenReturn(admin);
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    CreateTagHandler handler = new CreateTagHandler(tx, userProvider, repo);

    var cmd = new CreateTagCommand(new TagName("Backend"));

    // when
    TagId returnedId = handler.handle(cmd);

    // then
    verify(userProvider, times(1)).getCurrentUser();

    verify(repo, times(1))
        .save(
            argThat(
                tag -> {
                  assertNotNull(tag.getId(), "Category should have an id after create()");
                  assertEquals(new TagName("Backend"), tag.getName());
                  return true;
                }));

    verifyNoMoreInteractions(repo, userProvider);
    assertNotNull(returnedId);
  }

  @Test
  void handle_shouldThrowException_whenActorIsNotAdmin() {
    // given
    TagRepository repo = mock(TagRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);

    Actor viewer = new Actor("viewer-user", Set.of());
    when(userProvider.getCurrentUser()).thenReturn(viewer);
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    CreateTagHandler handler = new CreateTagHandler(tx, userProvider, repo);

    var cmd = new CreateTagCommand(new TagName("Backend"));

    // actor WITHOUT admin

    // when / then
    ApplicationUnauthorizedException ex =
        assertThrows(ApplicationUnauthorizedException.class, () -> handler.handle(cmd));

    assertTrue(ex.getMessage().contains("Only admins"), "should indicate lack of admin privileges");

    verify(userProvider, times(1)).getCurrentUser();
    verifyNoInteractions(repo);
  }
}
