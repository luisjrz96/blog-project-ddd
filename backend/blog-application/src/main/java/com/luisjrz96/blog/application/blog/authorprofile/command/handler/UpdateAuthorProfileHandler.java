package com.luisjrz96.blog.application.blog.authorprofile.command.handler;

import java.util.UUID;

import com.luisjrz96.blog.application.blog.authorprofile.command.UpdateAuthorProfileCommand;
import com.luisjrz96.blog.application.blog.authorprofile.port.AuthorProfileRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.authorprofile.AuthorProfile;
import com.luisjrz96.blog.domain.shared.AuthorId;

public class UpdateAuthorProfileHandler {

  private final TransactionalExecutor transactionalExecutor;
  private final UserProvider userProvider;
  private final AuthorProfileRepository repository;

  public UpdateAuthorProfileHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      AuthorProfileRepository repository) {
    this.transactionalExecutor = transactionalExecutor;
    this.userProvider = userProvider;
    this.repository = repository;
  }

  public void handle(UpdateAuthorProfileCommand cmd) {

    transactionalExecutor.executeInTransaction(
        () -> {
          var actor = userProvider.getCurrentUser();
          var authorId = new AuthorId(UUID.fromString(actor.userId()));
          AuthorProfile authorProfile = repository.load(authorId);
          if (authorProfile.getAuthorId() == null) {
            throw new ApplicationException(
                "Please first create your profile, then you can update it");
          }
          authorProfile.update(
              cmd.bio(), cmd.avatar(), cmd.resumeUrl(), cmd.portafolioUrl(), cmd.socialLinks());
          repository.save(authorProfile);
          return null;
        });
  }
}
