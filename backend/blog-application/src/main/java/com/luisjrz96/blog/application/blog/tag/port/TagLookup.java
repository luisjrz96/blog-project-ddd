package com.luisjrz96.blog.application.blog.tag.port;

import java.util.List;

import com.luisjrz96.blog.domain.blog.tag.TagId;

public interface TagLookup {
  List<TagId> findActiveTags(List<TagId> tagIds);
}
