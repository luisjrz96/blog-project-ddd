package com.luisjrz96.blog.application.blog.authorprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.luisjrz96.blog.application.blog.authorprofile.command.CreateAuthorProfileCommand;
import com.luisjrz96.blog.application.blog.authorprofile.command.UpdateAuthorProfileCommand;
import com.luisjrz96.blog.application.blog.authorprofile.command.handler.CreateAuthorProfileHandler;
import com.luisjrz96.blog.application.blog.authorprofile.command.handler.UpdateAuthorProfileHandler;
import com.luisjrz96.blog.application.blog.authorprofile.query.AuthorProfileViewDto;
import com.luisjrz96.blog.application.blog.authorprofile.query.GetAuthorProfileByIdQuery;
import com.luisjrz96.blog.application.blog.authorprofile.query.handler.GetAuthorProfileByIdHandler;

@ExtendWith(MockitoExtension.class)
class AuthorProfileServiceTest {

  @Mock private CreateAuthorProfileHandler createAuthorProfileHandler;
  @Mock private UpdateAuthorProfileHandler updateAuthorProfileHandler;
  @Mock private GetAuthorProfileByIdHandler getAuthorProfileByIdHandler;
  @InjectMocks private AuthorProfileService service;

  @Test
  void shouldDelegateCreateToHandler() {
    CreateAuthorProfileCommand cmd = mock(CreateAuthorProfileCommand.class);
    service.create(cmd);
    verify(createAuthorProfileHandler).handle(cmd);
    verifyNoMoreInteractions(createAuthorProfileHandler);
  }

  @Test
  void shouldDelegateUpdateToHandler() {
    UpdateAuthorProfileCommand cmd = mock(UpdateAuthorProfileCommand.class);
    service.update(cmd);
    verify(updateAuthorProfileHandler).handle(cmd);
    verifyNoMoreInteractions(updateAuthorProfileHandler);
  }

  @Test
  void shouldDelegateGetByIdToHandler() {
    GetAuthorProfileByIdQuery query = mock(GetAuthorProfileByIdQuery.class);
    AuthorProfileViewDto expected = mock(AuthorProfileViewDto.class);
    when(getAuthorProfileByIdHandler.handle(query)).thenReturn(expected);

    AuthorProfileViewDto result = service.getById(query);

    verify(getAuthorProfileByIdHandler).handle(query);
    assertEquals(expected, result);
  }
}
