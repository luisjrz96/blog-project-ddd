package com.luisjrz96.blog.application.blog.category.command.handler;

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

import com.luisjrz96.blog.application.blog.category.command.ArchiveCategoryCommand;
import com.luisjrz96.blog.application.blog.category.port.CategoryRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.security.Role;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.category.Category;
import com.luisjrz96.blog.domain.blog.category.CategoryId;

class ArchiveCategoryHandlerTest {

  @Test
  void handle_shouldLoadArchiveAndSaveCategory_whenActorIsAdmin() {
    // given
    CategoryRepository repo = mock(CategoryRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);

    ArchiveCategoryHandler handler = new ArchiveCategoryHandler(tx, userProvider, repo);

    CategoryId id = new CategoryId(UUID.randomUUID());
    ArchiveCategoryCommand cmd = new ArchiveCategoryCommand(id);

    Actor admin = new Actor("admin-user", Set.of(Role.ROLE_ADMIN.name()));
    when(tx.executeInTransaction(any()))
        .thenAnswer(
            invocation -> {
              Supplier<?> supplier = invocation.getArgument(0);
              return supplier.get();
            });

    when(userProvider.getCurrentUser()).thenReturn(admin);

    Category category = mock(Category.class);
    when(repo.load(id)).thenReturn(category);

    // when
    handler.handle(cmd);

    // then
    verify(userProvider, times(1)).getCurrentUser();
    verify(repo, times(1)).load(id);
    verify(category, times(1)).archive();
    verify(repo, times(1)).save(category);
    verifyNoMoreInteractions(userProvider, repo, category);
  }

  @Test
  void handle_shouldThrowAndNotSaveCategory_whenActorNotAdmin() {
    // given
    CategoryRepository repo = mock(CategoryRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    TransactionalExecutor tx = mock(TransactionalExecutor.class);
    ArchiveCategoryHandler handler = new ArchiveCategoryHandler(tx, userProvider, repo);

    CategoryId id = new CategoryId(UUID.randomUUID());
    ArchiveCategoryCommand cmd = new ArchiveCategoryCommand(id);

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
