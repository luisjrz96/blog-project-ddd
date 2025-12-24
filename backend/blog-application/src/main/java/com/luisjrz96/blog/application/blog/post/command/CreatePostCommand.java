package com.luisjrz96.blog.application.blog.post.command;

import java.util.List;

import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Summary;
import com.luisjrz96.blog.domain.shared.Title;

public record CreatePostCommand(
    Title title,
    Summary summary,
    Markdown body,
    CategoryId categoryId,
    List<TagId> tagIds,
    ImageUrl coverImage) {}
