package com.sivalabs.blog.blog.domain.models;

import com.sivalabs.blog.shared.models.PostStatus;
import java.time.LocalDateTime;

public record PostSummaryDTO(
        Long id,
        String title,
        String slug,
        String content,
        String coverImage,
        PostStatus status,
        String author,
        String categoryLabel,
        String categorySlug,
        LocalDateTime createdAt) {
    public String getSummary() {
        return content.substring(0, Math.min(content.length(), 200)) + "...";
    }

    public String getStatusStyles() {
        return switch (status) {
            case PostStatus.DRAFT -> "bg-yellow-100 text-yellow-800";
            case PostStatus.PUBLISHED -> "bg-green-100 text-green-800";
            case PostStatus.ARCHIVED -> "bg-gray-100 text-gray-800";
        };
    }
}
