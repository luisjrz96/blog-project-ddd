package com.luisjrz96.blog.adapters.persistence.blog.tag;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.luisjrz96.blog.application.blog.tag.port.TagViewReader;
import com.luisjrz96.blog.application.blog.tag.query.TagViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;
import com.luisjrz96.blog.domain.blog.tag.TagStatus;
import com.luisjrz96.blog.domain.shared.Slug;

@Service
public class TagViewReaderImpl implements TagViewReader {

  private final TagViewJpaRepository jpaRepository;

  public TagViewReaderImpl(TagViewJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Page<TagViewDto> getPageWithStatus(TagStatus status, PageRequest pageRequest) {
    var page =
        jpaRepository.findAllByStatus(
            String.valueOf(status),
            Pageable.ofSize(pageRequest.size()).withPage(pageRequest.page()));
    return new Page<>(
        page.stream().map(this::toDto).toList(),
        page.getTotalElements(),
        pageRequest.page(),
        pageRequest.size());
  }

  @Override
  public Page<TagViewDto> getPage(PageRequest pageRequest) {
    var page =
        jpaRepository.findAll(Pageable.ofSize(pageRequest.size()).withPage(pageRequest.page()));
    return new Page<>(
        page.stream().map(this::toDto).toList(),
        page.getTotalElements(),
        pageRequest.page(),
        pageRequest.size());
  }

  @Override
  public Optional<TagViewDto> getById(TagId id) {
    Optional<TagViewEntity> tagViewEntity = jpaRepository.findById(String.valueOf(id.value()));
    return tagViewEntity.map(this::toDto);
  }

  private TagViewDto toDto(TagViewEntity e) {
    return new TagViewDto(
        new TagId(UUID.fromString(e.getId())),
        new TagName(e.getName()),
        new Slug(e.getSlug()),
        TagStatus.valueOf(e.getStatus()),
        e.getCreatedAt(),
        e.getUpdatedAt(),
        e.getArchivedAt());
  }
}
