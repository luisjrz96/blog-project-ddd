package com.luisjrz96.blog.application.blog.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

  @Mock private CreateTagHandler createTagHandler;
  @Mock private UpdateTagHandler updateTagHandler;
  @Mock private ArchiveTagHandler archiveTagHandler;
  @Mock private GetTagsPageHandler getTagsPageHandler;
  @Mock private GetTagByIdHandler getTagByIdHandler;
  @InjectMocks private TagService tagService;

  @Test
  void shouldDelegateCreateToHandler() {
    var command = mock(CreateTagCommand.class);
    var expectedId = new TagId(java.util.UUID.randomUUID());

    when(createTagHandler.handle(command)).thenReturn(expectedId);

    var id = tagService.create(command);
    assertEquals(expectedId, id);
    verify(createTagHandler).handle(command);
  }

  @Test
  void shouldDelegateUpdateToHandler() {
    var command = mock(UpdateTagCommand.class);
    tagService.update(command);
    verify(updateTagHandler).handle(command);
  }

  @Test
  void shouldDelegateArchiveToHandler() {
    var command = mock(ArchiveTagCommand.class);
    tagService.archive(command);
    verify(archiveTagHandler).handle(command);
  }

  @Test
  void shouldDelegateGetPageToHandler() {
    var query = mock(TagsPageQuery.class);
    var expectedPage = spy(new Page<TagViewDto>(List.of(), 0, 0, 0));

    when(getTagsPageHandler.handle(query)).thenReturn(expectedPage);
    var result = tagService.getPage(query);
    assertEquals(expectedPage, result);
    verify(getTagsPageHandler).handle(query);
  }

  @Test
  void shouldDelegateFindByIdToHandler() {
    var query = mock(GetTagByIdQuery.class);
    var expectedTagViewDto = mock(TagViewDto.class);

    when(getTagByIdHandler.handle(query)).thenReturn(expectedTagViewDto);

    var result = tagService.findById(query);
    assertEquals(expectedTagViewDto, result);
    verify(getTagByIdHandler).handle(query);
  }
}
