package com.luisjrz96.blog.adapters.persistence.blog.tag;

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

import com.luisjrz96.blog.application.blog.tag.query.TagViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagStatus;

@ExtendWith(MockitoExtension.class)
class TagRepositoryViewReaderImplTest {

  @Mock private TagViewJpaRepository jpaRepository;

  @InjectMocks private TagViewReaderImpl reader;

  @Test
  void shouldReturnTagById() {
    UUID uuid = UUID.randomUUID();

    TagViewEntity entity = new TagViewEntity();
    entity.setId(uuid.toString());
    entity.setName("Java");
    entity.setSlug("java");
    entity.setStatus("ACTIVE");
    entity.setCreatedAt(Instant.now());

    when(jpaRepository.findById(uuid.toString())).thenReturn(Optional.of(entity));

    Optional<TagViewDto> result = reader.getById(new TagId(uuid));

    assertTrue(result.isPresent());
    assertEquals("Java", result.get().name().value());
    assertEquals(TagStatus.ACTIVE, result.get().status());
  }

  @Test
  void shouldReturnEmptyWhenTagNotFound() {
    UUID uuid = UUID.randomUUID();

    when(jpaRepository.findById(uuid.toString())).thenReturn(Optional.empty());

    Optional<TagViewDto> result = reader.getById(new TagId(uuid));

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnPageOfTags() {
    TagViewEntity entity1 = new TagViewEntity();
    entity1.setId(UUID.randomUUID().toString());
    entity1.setName("Java");
    entity1.setSlug("java");
    entity1.setStatus("ACTIVE");
    entity1.setCreatedAt(Instant.now());

    TagViewEntity entity2 = new TagViewEntity();
    entity2.setId(UUID.randomUUID().toString());
    entity2.setName("Python");
    entity2.setSlug("python");
    entity2.setStatus("ACTIVE");
    entity2.setCreatedAt(Instant.now());

    var pageRequest = new PageRequest(0, 10);

    org.springframework.data.domain.Page<TagViewEntity> springPage =
        new PageImpl<>(List.of(entity1, entity2), Pageable.ofSize(10).withPage(0), 2);

    when(jpaRepository.findAll(Pageable.ofSize(10).withPage(0))).thenReturn(springPage);

    Page<TagViewDto> result = reader.getPage(pageRequest);

    assertEquals(2, result.items().size());
    assertEquals(TagStatus.ACTIVE, result.items().getFirst().status());
    assertEquals("Java", result.items().getFirst().name().value());
    assertEquals(TagStatus.ACTIVE, result.items().getFirst().status());
    assertEquals("Python", result.items().get(1).name().value());
  }

  @Test
  void shouldReturnPageOfTagsWithStatus() {
    TagViewEntity entity1 = new TagViewEntity();
    entity1.setId(UUID.randomUUID().toString());
    entity1.setName("Java");
    entity1.setSlug("java");
    entity1.setStatus("ARCHIVED");
    entity1.setCreatedAt(Instant.now());
    entity1.setArchivedAt(Instant.now());

    var pageRequest = new PageRequest(0, 10);

    org.springframework.data.domain.Page<TagViewEntity> springPage =
        new PageImpl<>(List.of(entity1), Pageable.ofSize(10).withPage(0), 2);

    when(jpaRepository.findAllByStatus(eq("ARCHIVED"), any(Pageable.class))).thenReturn(springPage);

    Page<TagViewDto> result = reader.getPageWithStatus(TagStatus.ARCHIVED, pageRequest);

    assertEquals(1, result.items().size());
    assertEquals(TagStatus.ARCHIVED, result.items().getFirst().status());
    assertEquals("Java", result.items().getFirst().name().value());
  }
}
