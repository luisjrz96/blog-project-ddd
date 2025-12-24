package com.luisjrz96.blog.application.blog.tag.command;

import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;

public record UpdateTagCommand(TagId id, TagName name) {}
