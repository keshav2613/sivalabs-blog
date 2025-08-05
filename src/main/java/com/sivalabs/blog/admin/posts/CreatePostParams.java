package com.sivalabs.blog.admin.posts;

import com.sivalabs.blog.shared.models.PostStatus;
import java.util.Set;

public record CreatePostParams(
        String title,
        String mdContent,
        String categorySlug,
        Set<String> tagLabels,
        PostStatus status,
        Long createdBy) {}
