package com.luisjrz96.blog.application.blog.post.command.handler;

import static com.luisjrz96.blog.application.shared.Util.ensureAdmin;
import static com.luisjrz96.blog.application.shared.Util.ensurePostAuthor;

import com.luisjrz96.blog.application.blog.post.command.PublishPostCommand;
import com.luisjrz96.blog.application.blog.post.port.PostRepository;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.post.Post;

public class PublishPostHandler {

  private final TransactionalExecutor transactionalExecutor;
  private final UserProvider userProvider;
  private final PostRepository repository;

  public PublishPostHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      PostRepository repository) {
    this.transactionalExecutor = transactionalExecutor;
    this.userProvider = userProvider;
    this.repository = repository;
  }

  public void handle(PublishPostCommand cmd) {
    transactionalExecutor.executeInTransaction(
        () -> {
          Actor actor = userProvider.getCurrentUser();
          ensureAdmin(actor);
          Post post = repository.load(cmd.id());
          ensurePostAuthor(actor, post);
          post.publish();
          repository.save(post);
          return null;
        });
  }
}
