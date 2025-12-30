package com.luisjrz96.blog.application.blog.authorprofile;

import com.luisjrz96.blog.application.blog.authorprofile.command.CreateAuthorProfileCommand;
import com.luisjrz96.blog.application.blog.authorprofile.command.UpdateAuthorProfileCommand;
import com.luisjrz96.blog.application.blog.authorprofile.command.handler.CreateAuthorProfileHandler;
import com.luisjrz96.blog.application.blog.authorprofile.command.handler.UpdateAuthorProfileHandler;
import com.luisjrz96.blog.application.blog.authorprofile.query.AuthorProfileViewDto;
import com.luisjrz96.blog.application.blog.authorprofile.query.GetAuthorProfileByIdQuery;
import com.luisjrz96.blog.application.blog.authorprofile.query.handler.GetAuthorProfileByIdHandler;

public class AuthorProfileService {

  private final CreateAuthorProfileHandler createAuthorProfileHandler;
  private final UpdateAuthorProfileHandler updateAuthorProfileHandler;
  private final GetAuthorProfileByIdHandler getAuthorProfileByIdHandler;

  public AuthorProfileService(
      CreateAuthorProfileHandler createAuthorProfileHandler,
      UpdateAuthorProfileHandler updateAuthorProfileHandler,
      GetAuthorProfileByIdHandler getAuthorProfileByIdHandler) {
    this.createAuthorProfileHandler = createAuthorProfileHandler;
    this.updateAuthorProfileHandler = updateAuthorProfileHandler;
    this.getAuthorProfileByIdHandler = getAuthorProfileByIdHandler;
  }

  public void create(CreateAuthorProfileCommand cmd) {
    createAuthorProfileHandler.handle(cmd);
  }

  public void update(UpdateAuthorProfileCommand cmd) {
    updateAuthorProfileHandler.handle(cmd);
  }

  public AuthorProfileViewDto getById(GetAuthorProfileByIdQuery query) {
    return getAuthorProfileByIdHandler.handle(query);
  }
}
