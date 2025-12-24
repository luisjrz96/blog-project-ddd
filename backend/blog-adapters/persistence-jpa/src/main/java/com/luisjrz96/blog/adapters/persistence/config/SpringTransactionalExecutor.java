package com.luisjrz96.blog.adapters.persistence.config;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.luisjrz96.blog.application.shared.tx.TransactionalExecutor;

@Service
public class SpringTransactionalExecutor implements TransactionalExecutor {

  private final TransactionTemplate txTemplate;

  public SpringTransactionalExecutor(PlatformTransactionManager txManager) {
    this.txTemplate = new TransactionTemplate(txManager);
  }

  @Override
  public <T> T executeInTransaction(Supplier<T> work) {
    return txTemplate.execute(status -> work.get());
  }
}
