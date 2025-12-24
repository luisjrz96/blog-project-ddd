package com.luisjrz96.blog.application.blog.post.query.handler;

import com.luisjrz96.blog.application.blog.post.port.PostViewReader;
import com.luisjrz96.blog.application.blog.post.query.PostsPageQuery;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostViewDto;
import com.luisjrz96.blog.application.shared.Page;

public class GetPostsPageHandler {

  private final PostViewReader reader;

  public GetPostsPageHandler(PostViewReader reader) {
    this.reader = reader;
  }

  public Page<PostViewDto> handle(PostsPageQuery query) {

    if (query.status() != null) {
      return reader.getPageWithStatus(query.status(), query.pageRequest());
    }
    return reader.getPage(query.pageRequest());
  }
}
