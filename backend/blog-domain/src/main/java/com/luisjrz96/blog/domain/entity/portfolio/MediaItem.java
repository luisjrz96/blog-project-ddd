package com.luisjrz96.blog.domain.entity.portfolio;

import java.util.Objects;

import com.luisjrz96.blog.domain.vos.portfolio.MediaId;
import com.luisjrz96.blog.domain.vos.portfolio.MediaType;
import com.luisjrz96.blog.domain.vos.shared.Url;

public class MediaItem {
  private final MediaId id;
  private final MediaType type;
  private final Url urlOriginal;
  private Url urlThumb, urlWebp, urlAvif;
  private String altText, caption;
  private Integer width, height;
  private String contentType;
  private Long sizeBytes;
  private int sortIndex;

  public MediaItem(
      MediaId id,
      MediaType type,
      Url urlOriginal,
      Url urlThumb,
      Url urlWebp,
      Url urlAvif,
      String altText,
      String caption,
      Integer width,
      Integer height,
      String contentType,
      Long sizeBytes,
      int sortIndex) {
    this.id = Objects.requireNonNull(id);
    this.type = Objects.requireNonNull(type);
    this.urlOriginal = Objects.requireNonNull(urlOriginal);
    this.urlThumb = urlThumb;
    this.urlWebp = urlWebp;
    this.urlAvif = urlAvif;
    this.altText = altText;
    this.caption = caption;
    this.width = width;
    this.height = height;
    this.contentType = contentType;
    this.sizeBytes = sizeBytes;
    this.sortIndex = sortIndex;
  }

  public MediaId getId() {
    return id;
  }

  public MediaType getType() {
    return type;
  }

  public Url getUrlOriginal() {
    return urlOriginal;
  }

  public Url getUrlThumb() {
    return urlThumb;
  }

  public void setUrlThumb(Url urlThumb) {
    this.urlThumb = urlThumb;
  }

  public Url getUrlWebp() {
    return urlWebp;
  }

  public void setUrlWebp(Url urlWebp) {
    this.urlWebp = urlWebp;
  }

  public Url getUrlAvif() {
    return urlAvif;
  }

  public void setUrlAvif(Url urlAvif) {
    this.urlAvif = urlAvif;
  }

  public String getAltText() {
    return altText;
  }

  public void setAltText(String altText) {
    this.altText = altText;
  }

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public Long getSizeBytes() {
    return sizeBytes;
  }

  public void setSizeBytes(Long sizeBytes) {
    this.sizeBytes = sizeBytes;
  }

  public int getSortIndex() {
    return sortIndex;
  }

  public void setSortIndex(int sortIndex) {
    this.sortIndex = sortIndex;
  }
}
