package com.luisjrz96.blog.adapters.web.controllers.blog.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.luisjrz96.blog.adapters.web.dto.CreateTagRequest;
import com.luisjrz96.blog.adapters.web.dto.TagStatus;
import com.luisjrz96.blog.adapters.web.dto.UpdateTagRequest;
import com.luisjrz96.blog.application.blog.tag.TagService;
import com.luisjrz96.blog.application.blog.tag.query.TagViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.domain.blog.tag.TagId;

class TagControllerTest {

  private TagService tagService;
  private TagViewMapper mapper;
  private TagController controller;

  @BeforeEach
  void setUp() {
    tagService = mock(TagService.class);
    mapper = mock(TagViewMapper.class);
    controller = new TagController(tagService, mapper);
  }

  @Test
  void adminArchiveTag_callsService_andReturnsNoContent() {
    String id = UUID.randomUUID().toString();

    ResponseEntity<Void> resp = controller.adminArchiveTag(id);

    assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    verify(tagService, times(1)).archive(any());
  }

  @Test
  void adminGetTags_returnsOk_withPage() {
    Page<TagViewDto> pageSpy = spy(new Page<>(List.of(), 0L, 0, 10));
    when(tagService.getPage(any())).thenReturn(pageSpy);
    when(mapper.toViewList(any())).thenReturn(Collections.emptyList());

    ResponseEntity<?> resp = controller.adminGetTags(0, 10, TagStatus.ACTIVE);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(tagService, times(1)).getPage(any());
    verify(mapper, times(1)).toViewList(any());
  }

  @Test
  void getTags_returnsOk_withPage() {
    Page<TagViewDto> pageSpy = spy(new Page<>(List.of(), 0L, 0, 10));
    when(tagService.getPage(any())).thenReturn(pageSpy);
    when(mapper.toViewList(any())).thenReturn(Collections.emptyList());

    ResponseEntity<?> resp = controller.getTags(0, 10);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(tagService, times(1)).getPage(any());
    verify(mapper, times(1)).toViewList(any());
  }

  @Test
  void adminCreateTag_callsCreate_andReturnsCreated() {
    CreateTagRequest req = mock(CreateTagRequest.class);
    when(req.getName()).thenReturn("name");

    TagId returnedId = mock(TagId.class);
    when(returnedId.value()).thenReturn(UUID.randomUUID());
    when(tagService.create(any())).thenReturn(returnedId);

    ResponseEntity<?> resp = controller.adminCreateTag(req);

    assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    verify(tagService, times(1)).create(any());
  }

  @Test
  void adminUpdateTag_callsUpdate_andReturnsNoContent() {
    UpdateTagRequest req = mock(UpdateTagRequest.class);
    when(req.getName()).thenReturn("new-name");

    String id = UUID.randomUUID().toString();
    ResponseEntity<Void> resp = controller.adminUpdateTag(id, req);

    assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    verify(tagService, times(1)).update(any());
  }

  @Test
  void getTag_callsService_andReturnsOk() {
    String id = UUID.randomUUID().toString();
    TagViewDto dto = mock(TagViewDto.class);
    when(tagService.findById(any())).thenReturn(dto);
    when(mapper.toView(dto)).thenReturn(null);

    ResponseEntity<?> resp = controller.getTag(id);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(tagService, times(1)).findById(any());
    verify(mapper, times(1)).toView(dto);
  }

  @Test
  void adminGetTag_delegatesTo_getTag_andReturnsOk() {
    String id = UUID.randomUUID().toString();
    TagViewDto dto = mock(TagViewDto.class);
    when(tagService.findById(any())).thenReturn(dto);
    when(mapper.toView(dto)).thenReturn(null);

    ResponseEntity<?> resp = controller.adminGetTag(id);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(tagService, times(1)).findById(any());
    verify(mapper, times(1)).toView(dto);
  }
}
