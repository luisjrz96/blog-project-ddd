package com.luisjrz96.blog.domain.shared;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record SocialLink(SocialNetwork socialNetwork, Url url) {
  public SocialLink {
    ValidationUtil.requireNonNull(socialNetwork, "SocialNetwork cannot be null");
    ValidationUtil.requireNonNull(url, "Url cannot be null");
  }
}
