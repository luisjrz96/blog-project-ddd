package com.luisjrz96.blog.application.blog.authorprofile.port;

import java.util.Optional;

import com.luisjrz96.blog.application.blog.authorprofile.query.AuthorProfileViewDto;
import com.luisjrz96.blog.domain.shared.AuthorId;

public interface AuthorProfileViewReader {

  Optional<AuthorProfileViewDto> findById(AuthorId id);
}
