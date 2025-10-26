package com.luisjrz96.blog.domain.portfolio.project;

import java.util.List;

import com.luisjrz96.blog.domain.portfolio.mediaitem.MediaItem;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.Slug;
import com.luisjrz96.blog.domain.shared.Summary;
import com.luisjrz96.blog.domain.shared.Title;
import com.luisjrz96.blog.domain.shared.Url;

public class Project {

  private ProjectId id;
  private AuthorId authorId;
  private Title title;
  private Slug slug;
  private Summary summary;
  private ProjectStatus projectStatus;
  private Url repoUrl;
  private Url liveDemoUrl;
  private List<MediaItem> media;

  // TODO: pending implementation
}
