package com.luisjrz96.blog.application.blog.post.query.handler;

import com.luisjrz96.blog.application.blog.post.port.PostViewReader;
import com.luisjrz96.blog.application.blog.post.query.GetPostByIdQuery;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostViewDto;
import com.luisjrz96.blog.application.shared.error.NotFoundException;

public class GetPostByIdHandler {

  private final PostViewReader reader;

  public GetPostByIdHandler(PostViewReader reader) {
    this.reader = reader;
  }

  public PostViewDto handle(GetPostByIdQuery query) {
    return reader
        .getById(query.id())
        .orElseThrow(
            () ->
                new NotFoundException(
                    String.format("Post with id %s not found", query.id().value())));
  }
}
