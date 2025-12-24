package com.luisjrz96.blog.application.blog.category.command.handler;

import static com.luisjrz96.blog.application.shared.Util.ensureAdmin;

import com.luisjrz96.blog.application.blog.category.command.UpdateCategoryCommand;
import com.luisjrz96.blog.application.blog.category.port.CategoryRepository;
import com.luisjrz96.blog.application.shared.port.UserProvider;
import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;
import com.luisjrz96.blog.domain.blog.category.Category;

public class UpdateCategoryHandler {

  private final TransactionalExecutor transactionalExecutor;
  private final UserProvider userProvider;
  private final CategoryRepository categoryRepository;

  public UpdateCategoryHandler(
      TransactionalExecutor transactionalExecutor,
      UserProvider userProvider,
      CategoryRepository categoryRepository) {
    this.transactionalExecutor = transactionalExecutor;
    this.userProvider = userProvider;
    this.categoryRepository = categoryRepository;
  }

  public void handle(UpdateCategoryCommand cmd) {
    transactionalExecutor.executeInTransaction(
        () -> {
          ensureAdmin(userProvider.getCurrentUser());

          Category category = categoryRepository.load(cmd.categoryId());
          category.update(cmd.name(), cmd.defaultImage());
          categoryRepository.save(category);
          return null;
        });
  }
}
