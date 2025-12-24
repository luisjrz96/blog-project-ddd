package com.luisjrz96.blog.adapters.persistence.blog.category;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;

@ExtendWith(MockitoExtension.class)
class CategoryLookupImplTest {

  @Mock private CategoryViewJpaRepository jpaRepository;

  @InjectMocks private CategoryLookupImpl categoryLookup;

  @Test
  void shouldReturnTrueWhenCategoryIsActive() {
    // given
    UUID uuid = UUID.randomUUID();
    CategoryId categoryId = new CategoryId(uuid);

    when(jpaRepository.existsByIdAndStatus(String.valueOf(uuid), CategoryStatus.ACTIVE.name()))
        .thenReturn(true);

    // when
    boolean result = categoryLookup.isActive(categoryId);

    // then
    assertTrue(result);
    verify(jpaRepository).existsByIdAndStatus(String.valueOf(uuid), CategoryStatus.ACTIVE.name());
  }

  @Test
  void shouldReturnFalseWhenCategoryIsNotActive() {
    // given
    UUID uuid = UUID.randomUUID();
    CategoryId categoryId = new CategoryId(uuid);

    when(jpaRepository.existsByIdAndStatus(String.valueOf(uuid), CategoryStatus.ACTIVE.name()))
        .thenReturn(false);

    // when
    boolean result = categoryLookup.isActive(categoryId);

    // then
    assertFalse(result);
    verify(jpaRepository).existsByIdAndStatus(String.valueOf(uuid), CategoryStatus.ACTIVE.name());
  }
}
