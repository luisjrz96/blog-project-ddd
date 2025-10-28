package com.luisjrz96.blog.application.shared.tx;

import java.util.function.Supplier;

public interface TransactionalExecutor {

  <T> T executeInTransaction(Supplier<T> work);
}
