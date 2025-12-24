package com.luisjrz96.blog.application.blog.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.luisjrz96.blog.application.blog.post.command.ArchivePostCommand;
import com.luisjrz96.blog.application.blog.post.command.CreatePostCommand;
import com.luisjrz96.blog.application.blog.post.command.PublishPostCommand;
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

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  @Mock private CreatePostHandler createPostHandler;
  @Mock private UpdatePostHandler updatePostHandler;
  @Mock private ArchivePostHandler archivePostHandler;
  @Mock private PublishPostHandler publishPostHandler;
  @Mock private GetPostByIdHandler getPostByIdHandler;
  @Mock private GetPostsPageHandler getPostsPageHandler;
  @InjectMocks private PostService postService;

  @Test
  void shouldDelegateCreateToHandler() {
    var command = mock(CreatePostCommand.class);
    PostId expectedId = new PostId(UUID.randomUUID());

    when(createPostHandler.handle(command)).thenReturn(expectedId);
    PostId result = postService.create(command);
    assertEquals(expectedId, result);
    verify(createPostHandler, times(1)).handle(command);
  }

  @Test
  void shouldDelegateUpdateToHandler() {
    var command = mock(com.luisjrz96.blog.application.blog.post.command.UpdatePostCommand.class);

    postService.update(command);

    verify(updatePostHandler, times(1)).handle(command);
  }

  @Test
  void shouldDelegateArchiveToHandler() {
    var command = mock(ArchivePostCommand.class);

    postService.archive(command);

    verify(archivePostHandler, times(1)).handle(command);
  }

  @Test
  void shouldDelegatePublishToHandler() {
    var command = mock(PublishPostCommand.class);
    postService.publish(command);
    verify(publishPostHandler, times(1)).handle(command);
  }

  @Test
  void shouldDelegateFindByIdToHandler() {
    var query = mock(GetPostByIdQuery.class);
    var expectedDto = mock(PostViewDto.class);

    when(getPostByIdHandler.handle(query)).thenReturn(expectedDto);

    var result = postService.findById(query);

    assertEquals(expectedDto, result);
    verify(getPostByIdHandler, times(1)).handle(query);
  }

  @Test
  void shouldDelegateGetPostsPageToHandler() {
    var query = mock(PostsPageQuery.class);
    var expectedPage = spy(new Page<PostViewDto>(List.of(), 0, 10, 0));

    when(getPostsPageHandler.handle(query)).thenReturn(expectedPage);

    var result = postService.getPage(query);

    assertEquals(expectedPage, result);
    verify(getPostsPageHandler, times(1)).handle(query);
  }
}
