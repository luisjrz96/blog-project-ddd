package com.luisjrz96.blog.adapters.web.controllers.blog.tag;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.luisjrz96.blog.adapters.web.api.TagsApi;
import com.luisjrz96.blog.adapters.web.dto.CreateTagRequest;
import com.luisjrz96.blog.adapters.web.dto.CreateTagResponse;
import com.luisjrz96.blog.adapters.web.dto.PageTagView;
import com.luisjrz96.blog.adapters.web.dto.TagStatus;
import com.luisjrz96.blog.adapters.web.dto.TagView;
import com.luisjrz96.blog.adapters.web.dto.UpdateTagRequest;
import com.luisjrz96.blog.application.blog.tag.TagService;
import com.luisjrz96.blog.application.blog.tag.command.ArchiveTagCommand;
import com.luisjrz96.blog.application.blog.tag.command.CreateTagCommand;
import com.luisjrz96.blog.application.blog.tag.command.UpdateTagCommand;
import com.luisjrz96.blog.application.blog.tag.query.GetTagByIdQuery;
import com.luisjrz96.blog.application.blog.tag.query.TagViewDto;
import com.luisjrz96.blog.application.blog.tag.query.TagsPageQuery;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;

@RestController
public class TagController implements TagsApi {

  private final TagService tagService;
  private final TagViewMapper mapper;

  public TagController(TagService tagService, TagViewMapper mapper) {
    this.tagService = tagService;
    this.mapper = mapper;
  }

  private PageTagView toPageView(Page<TagViewDto> page) {
    int totalPages = (int) Math.ceil((double) page.total() / page.size());
    return new PageTagView(
        mapper.toViewList(page.items()), page.page(), page.size(), page.total(), totalPages);
  }

  @Override
  public ResponseEntity<Void> adminArchiveTag(String id) {
    tagService.archive(new ArchiveTagCommand(new TagId(UUID.fromString(id))));
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  public ResponseEntity<CreateTagResponse> adminCreateTag(CreateTagRequest createTagRequest) {
    var cmd = new CreateTagCommand(new TagName(createTagRequest.getName()));
    var id = tagService.create(cmd);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreateTagResponse(String.valueOf(id.value())));
  }

  @Override
  public ResponseEntity<TagView> adminGetTag(String id) {
    return this.getTag(id);
  }

  @Override
  public ResponseEntity<PageTagView> adminGetTags(Integer page, Integer size, TagStatus status) {
    var domainStatus = com.luisjrz96.blog.domain.blog.tag.TagStatus.valueOf(status.name());
    var pageRequest = PageRequest.of(page, size);
    var pageDto = tagService.getPage(new TagsPageQuery(domainStatus, pageRequest));
    return ResponseEntity.ok(toPageView(pageDto));
  }

  @Override
  public ResponseEntity<Void> adminUpdateTag(String id, UpdateTagRequest updateTagRequest) {
    var cmd =
        new UpdateTagCommand(
            new TagId(UUID.fromString(id)), new TagName(updateTagRequest.getName()));
    tagService.update(cmd);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  public ResponseEntity<TagView> getTag(String id) {
    var tagViewDto = tagService.findById(new GetTagByIdQuery(new TagId(UUID.fromString(id))));
    return ResponseEntity.ok(mapper.toView(tagViewDto));
  }

  @Override
  public ResponseEntity<PageTagView> getTags(Integer page, Integer size) {
    var domainStatus = com.luisjrz96.blog.domain.blog.tag.TagStatus.ACTIVE;
    var pageRequest = PageRequest.of(page, size);
    var pageDto = tagService.getPage(new TagsPageQuery(domainStatus, pageRequest));
    return ResponseEntity.ok(toPageView(pageDto));
  }
}
