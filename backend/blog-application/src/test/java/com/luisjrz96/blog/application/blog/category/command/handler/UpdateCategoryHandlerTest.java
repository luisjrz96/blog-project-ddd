package com.luisjrz96.blog.application.blog.category.command.handler;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.category.command.UpdateCategoryCommand;
import com.luisjrz96.blog.application.blog.category.port.CategoryRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.security.Role;
import com.luisjrz96.blog.domain.blog.category.Category;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.shared.ImageUrl;

public class UpdateCategoryHandlerTest {

  @Test
  void handle_shouldLoadUpdateAndSave_whenActorIsAdmin() {
    // given
    CategoryRepository repo = mock(CategoryRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    UpdateCategoryHandler handler = new UpdateCategoryHandler(userProvider, repo);

    CategoryId id = new CategoryId(UUID.randomUUID());
    CategoryName newName = new CategoryName("Platform Engineering");
    ImageUrl newImage = new ImageUrl("https://img/platform.png");

    var cmd = new UpdateCategoryCommand(id, newName, newImage);

    Actor admin = new Actor("admin-user", Set.of(Role.ADMIN));
    when(userProvider.getCurrentUser()).thenReturn(admin);

    Category loadedCategory = mock(Category.class);
    when(repo.load(id)).thenReturn(loadedCategory);

    // when
    handler.handle(cmd);

    // then
    verify(userProvider, times(1)).getCurrentUser();
    verify(repo, times(1)).load(id);
    verify(loadedCategory, times(1)).update(newName, newImage);
    verify(repo, times(1)).save(loadedCategory);
    verifyNoMoreInteractions(userProvider, repo, loadedCategory);
  }

  @Test
  void handle_shouldThrowAndNotSave_whenActorIsNotAdmin() {
    // given
    CategoryRepository repo = mock(CategoryRepository.class);
    UserProvider userProvider = mock(UserProvider.class);
    UpdateCategoryHandler handler = new UpdateCategoryHandler(userProvider, repo);

    var cmd =
        new UpdateCategoryCommand(
            new CategoryId(UUID.randomUUID()),
            new CategoryName("Platform"),
            new ImageUrl("https://img/platform.png"));

    Actor viewer = new Actor("viewer-user", Set.of());
    when(userProvider.getCurrentUser()).thenReturn(viewer);

    // when / then
    ApplicationUnauthorizedException ex =
        assertThrows(ApplicationUnauthorizedException.class, () -> handler.handle(cmd));

    assertTrue(ex.getMessage().contains("Only admins"));

    verify(userProvider, times(1)).getCurrentUser();
    verifyNoInteractions(repo);
  }
}
