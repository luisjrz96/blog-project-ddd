package com.luisjrz96.blog.application.blog.post.command.handler;

import static com.luisjrz96.blog.application.shared.Util.ensureAdmin;

import java.util.Objects;

import com.luisjrz96.blog.application.blog.post.command.ArchivePostCommand;
import com.luisjrz96.blog.application.blog.post.port.PostRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.post.Post;

public class ArchivePostHandler {

  private final TransactionalExecutor transactionalExecutor;
  private final UserProvider userProvider;
  private final PostRepository repository;

  public ArchivePostHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      PostRepository repository) {
    this.transactionalExecutor = transactionalExecutor;
    this.userProvider = userProvider;
    this.repository = repository;
  }

  public void handle(ArchivePostCommand cmd) {
    transactionalExecutor.executeInTransaction(
        () -> {
          Actor actor = userProvider.getCurrentUser();
          ensureAdmin(actor);
          Post post = repository.load(cmd.id());
          ensurePostAuthor(actor, post);
          post.archive();
          repository.save(post);
          return null;
        });
  }

  private void ensurePostAuthor(Actor actor, Post post) {
    if (actor == null
        || actor.userId() == null
        || post.getAuthorId() == null
        || !actor.userId().equals(String.valueOf(post.getAuthorId().value()))) {
      throw new ApplicationUnauthorizedException(
          String.format(
              "The post with id %s doesn't belongs to author with id %s",
              post.getId().value(), Objects.requireNonNull(actor).userId()));
    }
  }
}
