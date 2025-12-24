package com.luisjrz96.blog.adapters.persistence.blog.post;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luisjrz96.blog.adapters.persistence.blog.category.CategoryViewEntity;
import com.luisjrz96.blog.adapters.persistence.blog.tag.TagViewEntity;
import com.luisjrz96.blog.application.blog.post.port.PostViewReader;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostCategoryViewDto;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostTagViewDto;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.post.PostStatus;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;
import com.luisjrz96.blog.domain.shared.Summary;
import com.luisjrz96.blog.domain.shared.Title;

@Service
public class PostViewReaderImpl implements PostViewReader {

  private final PostViewJpaRepository repository;

  public PostViewReaderImpl(PostViewJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PostViewDto> getById(PostId id) {
    Optional<PostViewEntity> postEntity = repository.findById(String.valueOf(id.value()));
    return postEntity.map(this::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PostViewDto> getPage(PageRequest pageRequest) {
    var page = repository.findAll(Pageable.ofSize(pageRequest.size()).withPage(pageRequest.page()));
    return new Page<>(
        page.stream().map(this::toDto).toList(),
        page.getTotalElements(),
        pageRequest.page(),
        pageRequest.size());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PostViewDto> getPageWithStatus(PostStatus status, PageRequest pageRequest) {
    var page =
        repository.findAllByStatus(
            status.name(), Pageable.ofSize(pageRequest.size()).withPage(pageRequest.page()));
    return new Page<>(
        page.stream().map(this::toDto).toList(),
        page.getTotalElements(),
        pageRequest.page(),
        pageRequest.size());
  }

  private PostViewDto toDto(PostViewEntity entity) {
    return new PostViewDto(
        new PostId(UUID.fromString(entity.getId())),
        new AuthorId(UUID.fromString(entity.getAuthorId())),
        new Title(entity.getTitle()),
        new Slug(entity.getSlug()),
        new Summary(entity.getSummary()),
        new Markdown(entity.getBody()),
        PostStatus.valueOf(entity.getStatus()),
        toCategoryViewDto(entity.getCategory()),
        toSetTagViewDto(entity.getTags()),
        new ImageUrl(entity.getCoverImage()),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getPublishedAt(),
        entity.getArchivedAt());
  }

  private PostCategoryViewDto toCategoryViewDto(CategoryViewEntity category) {
    return new PostCategoryViewDto(
        new CategoryId(UUID.fromString(category.getId())), new CategoryName(category.getName()));
  }

  private PostTagViewDto toTagViewDto(TagViewEntity tag) {
    return new PostTagViewDto(new TagId(UUID.fromString(tag.getId())), new TagName(tag.getName()));
  }

  private Set<PostTagViewDto> toSetTagViewDto(Set<TagViewEntity> tagViewEntities) {
    return tagViewEntities.stream().map(this::toTagViewDto).collect(Collectors.toSet());
  }
}
