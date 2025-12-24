package com.luisjrz96.blog.application.shared.port;

import java.util.List;

import com.luisjrz96.blog.application.shared.VersionedEvent;
import com.luisjrz96.blog.domain.DomainEvent;

public interface EventStore {
  List<VersionedEvent> load(String aggregateType, Object aggregateId);

  List<VersionedEvent> append(
      String aggregateType, Object aggregateId, int expectedVersion, List<DomainEvent> newEvents);
}
