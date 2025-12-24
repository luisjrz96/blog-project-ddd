package com.luisjrz96.blog.application.blog.post.command.handler;

import static com.luisjrz96.blog.application.shared.Util.ensureAdmin;
import static com.luisjrz96.blog.application.shared.Util.ensurePostAuthor;
import static com.luisjrz96.blog.application.shared.Util.getValidTagIds;

import java.util.List;

import com.luisjrz96.blog.application.blog.category.port.CategoryLookup;
import com.luisjrz96.blog.application.blog.post.command.UpdatePostCommand;
import com.luisjrz96.blog.application.blog.post.port.PostRepository;
import com.luisjrz96.blog.application.blog.tag.port.TagLookup;
import com.luisjrz96.blog.application.shared.error.ApplicationException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.post.Post;
import com.luisjrz96.blog.domain.blog.tag.TagId;

public class UpdatePostHandler {

  private final TransactionalExecutor transactionalExecutor;
  private final UserProvider userProvider;
  private final PostRepository repository;
  private final TagLookup tagLookup;
  private final CategoryLookup categoryLookup;

  public UpdatePostHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      PostRepository repository,
      TagLookup tagLookup,
      CategoryLookup categoryLookup) {
    this.transactionalExecutor = transactionalExecutor;
    this.userProvider = userProvider;
    this.repository = repository;
    this.tagLookup = tagLookup;
    this.categoryLookup = categoryLookup;
  }

  public void handle(UpdatePostCommand cmd) {
    transactionalExecutor.executeInTransaction(
        () -> {
          Actor actor = userProvider.getCurrentUser();
          ensureAdmin(actor);
          ensureCategoryIsActive(cmd.categoryId());
          List<TagId> validTagIds = getValidTagIds(tagLookup, cmd.tagIds());
          Post post = repository.load(cmd.id());
          ensurePostAuthor(actor, post);
          post.update(
              cmd.title(),
              cmd.summary(),
              cmd.body(),
              cmd.categoryId(),
              validTagIds,
              cmd.coverImage());
          repository.save(post);
          return null;
        });
  }

  private void ensureCategoryIsActive(CategoryId id) {
    if (!categoryLookup.isActive(id)) {
      throw new ApplicationException(
          String.format("Category not found or not ACTIVE %s", id.value()));
    }
  }
}
