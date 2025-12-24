package com.luisjrz96.blog.application.blog.tag.command.handler;

import static com.luisjrz96.blog.application.shared.Util.ensureAdmin;

import com.luisjrz96.blog.application.blog.tag.command.ArchiveTagCommand;
import com.luisjrz96.blog.application.blog.tag.port.TagRepository;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.tag.Tag;

public class ArchiveTagHandler {

  private final TransactionalExecutor transactionalExecutor;
  private final UserProvider userProvider;
  private final TagRepository tagRepository;

  public ArchiveTagHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      TagRepository tagRepository) {
    this.transactionalExecutor = transactionalExecutor;
    this.userProvider = userProvider;
    this.tagRepository = tagRepository;
  }

  public void handle(ArchiveTagCommand cmd) {
    transactionalExecutor.executeInTransaction(
        () -> {
          ensureAdmin(userProvider.getCurrentUser());

          Tag tag = tagRepository.load(cmd.id());
          tag.archive();
          tagRepository.save(tag);
          return null;
        });
  }
}
