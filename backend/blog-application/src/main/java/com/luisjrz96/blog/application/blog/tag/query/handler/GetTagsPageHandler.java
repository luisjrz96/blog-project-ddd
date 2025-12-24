package com.luisjrz96.blog.application.blog.tag.query.handler;

import com.luisjrz96.blog.application.blog.tag.port.TagViewReader;
import com.luisjrz96.blog.application.blog.tag.query.TagViewDto;
import com.luisjrz96.blog.application.blog.tag.query.TagsPageQuery;
import com.luisjrz96.blog.application.shared.Page;

public class GetTagsPageHandler {
  private final TagViewReader tagViewReader;

  public GetTagsPageHandler(TagViewReader tagViewReader) {
    this.tagViewReader = tagViewReader;
  }

  public Page<TagViewDto> handle(TagsPageQuery query) {
    if (query.status() != null) {
      return tagViewReader.getPageWithStatus(query.status(), query.pageRequest());
    }
    return tagViewReader.getPage(query.pageRequest());
  }
}
