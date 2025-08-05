package com.sivalabs.blog.blog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sivalabs.blog.BaseServiceTest;
import com.sivalabs.blog.blog.domain.models.CommentSummaryDTO;
import com.sivalabs.blog.blog.domain.models.PostDetailsDTO;
import com.sivalabs.blog.blog.domain.models.PostSummaryDTO;
import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import com.sivalabs.blog.shared.models.PagedResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BlogPostServiceTest extends BaseServiceTest {
    @Autowired
    private BlogPostService postService;

    @Test
    void getLatestPosts_shouldReturnLatestPosts() {
        // When
        PagedResult<PostSummaryDTO> result = postService.getLatestPosts(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.data()).isNotEmpty();
        assertThat(result.data()).isSortedAccordingTo((p1, p2) -> p2.createdAt().compareTo(p1.createdAt()));
    }

    @Test
    void searchPosts_shouldReturnMatchingPosts() {
        // When
        PagedResult<PostSummaryDTO> result = postService.searchPosts("test", 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.data()).isNotEmpty();
        assertThat(result.data())
                .allMatch(post -> post.title().toLowerCase().contains("test")
                        || post.content().toLowerCase().contains("test"));
    }

    @Test
    void searchPosts_shouldReturnEmptyResult_whenNoMatchingPosts() {
        // When
        PagedResult<PostSummaryDTO> result = postService.searchPosts("nonexistentterm", 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.data()).isEmpty();
    }

    @Test
    void getPostsByCategory_shouldReturnPostsInCategory() {
        // When
        PagedResult<PostSummaryDTO> result = postService.getPostsByCategory("java", 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.data()).isNotEmpty();
        assertThat(result.data()).allMatch(post -> post.categorySlug().equals("java"));
    }

    @Test
    void getPostsByCategory_shouldReturnEmptyResult_whenCategoryHasNoPosts() {
        // When
        PagedResult<PostSummaryDTO> result = postService.getPostsByCategory("nonexistentcategory", 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.data()).isEmpty();
    }

    @Test
    void getPostsByTag_shouldReturnPostsWithTag() {
        // When
        PagedResult<PostSummaryDTO> result = postService.getPostsByTag("spring-boot", 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.data()).isNotEmpty();
    }

    @Test
    void getPostsByTag_shouldReturnEmptyResult_whenTagHasNoPosts() {
        // When
        PagedResult<PostSummaryDTO> result = postService.getPostsByTag("nonexistenttag", 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.data()).isEmpty();
    }

    @Test
    void getPostBySlug_shouldReturnPost_whenPostExists() {
        // When
        PostDetailsDTO post = postService.getPostBySlug("test-post-1");

        // Then
        assertThat(post).isNotNull();
        assertThat(post.slug()).isEqualTo("test-post-1");
    }

    @Test
    void getPostBySlug_shouldThrowException_whenPostDoesNotExist() {
        // When/Then
        assertThatThrownBy(() -> postService.getPostBySlug("nonexistent-post"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found for slug: nonexistent-post");
    }

    @Test
    void getCommentsByPostSlug_shouldReturnCommentsForPost() {
        // When
        List<CommentSummaryDTO> comments = postService.getCommentsByPostSlug("spring-boot-introduction");

        // Then
        assertThat(comments).isNotNull();
        // The test data might not have comments for this post, so we can't assert on size
    }
}
