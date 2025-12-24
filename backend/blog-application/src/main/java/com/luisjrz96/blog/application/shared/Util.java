package com.luisjrz96.blog.application.shared;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.luisjrz96.blog.application.blog.tag.port.TagLookup;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.security.Actor;
import com.luisjrz96.blog.domain.blog.post.Post;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.exception.DomainException;

public class Util {

  private Util() {
    throw new DomainException("Util class cannot be instantiated");
  }

  public static void ensureAdmin(Actor actor) {
    if (actor == null || !actor.isAdmin()) {
      throw new ApplicationUnauthorizedException("Only admins make changes to categories");
    }
  }

  public static void ensurePostAuthor(Actor actor, Post post) {
    if (actor == null
        || actor.userId() == null
        || post.getAuthorId() == null
        || !actor.userId().equals(String.valueOf(post.getAuthorId().value()))) {
      throw new ApplicationUnauthorizedException(
          String.format(
              "The post with id %s doesn't belongs to author with id %s",
              post.getId().value(), Objects.requireNonNull(actor).userId()));
    }
  }

  public static List<TagId> getValidTagIds(TagLookup tagLookup, List<TagId> tagIds) {
    Set<TagId> tagSet = new LinkedHashSet<>(tagIds);
    return tagLookup.findActiveTags(tagSet.stream().toList());
  }
}
