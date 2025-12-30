package com.luisjrz96.blog.adapters.web.controllers.blog.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.luisjrz96.blog.adapters.web.dto.CreatePostRequest;
import com.luisjrz96.blog.adapters.web.dto.PagePostSummaryView;
import com.luisjrz96.blog.adapters.web.dto.PostDetailView;
import com.luisjrz96.blog.adapters.web.dto.PostStatus;
import com.luisjrz96.blog.adapters.web.dto.UpdatePostRequest;
import com.luisjrz96.blog.application.blog.post.PostService;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.domain.blog.post.PostId;

class PostControllerTest {

  private PostService postService;
  private PostViewMapper mapper;
  private PostController controller;

  @BeforeEach
  void setUp() {
    postService = mock(PostService.class);
    mapper = mock(PostViewMapper.class);
    controller = new PostController(postService, mapper);
  }

  @Test
  void adminArchivePost_callsService_andReturnsNoContent() {
    String id = UUID.randomUUID().toString();

    ResponseEntity<Void> resp = controller.adminArchivePost(id);

    assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    verify(postService, times(1)).archive(any());
  }

  @Test
  void adminGetPostPage_returnsOk_withPage() {
    Page<PostViewDto> pageSpy = spy(new Page<>(List.of(), 0L, 0, 10));
    when(postService.getPage(any())).thenReturn(pageSpy);
    when(mapper.toSummaryViewList(any())).thenReturn(Collections.emptyList());

    ResponseEntity<PagePostSummaryView> resp =
        controller.adminGetPostPage(0, 10, null, null, PostStatus.PUBLISHED);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(postService, times(1)).getPage(any());
    verify(mapper, times(1)).toSummaryViewList(any());
  }

  @Test
  void getPostPage_returnsOk_withPage() {
    Page<PostViewDto> pageSpy = spy(new Page<>(List.of(), 0L, 0, 10));
    when(postService.getPage(any())).thenReturn(pageSpy);
    when(mapper.toSummaryViewList(any())).thenReturn(Collections.emptyList());

    ResponseEntity<PagePostSummaryView> resp = controller.getPostPage(0, 10, null, null);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(postService, times(1)).getPage(any());
    verify(mapper, times(1)).toSummaryViewList(any());
  }

  @Test
  void adminPublishPost_callsService_andReturnsNoContent() {
    String id = UUID.randomUUID().toString();

    ResponseEntity<Void> resp = controller.adminPublishPost(id);

    assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    verify(postService, times(1)).publish(any());
  }

  @Test
  void adminUpdatePost_callsUpdate_andReturnsNoContent() {
    UpdatePostRequest req = mock(UpdatePostRequest.class);
    when(req.getTitle()).thenReturn("title");
    when(req.getSummary()).thenReturn("summary");
    when(req.getBody()).thenReturn("body");
    when(req.getCategoryId()).thenReturn(UUID.randomUUID().toString());
    when(req.getTagIds()).thenReturn(List.of(UUID.randomUUID().toString()));
    when(req.getCoverImage()).thenReturn(URI.create("https://img/img.png"));

    String id = UUID.randomUUID().toString();
    ResponseEntity<Void> resp = controller.adminUpdatePost(id, req);

    assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    verify(postService, times(1)).update(any());
  }

  @Test
  void adminCreatePost_callsCreate_andReturnsCreated() {
    CreatePostRequest req = mock(CreatePostRequest.class);
    when(req.getTitle()).thenReturn("title");
    when(req.getSummary()).thenReturn("summary");
    when(req.getBody()).thenReturn("body");
    when(req.getCategoryId()).thenReturn(UUID.randomUUID().toString());
    when(req.getTagIds()).thenReturn(List.of(UUID.randomUUID().toString()));
    when(req.getCoverImage()).thenReturn(URI.create("https://img/img.png"));

    PostId returnedId = mock(PostId.class);
    when(returnedId.value()).thenReturn(UUID.randomUUID());
    when(postService.create(any())).thenReturn(returnedId);

    ResponseEntity<?> resp = controller.adminCreatePost(req);

    assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    verify(postService, times(1)).create(any());
  }

  @Test
  void getPost_callsService_andReturnsOk() {
    String id = UUID.randomUUID().toString();
    PostViewDto dto = mock(PostViewDto.class);
    when(postService.findById(any())).thenReturn(dto);
    when(mapper.toPostDetailView(dto)).thenReturn(null);

    ResponseEntity<PostDetailView> resp = controller.getPost(id);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(postService, times(1)).findById(any());
    verify(mapper, times(1)).toPostDetailView(dto);
  }

  @Test
  void adminGetPost_delegatesTo_getPost_andReturnsOk() {
    String id = UUID.randomUUID().toString();
    PostViewDto dto = mock(PostViewDto.class);
    when(postService.findById(any())).thenReturn(dto);
    when(mapper.toPostDetailView(dto)).thenReturn(null);

    ResponseEntity<PostDetailView> resp = controller.adminGetPost(id);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(postService, times(1)).findById(any());
    verify(mapper, times(1)).toPostDetailView(dto);
  }
}
