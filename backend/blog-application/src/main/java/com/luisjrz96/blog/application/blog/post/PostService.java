package com.luisjrz96.blog.application.blog.post;

import com.luisjrz96.blog.application.blog.post.command.ArchivePostCommand;
import com.luisjrz96.blog.application.blog.post.command.CreatePostCommand;
import com.luisjrz96.blog.application.blog.post.command.PublishPostCommand;
import com.luisjrz96.blog.application.blog.post.command.UpdatePostCommand;
import com.luisjrz96.blog.application.blog.post.command.handler.ArchivePostHandler;
import com.luisjrz96.blog.application.blog.post.command.handler.CreatePostHandler;
import com.luisjrz96.blog.application.blog.post.command.handler.PublishPostHandler;
import com.luisjrz96.blog.application.blog.post.command.handler.UpdatePostHandler;
import com.luisjrz96.blog.application.blog.post.query.GetPostByIdQuery;
import com.luisjrz96.blog.application.blog.post.query.PostsPageQuery;
import com.luisjrz96.blog.application.blog.post.query.handler.GetPostByIdHandler;
import com.luisjrz96.blog.application.blog.post.query.handler.GetPostsPageHandler;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.domain.blog.post.PostId;

public class PostService {

  private final CreatePostHandler createPostHandler;
  private final UpdatePostHandler updatePostHandler;
  private final ArchivePostHandler archivePostHandler;
  private final PublishPostHandler publishPostHandler;
  private final GetPostByIdHandler getPostByIdHandler;
  private final GetPostsPageHandler getPostsPageHandler;

  public PostService(
      CreatePostHandler createPostHandler,
      UpdatePostHandler updatePostHandler,
      ArchivePostHandler archivePostHandler,
      PublishPostHandler publishPostHandler,
      GetPostByIdHandler getPostByIdHandler,
      GetPostsPageHandler getPostsPageHandler) {
    this.createPostHandler = createPostHandler;
    this.updatePostHandler = updatePostHandler;
    this.archivePostHandler = archivePostHandler;
    this.publishPostHandler = publishPostHandler;
    this.getPostByIdHandler = getPostByIdHandler;
    this.getPostsPageHandler = getPostsPageHandler;
  }

  public PostId create(CreatePostCommand cmd) {
    return createPostHandler.handle(cmd);
  }

  public void update(UpdatePostCommand cmd) {
    updatePostHandler.handle(cmd);
  }

  public void archive(ArchivePostCommand cmd) {
    archivePostHandler.handle(cmd);
  }

  public void publish(PublishPostCommand cmd) {
    publishPostHandler.handle(cmd);
  }

  public PostViewDto findById(GetPostByIdQuery query) {
    return getPostByIdHandler.handle(query);
  }

  public Page<PostViewDto> getPage(PostsPageQuery query) {
    return getPostsPageHandler.handle(query);
  }
}
