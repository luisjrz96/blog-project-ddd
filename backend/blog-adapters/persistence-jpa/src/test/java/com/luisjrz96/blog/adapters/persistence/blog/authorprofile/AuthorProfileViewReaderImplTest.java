package com.luisjrz96.blog.adapters.persistence.blog.authorprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.luisjrz96.blog.application.blog.authorprofile.query.AuthorProfileViewDto;
import com.luisjrz96.blog.domain.shared.AuthorId;

@ExtendWith(MockitoExtension.class)
class AuthorProfileViewReaderImplTest {

  @Mock private AuthorProfileViewJpaRepository jpaRepository;
  @InjectMocks private AuthorProfileViewReaderImpl reader;

  @Test
  void shouldReturnAuthorProfileById() {
    UUID id = UUID.randomUUID();

    AuthorProfileViewEntity entity = new AuthorProfileViewEntity();
    entity.setId(id.toString());
    entity.setMarkdown("ABCD");
    entity.setAvatarUrl("https://avatars/avatar1.png");
    entity.setResumeUrl("https://resumes/resume1.pdf");
    entity.setPortfolioUrl("https://portfolios/p1.html");
    entity.setCreatedAt(Instant.now());

    when(jpaRepository.findById(id.toString())).thenReturn(Optional.of(entity));

    Optional<AuthorProfileViewDto> result = reader.findById(new AuthorId(id));

    assertTrue(result.isPresent());
    assertEquals("ABCD", result.get().bio().value());
  }

  @Test
  void shouldReturnEmptyWhenAuthorProfileDoesNotExist() {
    UUID id = UUID.randomUUID();

    when(jpaRepository.findById(id.toString())).thenReturn(Optional.empty());

    Optional<AuthorProfileViewDto> result = reader.findById(new AuthorId(id));

    assertTrue(result.isEmpty());
  }
}
