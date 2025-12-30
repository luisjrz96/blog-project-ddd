package com.luisjrz96.blog.application.blog.authorprofile.query;

import java.time.Instant;
import java.util.List;

import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.SocialLink;
import com.luisjrz96.blog.domain.shared.Url;

public record AuthorProfileViewDto(
    AuthorId id,
    Markdown bio,
    ImageUrl avatar,
    Url resumeUrl,
    Url portfolioUrl,
    List<SocialLink> socialLinks,
    Instant createdAt,
    Instant updatedAt) {}
