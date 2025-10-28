package com.luisjrz96.blog.application.blog.category.command.handler;

import com.luisjrz96.blog.application.blog.category.command.ArchiveCategoryCommand;
import com.luisjrz96.blog.application.blog.category.port.CategoryRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.category.Category;

public class ArchiveCategoryHandler {

  private final TransactionalExecutor transactionalExecutor;
  private final UserProvider userProvider;
  private final CategoryRepository categoryRepository;

  public ArchiveCategoryHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      CategoryRepository categoryRepository) {
    this.transactionalExecutor = transactionalExecutor;
    this.userProvider = userProvider;
    this.categoryRepository = categoryRepository;
  }

  public void handle(ArchiveCategoryCommand cmd) {
    transactionalExecutor.executeInTransaction(
        () -> {
          ensureAdmin(userProvider.getCurrentUser());

          Category category = categoryRepository.load(cmd.categoryId());
          category.archive();
          categoryRepository.save(category);
          return null;
        });
  }

  private void ensureAdmin(Actor actor) {
    if (actor == null || !actor.isAdmin()) {
      throw new ApplicationUnauthorizedException("Only admins can archive categories");
    }
  }
}
