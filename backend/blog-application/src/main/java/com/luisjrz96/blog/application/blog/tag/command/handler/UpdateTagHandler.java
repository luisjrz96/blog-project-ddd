package com.luisjrz96.blog.application.blog.tag.command.handler;

import static com.luisjrz96.blog.application.shared.Util.ensureAdmin;

import com.luisjrz96.blog.application.blog.tag.command.UpdateTagCommand;
import com.luisjrz96.blog.application.blog.tag.port.TagRepository;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.tag.Tag;

public class UpdateTagHandler {

  private final TransactionalExecutor transactionalExecutor;
  private final UserProvider userProvider;
  private final TagRepository tagRepository;

  public UpdateTagHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      TagRepository tagRepository) {
    this.transactionalExecutor = transactionalExecutor;
    this.userProvider = userProvider;
    this.tagRepository = tagRepository;
  }

  public void handle(UpdateTagCommand cmd) {
    transactionalExecutor.executeInTransaction(
        () -> {
          ensureAdmin(userProvider.getCurrentUser());
          Tag tag = tagRepository.load(cmd.id());
          tag.update(cmd.name());
          tagRepository.save(tag);
          return null;
        });
  }
}
