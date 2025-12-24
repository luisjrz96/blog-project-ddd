package com.luisjrz96.blog.adapters.persistence.blog.category;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.luisjrz96.blog.application.blog.category.port.CategoryViewReader;
import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;

@Service
public class CategoryViewReaderImpl implements CategoryViewReader {

  private final CategoryViewJpaRepository jpaRepository;

  public CategoryViewReaderImpl(CategoryViewJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<CategoryViewDto> getById(CategoryId id) {
    Optional<CategoryViewEntity> entity = jpaRepository.findById(String.valueOf(id.value()));
    return entity.map(this::toDto);
  }

  @Override
  public Page<CategoryViewDto> getPage(PageRequest pageRequest) {
    var page =
        jpaRepository.findAll(Pageable.ofSize(pageRequest.size()).withPage(pageRequest.page()));
    return new Page<>(
        page.stream().map(this::toDto).toList(),
        page.getTotalElements(),
        pageRequest.page(),
        pageRequest.size());
  }

  @Override
  public Page<CategoryViewDto> getPageWithStatus(CategoryStatus status, PageRequest pageRequest) {
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

  private CategoryViewDto toDto(CategoryViewEntity e) {
    return new CategoryViewDto(
        new CategoryId(UUID.fromString(e.getId())),
        new CategoryName(e.getName()),
        new Slug(e.getSlug()),
        new ImageUrl(e.getDefaultImage()),
        CategoryStatus.valueOf(e.getStatus()),
        e.getCreatedAt(),
        e.getUpdatedAt(),
        e.getArchivedAt());
  }
}
