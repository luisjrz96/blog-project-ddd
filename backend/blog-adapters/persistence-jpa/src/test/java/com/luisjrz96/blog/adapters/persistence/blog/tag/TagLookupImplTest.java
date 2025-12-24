package com.luisjrz96.blog.adapters.persistence.blog.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagStatus;

@ExtendWith(MockitoExtension.class)
class TagLookupImplTest {

  @Mock private TagViewJpaRepository tagViewJpaRepository;

  @InjectMocks private TagLookupImpl tagLookup;

  @Test
  void testFindActiveTags_ShouldReturnActiveTagIds() {
    // Given
    TagId tagId1 = new TagId(UUID.randomUUID());
    TagId tagId2 = new TagId(UUID.randomUUID());

    List<TagId> inputTagIds = List.of(tagId1, tagId2);

    // Mock repository to return only the first tag as active
    when(tagViewJpaRepository.findActiveIds(anyList(), eq(TagStatus.ACTIVE.toString())))
        .thenReturn(List.of(tagId1.value().toString()));

    // When
    List<TagId> activeTags = tagLookup.findActiveTags(inputTagIds);

    // Then
    assertEquals(1, activeTags.size(), "Should return only one active tag");
    assertEquals(tagId1, activeTags.getFirst(), "Returned tag should match the active one");

    // Verify repository method was called with correct arguments
    verify(tagViewJpaRepository).findActiveIds(anyList(), eq(TagStatus.ACTIVE.toString()));
  }

  @Test
  void testFindActiveTags_ShouldReturnEmptyList_WhenNoActiveTags() {
    // Given
    TagId tagId1 = new TagId(UUID.randomUUID());
    List<TagId> inputTagIds = List.of(tagId1);

    when(tagViewJpaRepository.findActiveIds(anyList(), eq(TagStatus.ACTIVE.toString())))
        .thenReturn(List.of());

    // When
    List<TagId> activeTags = tagLookup.findActiveTags(inputTagIds);

    // Then
    assertTrue(activeTags.isEmpty(), "Should return empty list when no tags are active");

    verify(tagViewJpaRepository).findActiveIds(anyList(), eq(TagStatus.ACTIVE.toString()));
  }
}
