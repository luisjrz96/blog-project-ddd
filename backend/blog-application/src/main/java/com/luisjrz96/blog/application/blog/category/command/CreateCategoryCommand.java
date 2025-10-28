package com.luisjrz96.blog.application.blog.category.command;

import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.shared.ImageUrl;

public record CreateCategoryCommand(CategoryName name, ImageUrl defaultImage) {}
