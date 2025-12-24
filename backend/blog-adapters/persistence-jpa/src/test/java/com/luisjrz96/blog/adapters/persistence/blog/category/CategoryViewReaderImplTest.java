package com.luisjrz96.blog.adapters.persistence.blog.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;

@ExtendWith(MockitoExtension.class)
class CategoryViewReaderImplTest {

  @Mock private CategoryViewJpaRepository jpaRepository;

  @InjectMocks private CategoryViewReaderImpl reader;

  @Test
  void shouldReturnCategoryById() {
    UUID id = UUID.randomUUID();

    CategoryViewEntity entity = new CategoryViewEntity();
    entity.setId(id.toString());
    entity.setName("Tech");
    entity.setSlug("tech");
    entity.setDefaultImage("https://img/image.jpg");
    entity.setStatus("ACTIVE");
    entity.setCreatedAt(Instant.now());

    when(jpaRepository.findById(id.toString())).thenReturn(Optional.of(entity));

    Optional<CategoryViewDto> result = reader.getById(new CategoryId(id));

    assertTrue(result.isPresent());
    assertEquals("Tech", result.get().name().value());
    assertEquals(CategoryStatus.ACTIVE, result.get().status());
  }

  @Test
  void shouldReturnEmptyWhenCategoryNotFound() {
    UUID id = UUID.randomUUID();

    when(jpaRepository.findById(id.toString())).thenReturn(Optional.empty());

    Optional<CategoryViewDto> result = reader.getById(new CategoryId(id));

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnPagedCategories() {
    CategoryViewEntity entity = new CategoryViewEntity();
    entity.setId(UUID.randomUUID().toString());
    entity.setName("Tech");
    entity.setSlug("tech");
    entity.setDefaultImage("https://img/image.jpg");
    entity.setStatus("ACTIVE");
    entity.setCreatedAt(Instant.now());

    PageRequest request = new PageRequest(0, 10);

    org.springframework.data.domain.Page<CategoryViewEntity> springPage =
        new PageImpl<>(List.of(entity), Pageable.ofSize(10).withPage(0), 1);

    when(jpaRepository.findAll(Pageable.ofSize(10).withPage(0))).thenReturn(springPage);

    Page<CategoryViewDto> result = reader.getPage(request);

    assertEquals(1, result.total());
    assertEquals(1, result.items().size());
    assertEquals("Tech", result.items().getFirst().name().value());
  }

  @Test
  void shouldReturnPagedCategoriesWithStatus() {
    CategoryViewEntity entity = new CategoryViewEntity();
    entity.setId(UUID.randomUUID().toString());
    entity.setName("Tech");
    entity.setSlug("tech");
    entity.setDefaultImage("https://img/image.jpg");
    entity.setStatus("ARCHIVED");
    entity.setCreatedAt(Instant.now());
    entity.setArchivedAt(Instant.now());

    PageRequest request = new PageRequest(0, 5);

    org.springframework.data.domain.Page<CategoryViewEntity> springPage =
        new PageImpl<>(List.of(entity), Pageable.ofSize(5).withPage(0), 1);

    when(jpaRepository.findAllByStatus(eq("ARCHIVED"), any(Pageable.class))).thenReturn(springPage);

    Page<CategoryViewDto> result = reader.getPageWithStatus(CategoryStatus.ARCHIVED, request);

    assertEquals(1, result.total());
    assertEquals(CategoryStatus.ARCHIVED, result.items().getFirst().status());
  }
}
