package com.sivalabs.blog.blog.domain.models;

import com.sivalabs.blog.shared.models.CommentStatus;

public record CreateCommentParams(String name, String email, String content, Long postId, CommentStatus status) {}
