package com.luisjrz96.blog.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {
  protected int version = 0;
  private final List<DomainEvent> uncommittedEvents;

  public AggregateRoot() {
    this.uncommittedEvents = new ArrayList<>();
  }

  public void applyChange(DomainEvent event) {
    applyEvent(event);
    uncommittedEvents.add(event);
  }

  public void replayEvent(DomainEvent event) {
    applyEvent(event);
  }

  private void applyEvent(DomainEvent event) {
    when(event);
  }

  protected abstract void when(DomainEvent event);

  public List<DomainEvent> getUncommittedEvents() {
    return List.copyOf(uncommittedEvents);
  }

  public void markEventsAsCommitted() {
    uncommittedEvents.clear();
  }

  public int getVersion() {
    return version;
  }

  protected void incrementVersion() {
    this.version++;
  }

  public void setVersion(int version) {
    this.version = version;
  }
}
