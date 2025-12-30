package com.luisjrz96.blog.application.blog.authorprofile.query.handler;

import com.luisjrz96.blog.application.blog.authorprofile.port.AuthorProfileViewReader;
import com.luisjrz96.blog.application.blog.authorprofile.query.AuthorProfileViewDto;
import com.luisjrz96.blog.application.blog.authorprofile.query.GetAuthorProfileByIdQuery;
import com.luisjrz96.blog.application.shared.error.NotFoundException;

public class GetAuthorProfileByIdHandler {

  private final AuthorProfileViewReader reader;

  public GetAuthorProfileByIdHandler(AuthorProfileViewReader reader) {
    this.reader = reader;
  }

  public AuthorProfileViewDto handle(GetAuthorProfileByIdQuery query) {
    return reader
        .findById(query.id())
        .orElseThrow(
            () ->
                new NotFoundException(
                    String.format(
                        "profile for author with id %s not found", query.id().value().toString())));
  }
}
