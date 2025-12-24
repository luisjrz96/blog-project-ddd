package com.luisjrz96.blog.adapters.persistence.blog.tag;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface TagViewJpaRepository extends PagingAndSortingRepository<TagViewEntity, String> {

  Page<TagViewEntity> findAllByStatus(String status, Pageable pageable);

  @Query("SELECT t.id FROM TagViewEntity t WHERE t.id IN :ids AND t.status = :status")
  List<String> findActiveIds(@Param("ids") List<String> ids, @Param("status") String status);

  Optional<TagViewEntity> findById(String id);
}
