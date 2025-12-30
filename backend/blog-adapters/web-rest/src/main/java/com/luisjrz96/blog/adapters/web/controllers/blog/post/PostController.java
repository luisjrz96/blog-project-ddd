package com.luisjrz96.blog.adapters.web.controllers.post;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.luisjrz96.blog.adapters.web.api.PostsApi;
import com.luisjrz96.blog.adapters.web.dto.CreatePostRequest;
import com.luisjrz96.blog.adapters.web.dto.CreatePostResponse;
import com.luisjrz96.blog.adapters.web.dto.PagePostSummaryView;
import com.luisjrz96.blog.adapters.web.dto.PostDetailView;
import com.luisjrz96.blog.adapters.web.dto.PostStatus;
import com.luisjrz96.blog.adapters.web.dto.UpdatePostRequest;
import com.luisjrz96.blog.application.blog.post.PostService;
import com.luisjrz96.blog.application.blog.post.command.ArchivePostCommand;
import com.luisjrz96.blog.application.blog.post.command.CreatePostCommand;
import com.luisjrz96.blog.application.blog.post.command.PublishPostCommand;
import com.luisjrz96.blog.application.blog.post.command.UpdatePostCommand;
import com.luisjrz96.blog.application.blog.post.query.GetPostByIdQuery;
import com.luisjrz96.blog.application.blog.post.query.PostsPageQuery;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Summary;
import com.luisjrz96.blog.domain.shared.Title;

@RestController
public class PostController implements PostsApi {

  private final PostService postService;
  private final PostViewMapper mapper;

  public PostController(PostService postService, PostViewMapper mapper) {
    this.postService = postService;
    this.mapper = mapper;
  }

  @Override
  public ResponseEntity<Void> adminArchivePost(String id) {
    var cmd = new ArchivePostCommand(new PostId(UUID.fromString(id)));
    postService.archive(cmd);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  public ResponseEntity<PostDetailView> adminGetPost(String id) {
    return this.getPost(id);
  }

  @Override
  public ResponseEntity<PagePostSummaryView> adminGetPostPage(
      Integer page, Integer size, String categoryId, String tagId, PostStatus status) {
    PostStatus postStatus = status != null ? status : PostStatus.PUBLISHED;
    var query =
        new PostsPageQuery(
            com.luisjrz96.blog.domain.blog.post.PostStatus.valueOf(postStatus.name()),
            PageRequest.of(page, size));
    var pageData = postService.getPage(query);
    return ResponseEntity.ok(toPageView(pageData));
  }

  @Override
  public ResponseEntity<Void> adminPublishPost(String id) {
    var cmd = new PublishPostCommand(new PostId(UUID.fromString(id)));
    postService.publish(cmd);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  public ResponseEntity<Void> adminUpdatePost(String id, UpdatePostRequest updatePostRequest) {
    var cmd =
        new UpdatePostCommand(
            new PostId(UUID.fromString(id)),
            new Title(updatePostRequest.getTitle()),
            new Summary(updatePostRequest.getSummary()),
            new Markdown(updatePostRequest.getBody()),
            new CategoryId(UUID.fromString(updatePostRequest.getCategoryId())),
            updatePostRequest.getTagIds().stream().map(UUID::fromString).map(TagId::new).toList(),
            new ImageUrl(updatePostRequest.getCoverImage().toString()));
    postService.update(cmd);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  public ResponseEntity<CreatePostResponse> adminCreatePost(CreatePostRequest request) {
    var cmd =
        new CreatePostCommand(
            new Title(request.getTitle()),
            new Summary(request.getSummary()),
            new Markdown(request.getBody()),
            new CategoryId(UUID.fromString(request.getCategoryId())),
            request.getTagIds().stream().map(UUID::fromString).map(TagId::new).toList(),
            new ImageUrl(request.getCoverImage().toString()));

    var postId = postService.create(cmd);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreatePostResponse(String.valueOf(postId.value()), PostStatus.DRAFT));
  }

  @Override
  public ResponseEntity<PagePostSummaryView> getPostPage(
      Integer page, Integer size, String categoryId, String tagId) {

    var query =
        new PostsPageQuery(
            com.luisjrz96.blog.domain.blog.post.PostStatus.PUBLISHED, PageRequest.of(page, size));
    var pageData = postService.getPage(query);
    return ResponseEntity.ok(toPageView(pageData));
  }

  @Override
  public ResponseEntity<PostDetailView> getPost(String id) {
    var cmd = new GetPostByIdQuery(new PostId(UUID.fromString(id)));
    var data = postService.findById(cmd);
    return ResponseEntity.ok(mapper.toPostDetailView(data));
  }

  private PagePostSummaryView toPageView(Page<PostViewDto> page) {
    int totalPages = (int) Math.ceil((double) page.total() / page.size());
    return new PagePostSummaryView(
        mapper.toSummaryViewList(page.items()), page.page(), page.size(), page.total(), totalPages);
  }
}
