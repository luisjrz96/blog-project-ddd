package com.luisjrz96.blog.application.blog.tag.port;

import com.luisjrz96.blog.domain.blog.tag.Tag;
import com.luisjrz96.blog.domain.blog.tag.TagId;

public interface TagRepository {
  Tag load(TagId id);

  void save(Tag tag);
}
