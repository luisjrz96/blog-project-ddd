package com.luisjrz96.blog.application.blog.authorprofile.command.handler;

import java.util.UUID;

import com.luisjrz96.blog.application.blog.authorprofile.command.CreateAuthorProfileCommand;
import com.luisjrz96.blog.application.blog.authorprofile.port.AuthorProfileRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.authorprofile.AuthorProfile;
import com.luisjrz96.blog.domain.shared.AuthorId;

public class CreateAuthorProfileHandler {
  private final TransactionalExecutor transactionalExecutor;
  private final UserProvider userProvider;
  private final AuthorProfileRepository repository;

  public CreateAuthorProfileHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      AuthorProfileRepository repository) {
    this.transactionalExecutor = transactionalExecutor;
    this.userProvider = userProvider;
    this.repository = repository;
  }

  public void handle(CreateAuthorProfileCommand cmd) {
    transactionalExecutor.executeInTransaction(
        () -> {
          var actor = userProvider.getCurrentUser();
          var authorId = new AuthorId(UUID.fromString(actor.userId()));
          AuthorProfile authorProfileInDb = repository.load(authorId);
          if (authorProfileInDb.getAuthorId() != null) {
            throw new ApplicationException(
                String.format(
                    "The profile for author with id %s, already exist, you can update it",
                    actor.userId()));
          }
          var authorProfile =
              AuthorProfile.create(
                  authorId,
                  cmd.bio(),
                  cmd.avatar(),
                  cmd.resumeUrl(),
                  cmd.portafolioUrl(),
                  cmd.socialLinks());
          repository.save(authorProfile);
          return null;
        });
  }
}
