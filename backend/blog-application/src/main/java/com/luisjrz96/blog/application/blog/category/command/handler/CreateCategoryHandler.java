package com.luisjrz96.blog.application.blog.category.command.handler;

import com.luisjrz96.blog.application.blog.category.command.CreateCategoryCommand;
import com.luisjrz96.blog.application.blog.category.port.CategoryRepository;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.category.Category;
import com.luisjrz96.blog.domain.blog.category.CategoryId;

public class CreateCategoryHandler {

  private final TransactionalExecutor transactionalExecutor;
  private final UserProvider userProvider;
  private final CategoryRepository categoryRepository;

  public CreateCategoryHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      CategoryRepository categoryRepository) {
    this.transactionalExecutor = transactionalExecutor;
    this.userProvider = userProvider;
    this.categoryRepository = categoryRepository;
  }

  public CategoryId handle(CreateCategoryCommand cmd) {
    return transactionalExecutor.executeInTransaction(
        () -> {
          ensureAdmin(userProvider.getCurrentUser());

          Category category = Category.create(cmd.name(), cmd.defaultImage());
          categoryRepository.save(category);
          return category.getId();
        });
  }

  private void ensureAdmin(Actor actor) {
    if (actor == null || !actor.isAdmin()) {
      throw new ApplicationUnauthorizedException("Only admins can create categories");
    }
  }
}
