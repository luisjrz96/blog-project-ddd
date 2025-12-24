package com.luisjrz96.blog.adapters.security.bridge;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.shared.security.Actor;

class KeycloakAuthorizationServiceTest {

  private final KeycloakAuthorizationService service = new KeycloakAuthorizationService();

  @Test
  void canManageTaxonomy_shouldReturnFalse_whenUserIsNull() {
    assertFalse(service.canManageTaxonomy(null));
  }

  @Test
  void canManageTaxonomy_shouldReturnTrue_whenUserIsAdmin() {
    Actor user = mock(Actor.class);
    when(user.isAdmin()).thenReturn(true);

    assertTrue(service.canManageTaxonomy(user));
    verify(user).isAdmin();
  }

  @Test
  void canManageTaxonomy_shouldReturnFalse_whenUserIsNotAdmin() {
    Actor user = mock(Actor.class);
    when(user.isAdmin()).thenReturn(false);

    assertFalse(service.canManageTaxonomy(user));
    verify(user).isAdmin();
  }

  @Test
  void canManageTaxonomy_shouldReturnFalse_whenUserIsNonNullButIsAdminThrows() {
    Actor user = mock(Actor.class);
    when(user.isAdmin()).thenThrow(new RuntimeException("boom"));
    assertThrows(RuntimeException.class, () -> service.canManageTaxonomy(user));
  }
}
