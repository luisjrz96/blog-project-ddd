package com.luisjrz96.blog.domain.aggregate.blog;

import java.util.List;

import com.luisjrz96.blog.domain.util.ValidationUtil;
import com.luisjrz96.blog.domain.vos.blog.Markdown;
import com.luisjrz96.blog.domain.vos.blog.SocialLink;
import com.luisjrz96.blog.domain.vos.shared.AuthorId;
import com.luisjrz96.blog.domain.vos.shared.ImageUrl;
import com.luisjrz96.blog.domain.vos.shared.Url;

public class AuthorProfile {
  private final AuthorId authorId;
  private Markdown bio;
  private ImageUrl avatar;
  private Url resumeUrl;
  private Url portafolioUrl;
  private List<SocialLink> socialLinks;

  public AuthorProfile(
      AuthorId authorId,
      Markdown bio,
      ImageUrl avatar,
      Url resumeUrl,
      Url portafolioUrl,
      List<SocialLink> socialLinks) {
    this.authorId =
        ValidationUtil.requireNonNull(authorId, "AuthorId cannot be null for AuthorProfile");
    this.bio = ValidationUtil.requireNonNull(bio, "bio cannot be null for AuthorProfile");
    this.avatar = ValidationUtil.requireNonNull(avatar, "avatar cannot be null for AuthorProfile");
    this.resumeUrl = resumeUrl;
    this.portafolioUrl = portafolioUrl;
    this.socialLinks = socialLinks;
  }

  public AuthorId getAuthorId() {
    return authorId;
  }

  public Markdown getBio() {
    return bio;
  }

  public void setBio(Markdown bio) {
    this.bio = bio;
  }

  public ImageUrl getAvatar() {
    return avatar;
  }

  public void setAvatar(ImageUrl avatar) {
    this.avatar = avatar;
  }

  public Url getResumeUrl() {
    return resumeUrl;
  }

  public void setResumeUrl(Url resumeUrl) {
    this.resumeUrl = resumeUrl;
  }

  public Url getPortafolioUrl() {
    return portafolioUrl;
  }

  public void setPortafolioUrl(Url portafolioUrl) {
    this.portafolioUrl = portafolioUrl;
  }

  public List<SocialLink> getSocialLinks() {
    return socialLinks;
  }

  public void setSocialLinks(List<SocialLink> socialLinks) {
    this.socialLinks = socialLinks;
  }
}
