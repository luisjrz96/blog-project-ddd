package com.luisjrz96.blog.application.blog.tag.command.handler;

import static com.luisjrz96.blog.application.shared.Util.ensureAdmin;

import com.luisjrz96.blog.application.blog.tag.command.CreateTagCommand;
import com.luisjrz96.blog.application.blog.tag.port.TagRepository;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.tag.Tag;
import com.luisjrz96.blog.domain.blog.tag.TagId;

public class CreateTagHandler {

  private final TransactionalExecutor transactionalExecutor;
  private final UserProvider userProvider;
  private final TagRepository tagRepository;

  public CreateTagHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      TagRepository tagRepository) {
    this.transactionalExecutor = transactionalExecutor;
    this.userProvider = userProvider;
    this.tagRepository = tagRepository;
  }

  public TagId handle(CreateTagCommand cmd) {
    return transactionalExecutor.executeInTransaction(
        () -> {
          ensureAdmin(userProvider.getCurrentUser());
          Tag tag = Tag.create(cmd.name());
          tagRepository.save(tag);
          return tag.getId();
        });
  }
}
