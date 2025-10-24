package com.luisjrz96.blog.domain.portfolio.aggregate;

import java.util.List;

import com.luisjrz96.blog.domain.portfolio.entity.MediaItem;
import com.luisjrz96.blog.domain.util.ValidationUtil;
import com.luisjrz96.blog.domain.portfolio.vos.ProjectId;
import com.luisjrz96.blog.domain.portfolio.vos.ProjectStatus;
import com.luisjrz96.blog.domain.shared.vos.AuthorId;
import com.luisjrz96.blog.domain.shared.vos.Slug;
import com.luisjrz96.blog.domain.shared.vos.Summary;
import com.luisjrz96.blog.domain.shared.vos.Title;
import com.luisjrz96.blog.domain.shared.vos.Url;

public class Project {

  private final ProjectId projectId;
  private final AuthorId authorId;
  private Title title;
  private Slug slug;
  private Summary summary;
  private ProjectStatus projectStatus;
  private Url repoUrl;
  private Url liveDemoUrl;
  private List<MediaItem> media;

  public Project(
      ProjectId projectId,
      AuthorId authorId,
      Title title,
      Summary summary,
      Url repoUrl,
      Url liveDemoUrl,
      List<MediaItem> media) {
    this.projectId =
        ValidationUtil.requireNonNull(projectId, "ProjectId cannot be null for Project");
    this.authorId = ValidationUtil.requireNonNull(authorId, "Author cannot be null for Project");
    this.title = title;
    this.slug = Slug.from(title.value());
    this.summary = summary;
    this.projectStatus = ProjectStatus.DRAFT;
    this.repoUrl = repoUrl;
    this.liveDemoUrl = liveDemoUrl;
    this.media = media;
  }

  public ProjectId getProjectId() {
    return projectId;
  }

  public AuthorId getAuthorId() {
    return authorId;
  }

  public Title getTitle() {
    return title;
  }

  public void setTitle(Title title) {
    this.title = title;
  }

  public Slug getSlug() {
    return slug;
  }

  public void setSlug(Slug slug) {
    this.slug = slug;
  }

  public Summary getSummary() {
    return summary;
  }

  public void setSummary(Summary summary) {
    this.summary = summary;
  }

  public ProjectStatus getProjectStatus() {
    return projectStatus;
  }

  public void setProjectStatus(ProjectStatus projectStatus) {
    this.projectStatus = projectStatus;
  }

  public Url getRepoUrl() {
    return repoUrl;
  }

  public void setRepoUrl(Url repoUrl) {
    this.repoUrl = repoUrl;
  }

  public Url getLiveDemoUrl() {
    return liveDemoUrl;
  }

  public void setLiveDemoUrl(Url liveDemoUrl) {
    this.liveDemoUrl = liveDemoUrl;
  }

  public List<MediaItem> getMedia() {
    return media;
  }

  public void setMedia(List<MediaItem> media) {
    this.media = media;
  }
}
