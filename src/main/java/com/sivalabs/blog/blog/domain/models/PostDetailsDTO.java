package com.sivalabs.blog.blog.domain.models;

import com.sivalabs.blog.shared.models.PostStatus;
import java.time.LocalDateTime;
import java.util.List;

public record PostDetailsDTO(
        Long id,
        String title,
        String slug,
        String mdContent,
        String content,
        String coverImage,
        String author,
        String authorBio,
        String authorImage,
        PostCategory category,
        List<PostTag> tags,
        PostStatus status,
        LocalDateTime createdAt) {

    public record PostCategory(String label, String slug) {}

    public record PostTag(String label, String slug) {}
}
