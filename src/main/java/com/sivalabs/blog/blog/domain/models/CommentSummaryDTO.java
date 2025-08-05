package com.sivalabs.blog.blog.domain.models;

import com.sivalabs.blog.shared.models.CommentStatus;
import java.time.LocalDateTime;

public record CommentSummaryDTO(
        Long id,
        String name,
        String email,
        String content,
        String postTitle,
        String postSlug,
        CommentStatus status,
        LocalDateTime createdAt) {

    public String getSummary() {
        return content.substring(0, Math.min(content.length(), 50)) + "...";
    }

    public String getStatusStyles() {
        return switch (status) {
            case PENDING -> "bg-yellow-100 text-yellow-800";
            case APPROVED -> "bg-green-100 text-green-800";
            case SPAM -> "bg-red-100 text-red-800";
        };
    }
}
