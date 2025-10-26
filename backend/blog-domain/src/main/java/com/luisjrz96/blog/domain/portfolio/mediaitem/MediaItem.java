package com.luisjrz96.blog.domain.portfolio.mediaitem;

import com.luisjrz96.blog.domain.shared.Url;

public class MediaItem {
  private MediaId id;
  private MediaType type;
  private Url urlOriginal;
  private Url urlThumb, urlWebp, urlAvif;
  private String altText, caption;
  private Integer width, height;
  private String contentType;
  private Long sizeBytes;
  private int sortIndex;
  private MediaStatus mediaStatus;

  // TODO: pending implementation
}
