package com.luisjrz96.blog.adapters.persistence.blog.category;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryViewJpaRepository extends JpaRepository<CategoryViewEntity, String> {

  Page<CategoryViewEntity> findAllByStatus(String status, Pageable pageable);

  Optional<CategoryViewEntity> findById(String id);

  boolean existsByIdAndStatus(String id, String status);
}
