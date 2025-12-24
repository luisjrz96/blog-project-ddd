package com.luisjrz96.blog.adapters.persistence.blog.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.luisjrz96.blog.adapters.persistence.blog.category.CategoryViewEntity;
import com.luisjrz96.blog.adapters.persistence.blog.tag.TagViewEntity;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.post.PostStatus;

@ExtendWith(MockitoExtension.class)
class PostViewReaderImplTest {

  @Mock private PostViewJpaRepository jpaRepository;
  @InjectMocks private PostViewReaderImpl reader;

  @Test
  void shouldReturnPostById() {
    UUID id = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    PostViewEntity entity = new PostViewEntity();
    CategoryViewEntity categoryViewEntity = new CategoryViewEntity();
    TagViewEntity tagViewEntity = new TagViewEntity();
    categoryViewEntity.setId(UUID.randomUUID().toString());
    categoryViewEntity.setName("Tech");
    tagViewEntity.setId(UUID.randomUUID().toString());
    tagViewEntity.setName("Java");
    entity.setId(id.toString());
    entity.setTitle("First Post");
    entity.setSlug("first-post");
    entity.setSummary("This is the summary of the first post.");
    entity.setBody("This is the content of the first post.");
    entity.setStatus("PUBLISHED");
    entity.setCoverImage("https://img/image.jpg");
    entity.setAuthorId(authorId.toString());
    entity.setCreatedAt(Instant.now());
    entity.setCategory(categoryViewEntity);
    entity.setTags(Set.of(tagViewEntity));
    entity.setPublishedAt(Instant.now());

    when(jpaRepository.findById(id.toString())).thenReturn(Optional.of(entity));

    Optional<PostViewDto> result = reader.getById(new PostId(id));

    assertTrue(result.isPresent());
    assertEquals("First Post", result.get().title().value());
    assertEquals("PUBLISHED", result.get().status().name());
    assertEquals(authorId.toString(), result.get().authorId().value().toString());
  }

  @Test
  void shouldReturnEmptyWhenPostNotFound() {
    UUID id = UUID.randomUUID();

    when(jpaRepository.findById(id.toString())).thenReturn(Optional.empty());

    Optional<PostViewDto> result = reader.getById(new PostId(id));

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnPagedPosts() {
    UUID id = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    PostViewEntity entity = new PostViewEntity();
    CategoryViewEntity categoryViewEntity = new CategoryViewEntity();
    categoryViewEntity.setId(UUID.randomUUID().toString());
    categoryViewEntity.setName("Tech");
    entity.setId(id.toString());
    entity.setTitle("First Post");
    entity.setSlug("first-post");
    entity.setSummary("This is the summary of the first post.");
    entity.setBody("This is the content of the first post.");
    entity.setStatus("PUBLISHED");
    entity.setCoverImage("https://img/image.jpg");
    entity.setAuthorId(authorId.toString());
    entity.setCreatedAt(Instant.now());
    entity.setCategory(categoryViewEntity);
    entity.setPublishedAt(Instant.now());

    PageRequest request = new PageRequest(0, 10);

    org.springframework.data.domain.Page<PostViewEntity> springPage =
        new PageImpl<>(List.of(entity), Pageable.ofSize(10).withPage(0), 1);

    when(jpaRepository.findAll(Pageable.ofSize(10).withPage(0))).thenReturn(springPage);

    Page<PostViewDto> result = reader.getPage(request);

    assertEquals(1, result.total());
    assertEquals(1, result.items().size());
    assertEquals("First Post", result.items().getFirst().title().value());
  }

  @Test
  void shouldReturnPagedPostsWithStatus() {
    UUID id = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    PostViewEntity entity = new PostViewEntity();
    CategoryViewEntity categoryViewEntity = new CategoryViewEntity();
    categoryViewEntity.setId(UUID.randomUUID().toString());
    categoryViewEntity.setName("Tech");
    entity.setId(id.toString());
    entity.setTitle("First Post");
    entity.setSlug("first-post");
    entity.setSummary("This is the summary of the first post.");
    entity.setBody("This is the content of the first post.");
    entity.setStatus("PUBLISHED");
    entity.setCoverImage("https://img/image.jpg");
    entity.setAuthorId(authorId.toString());
    entity.setCreatedAt(Instant.now());
    entity.setCategory(categoryViewEntity);
    entity.setPublishedAt(Instant.now());

    PageRequest request = new PageRequest(0, 10);

    org.springframework.data.domain.Page<PostViewEntity> springPage =
        new PageImpl<>(List.of(entity), Pageable.ofSize(10).withPage(0), 1);

    when(jpaRepository.findAllByStatus(
            PostStatus.PUBLISHED.name(), Pageable.ofSize(10).withPage(0)))
        .thenReturn(springPage);

    Page<PostViewDto> result = reader.getPageWithStatus(PostStatus.PUBLISHED, request);

    assertEquals(1, result.total());
    assertEquals(1, result.items().size());
    assertEquals("First Post", result.items().getFirst().title().value());
  }
}
