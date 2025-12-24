package com.luisjrz96.blog.adapters.persistence.blog.category;

import org.springframework.stereotype.Service;

import com.luisjrz96.blog.application.blog.category.port.CategoryLookup;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;

@Service
public class CategoryLookupImpl implements CategoryLookup {

  private final CategoryViewJpaRepository jpaRepository;

  public CategoryLookupImpl(CategoryViewJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public boolean isActive(CategoryId id) {
    return jpaRepository.existsByIdAndStatus(
        String.valueOf(id.value()), CategoryStatus.ACTIVE.name());
  }
}
