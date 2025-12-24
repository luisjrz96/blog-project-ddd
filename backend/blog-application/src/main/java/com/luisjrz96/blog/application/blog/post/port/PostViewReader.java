package com.luisjrz96.blog.application.blog.post.port;

import java.util.Optional;

import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.post.PostStatus;

public interface PostViewReader {

  Optional<PostViewDto> getById(PostId id);

  Page<PostViewDto> getPage(PageRequest pageRequest);

  Page<PostViewDto> getPageWithStatus(PostStatus status, PageRequest pageRequest);
}
