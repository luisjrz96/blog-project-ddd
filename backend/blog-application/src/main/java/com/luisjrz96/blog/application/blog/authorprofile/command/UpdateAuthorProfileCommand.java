package com.luisjrz96.blog.application.blog.authorprofile.command;

import java.util.List;

import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.SocialLink;
import com.luisjrz96.blog.domain.shared.Url;

public record UpdateAuthorProfileCommand(
    AuthorId authorId,
    Markdown bio,
    ImageUrl avatar,
    Url resumeUrl,
    Url portafolioUrl,
    List<SocialLink> socialLinks) {}
