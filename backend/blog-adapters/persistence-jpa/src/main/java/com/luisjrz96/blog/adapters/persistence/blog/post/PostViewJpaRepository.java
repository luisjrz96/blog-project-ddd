package com.luisjrz96.blog.adapters.persistence.blog.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewJpaRepository extends JpaRepository<PostViewEntity, String> {

  @EntityGraph(attributePaths = {"category", "tags"})
  Page<PostViewEntity> findAllByStatus(String status, Pageable pageable);
}
