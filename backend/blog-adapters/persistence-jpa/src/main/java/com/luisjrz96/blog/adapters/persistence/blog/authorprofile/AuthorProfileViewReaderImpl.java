package com.luisjrz96.blog.adapters.persistence.blog.authorprofile;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.luisjrz96.blog.application.blog.authorprofile.port.AuthorProfileViewReader;
import com.luisjrz96.blog.application.blog.authorprofile.query.AuthorProfileViewDto;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Url;

@Service
public class AuthorProfileViewReaderImpl implements AuthorProfileViewReader {

  private final AuthorProfileViewJpaRepository repository;

  public AuthorProfileViewReaderImpl(AuthorProfileViewJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<AuthorProfileViewDto> findById(AuthorId id) {
    Optional<AuthorProfileViewEntity> entity = repository.findById(String.valueOf(id.value()));
    return entity.map(this::toDto);
  }

  private AuthorProfileViewDto toDto(AuthorProfileViewEntity e) {
    return new AuthorProfileViewDto(
        new AuthorId(UUID.fromString(e.getId())),
        new Markdown(e.getMarkdown()),
        new ImageUrl(e.getAvatarUrl()),
        new Url(e.getResumeUrl()),
        new Url(e.getPortfolioUrl()),
        e.getSocialLinks(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }
}
