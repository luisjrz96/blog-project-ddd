package com.luisjrz96.blog.application.shared;

import java.util.List;

public record Page<T>(List<T> items, long total, int page, int size) {}
