package com.luisjrz96.blog.domain.vos.blog;

import com.luisjrz96.blog.domain.util.ValidationUtil;
import com.luisjrz96.blog.domain.vos.shared.Url;

public record SocialLink(SocialNetwork socialNetwork, Url url) {
  public SocialLink {
    ValidationUtil.requireNonNull(socialNetwork, "SocialNetwork cannot be null");
    ValidationUtil.requireNonNull(url, "Url cannot be null");
  }
}
