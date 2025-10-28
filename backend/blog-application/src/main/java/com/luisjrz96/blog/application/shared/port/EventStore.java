package com.luisjrz96.blog.application.shared.port;

import java.util.List;

import com.luisjrz96.blog.domain.DomainEvent;

public interface EventStore {
  List<DomainEvent> load(String aggregateType, Object aggregateId);

  void append(
      String aggregateType, Object aggregateId, int expectedVersion, List<DomainEvent> newEvents);
}
