package com.luisjrz96.blog.adapters.persistence.blog.tag;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.luisjrz96.blog.application.blog.tag.port.TagLookup;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagStatus;

@Service
public class TagLookupImpl implements TagLookup {

  private final TagViewJpaRepository tagViewJpaRepository;

  public TagLookupImpl(TagViewJpaRepository tagViewJpaRepository) {
    this.tagViewJpaRepository = tagViewJpaRepository;
  }

  @Override
  public List<TagId> findActiveTags(List<TagId> tagIds) {
    return tagViewJpaRepository
        .findActiveIds(
            tagIds.stream().map(TagId::value).map(UUID::toString).toList(),
            TagStatus.ACTIVE.toString())
        .stream()
        .map(id -> new TagId(UUID.fromString(id)))
        .toList();
  }
}
