package com.luisjrz96.blog.application.blog.authorprofile.port;

import com.luisjrz96.blog.domain.blog.authorprofile.AuthorProfile;
import com.luisjrz96.blog.domain.shared.AuthorId;

public interface AuthorProfileRepository {

  AuthorProfile load(AuthorId id);

  void save(AuthorProfile authorProfile);
}
