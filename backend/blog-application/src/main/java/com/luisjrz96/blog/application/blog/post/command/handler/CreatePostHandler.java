package com.luisjrz96.blog.application.blog.post.command.handler;

import static com.luisjrz96.blog.application.shared.Util.ensureAdmin;
import static com.luisjrz96.blog.application.shared.Util.getValidTagIds;

import java.util.List;
import java.util.UUID;

import com.luisjrz96.blog.application.blog.category.port.CategoryLookup;
import com.luisjrz96.blog.application.blog.post.command.CreatePostCommand;
import com.luisjrz96.blog.application.blog.post.port.PostRepository;
import com.luisjrz96.blog.application.blog.tag.port.TagLookup;
import com.luisjrz96.blog.application.shared.error.ApplicationException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.post.Post;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.shared.AuthorId;

public class CreatePostHandler {
  private final TransactionalExecutor transactionalExecutor;
  private final UserProvider userProvider;
  private final PostRepository postRepository;
  private final TagLookup tagLookup;
  private final CategoryLookup categoryLookup;

  public CreatePostHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      PostRepository postRepository,
      TagLookup tagLookup,
      CategoryLookup categoryLookup) {
    this.transactionalExecutor = transactionalExecutor;
    this.userProvider = userProvider;
    this.postRepository = postRepository;
    this.tagLookup = tagLookup;
    this.categoryLookup = categoryLookup;
  }

  public PostId handle(CreatePostCommand cmd) {
    return transactionalExecutor.executeInTransaction(
        () -> {
          Actor actor = userProvider.getCurrentUser();
          ensureAdmin(actor);
          ensureCategoryIsActive(cmd.categoryId());
          List<TagId> validTagIds = getValidTagIds(tagLookup, cmd.tagIds());

          Post post =
              Post.create(
                  new AuthorId(UUID.fromString(actor.userId())),
                  cmd.title(),
                  cmd.summary(),
                  cmd.body(),
                  cmd.categoryId(),
                  validTagIds,
                  cmd.coverImage());
          postRepository.save(post);
          return post.getId();
        });
  }

  private void ensureCategoryIsActive(CategoryId id) {
    if (!categoryLookup.isActive(id)) {
      throw new ApplicationException(
          String.format("Category not found or not ACTIVE %s", id.value()));
    }
  }
}
