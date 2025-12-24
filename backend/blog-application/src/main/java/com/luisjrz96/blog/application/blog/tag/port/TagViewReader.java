package com.luisjrz96.blog.application.blog.tag.port;

import java.util.Optional;

import com.luisjrz96.blog.application.blog.tag.query.TagViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagStatus;

public interface TagViewReader {
  Page<TagViewDto> getPageWithStatus(TagStatus status, PageRequest pageRequest);

  Page<TagViewDto> getPage(PageRequest pageRequest);

  Optional<TagViewDto> getById(TagId id);
}
