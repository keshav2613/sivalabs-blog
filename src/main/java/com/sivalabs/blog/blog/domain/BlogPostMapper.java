package com.sivalabs.blog.blog.domain;

import com.sivalabs.blog.blog.domain.models.CommentSummaryDTO;
import com.sivalabs.blog.blog.domain.models.PostDetailsDTO;
import com.sivalabs.blog.blog.domain.models.PostSummaryDTO;
import com.sivalabs.blog.shared.entities.Comment;
import com.sivalabs.blog.shared.entities.Post;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BlogPostMapper {
    private static final String DEFAULT_COVER_IMAGE = "/images/covers/blog-cover-1.jpg";

    public PostSummaryDTO toPostSummaryDTO(Post post) {
        String coverImage = post.getCoverImage() == null ? DEFAULT_COVER_IMAGE : post.getCoverImage();
        return new PostSummaryDTO(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getContent(),
                coverImage,
                post.getStatus(),
                post.getCreatedBy().getName(),
                post.getCategory().getLabel(),
                post.getCategory().getSlug(),
                post.getCreatedAt());
    }

    public PostDetailsDTO toPostDetailsDTO(Post post) {
        String coverImage = post.getCoverImage() == null ? DEFAULT_COVER_IMAGE : post.getCoverImage();

        PostDetailsDTO.PostCategory category = new PostDetailsDTO.PostCategory(
                post.getCategory().getLabel(), post.getCategory().getSlug());
        List<PostDetailsDTO.PostTag> postTags = post.getTags().stream()
                .map(tag -> new PostDetailsDTO.PostTag(tag.getLabel(), tag.getSlug()))
                .toList();
        return new PostDetailsDTO(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getMdContent(),
                post.getContent(),
                coverImage,
                post.getCreatedBy().getName(),
                post.getCreatedBy().getBio(),
                post.getCreatedBy().getImage(),
                category,
                postTags,
                post.getStatus(),
                post.getCreatedAt());
    }

    public CommentSummaryDTO toCommentSummaryDTO(Comment comment) {
        return new CommentSummaryDTO(
                comment.getId(),
                comment.getName(),
                comment.getEmail(),
                comment.getContent(),
                comment.getPost().getTitle(),
                comment.getPost().getSlug(),
                comment.getStatus(),
                comment.getCreatedAt());
    }
}
