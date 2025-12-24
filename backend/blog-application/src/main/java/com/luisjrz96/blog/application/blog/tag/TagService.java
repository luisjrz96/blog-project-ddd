package com.luisjrz96.blog.application.blog.tag;

import com.luisjrz96.blog.application.blog.tag.command.ArchiveTagCommand;
import com.luisjrz96.blog.application.blog.tag.command.CreateTagCommand;
import com.luisjrz96.blog.application.blog.tag.command.UpdateTagCommand;
import com.luisjrz96.blog.application.blog.tag.command.handler.ArchiveTagHandler;
import com.luisjrz96.blog.application.blog.tag.command.handler.CreateTagHandler;
import com.luisjrz96.blog.application.blog.tag.command.handler.UpdateTagHandler;
import com.luisjrz96.blog.application.blog.tag.query.GetTagByIdQuery;
import com.luisjrz96.blog.application.blog.tag.query.TagViewDto;
import com.luisjrz96.blog.application.blog.tag.query.TagsPageQuery;
import com.luisjrz96.blog.application.blog.tag.query.handler.GetTagByIdHandler;
import com.luisjrz96.blog.application.blog.tag.query.handler.GetTagsPageHandler;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.domain.blog.tag.TagId;

public class TagService {

  private final CreateTagHandler createTagHandler;
  private final UpdateTagHandler updateTagHandler;
  private final ArchiveTagHandler archiveTagHandler;
  private final GetTagsPageHandler getTagsPageHandler;
  private final GetTagByIdHandler getTagByIdHandler;

  public TagService(
      CreateTagHandler createTagHandler,
      UpdateTagHandler updateTagHandler,
      ArchiveTagHandler archiveTagHandler,
      GetTagsPageHandler getTagsPageHandler,
      GetTagByIdHandler getTagByIdHandler) {
    this.createTagHandler = createTagHandler;
    this.updateTagHandler = updateTagHandler;
    this.archiveTagHandler = archiveTagHandler;
    this.getTagsPageHandler = getTagsPageHandler;
    this.getTagByIdHandler = getTagByIdHandler;
  }

  public TagId create(CreateTagCommand cmd) {
    return createTagHandler.handle(cmd);
  }

  public void update(UpdateTagCommand cmd) {
    updateTagHandler.handle(cmd);
  }

  public void archive(ArchiveTagCommand cmd) {
    archiveTagHandler.handle(cmd);
  }

  public Page<TagViewDto> getPage(TagsPageQuery query) {
    return getTagsPageHandler.handle(query);
  }

  public TagViewDto findById(GetTagByIdQuery query) {
    return getTagByIdHandler.handle(query);
  }
}
