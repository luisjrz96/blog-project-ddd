package com.luisjrz96.blog.application.blog.post.port;

import com.luisjrz96.blog.domain.blog.post.Post;
import com.luisjrz96.blog.domain.blog.post.PostId;

public interface PostRepository {
  Post load(PostId id);

  void save(Post post);
}
