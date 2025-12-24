package com.luisjrz96.blog.application.shared;

import com.luisjrz96.blog.domain.DomainEvent;

public record VersionedEvent(int version, DomainEvent event) {}
