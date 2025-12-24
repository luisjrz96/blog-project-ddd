package com.luisjrz96.blog.application.blog.tag.query.handler;

import com.luisjrz96.blog.application.blog.tag.port.TagViewReader;
import com.luisjrz96.blog.application.blog.tag.query.GetTagByIdQuery;
import com.luisjrz96.blog.application.blog.tag.query.TagViewDto;
import com.luisjrz96.blog.application.shared.error.NotFoundException;

public class GetTagByIdHandler {

  private final TagViewReader reader;

  public GetTagByIdHandler(TagViewReader reader) {
    this.reader = reader;
  }

  public TagViewDto handle(GetTagByIdQuery query) {
    return reader
        .getById(query.id())
        .orElseThrow(
            () ->
                new NotFoundException(
                    String.format("Tag with id %s not found", query.id().value())));
  }
}
