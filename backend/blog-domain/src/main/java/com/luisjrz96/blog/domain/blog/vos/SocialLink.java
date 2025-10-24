package com.luisjrz96.blog.domain.blog.vos;

import com.luisjrz96.blog.domain.util.ValidationUtil;
import com.luisjrz96.blog.domain.shared.vos.Url;

public record SocialLink(SocialNetwork socialNetwork, Url url) {
  public SocialLink {
    ValidationUtil.requireNonNull(socialNetwork, "SocialNetwork cannot be null");
    ValidationUtil.requireNonNull(url, "Url cannot be null");
  }
}
