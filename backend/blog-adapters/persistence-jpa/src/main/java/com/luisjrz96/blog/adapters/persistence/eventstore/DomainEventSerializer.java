package com.luisjrz96.blog.adapters.persistence.eventstore;

import com.luisjrz96.blog.domain.DomainEvent;

public interface DomainEventSerializer {

  String serialize(DomainEvent event);

  DomainEvent deserialize(String eventType, String payloadJson);
}
