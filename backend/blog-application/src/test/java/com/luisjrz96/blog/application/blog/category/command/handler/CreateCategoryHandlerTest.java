package com.luisjrz96.blog.application.blog.category.command.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.category.command.CreateCategoryCommand;
import com.luisjrz96.blog.application.blog.category.port.CategoryRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.security.Role;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.shared.ImageUrl;

class CreateCategoryHandlerTest {

  @Test
  void handle_shouldCreateAndSaveCategory_whenActorIsAdmin() {
    // given
    CategoryRepository repo = mock(CategoryRepository.class);
    UserProvider userProvider = mock(UserProvider.class);

    Actor admin = new Actor("admin-user", Set.of(Role.ADMIN));
    when(userProvider.getCurrentUser()).thenReturn(admin);

    CreateCategoryHandler handler = new CreateCategoryHandler(userProvider, repo);

    var cmd =
        new CreateCategoryCommand(
            new CategoryName("Backend"), new ImageUrl("https://img/backend.png"));

    // when
    CategoryId returnedId = handler.handle(cmd);

    // then
    verify(userProvider, times(1)).getCurrentUser();

    verify(repo, times(1))
        .save(
            argThat(
                category -> {
                  assertNotNull(category.getId(), "Category should have an id after create()");
                  assertEquals(new CategoryName("Backend"), category.getName());
                  assertEquals(new ImageUrl("https://img/backend.png"), category.getDefaultImage());
                  return true;
                }));

    verifyNoMoreInteractions(repo, userProvider);
    assertNotNull(returnedId);
  }

  @Test
  void handle_shouldThrow_whenActorIsNotAdmin() {
    // given
    CategoryRepository repo = mock(CategoryRepository.class);
    UserProvider userProvider = mock(UserProvider.class);

    Actor viewer = new Actor("viewer-user", Set.of());
    when(userProvider.getCurrentUser()).thenReturn(viewer);

    CreateCategoryHandler handler = new CreateCategoryHandler(userProvider, repo);

    var cmd =
        new CreateCategoryCommand(
            new CategoryName("Backend"), new ImageUrl("https://img/backend.png"));

    // actor WITHOUT admin

    // when / then
    ApplicationUnauthorizedException ex =
        assertThrows(ApplicationUnauthorizedException.class, () -> handler.handle(cmd));

    assertTrue(ex.getMessage().contains("Only admins"), "should indicate lack of admin privileges");

    verify(userProvider, times(1)).getCurrentUser();
    verifyNoInteractions(repo);
  }
}
