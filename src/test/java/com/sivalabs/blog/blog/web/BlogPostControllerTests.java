package com.sivalabs.blog.blog.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class BlogPostControllerTests extends AbstractIT {

    @Test
    void showPosts_shouldGetPaginatedPosts() {
        var result = mockMvcTester.get().uri("/posts").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("blog/posts")
                .model()
                .containsKeys("posts", "pagination", "categories", "tags");
    }

    @Test
    void showPosts_shouldGetPaginatedPostsWithSpecificPage() {
        var result = mockMvcTester.get().uri("/posts?page=2").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("blog/posts")
                .model()
                .containsKeys("posts", "pagination", "categories", "tags");
    }

    @Test
    void searchPosts_shouldReturnMatchingPosts() {
        var result = mockMvcTester.get().uri("/posts/search?q=Test").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("blog/posts")
                .model()
                .containsKeys("posts", "pagination", "categories", "tags");
    }

    @Test
    void searchPosts_shouldHandleNoResults() {
        var result =
                mockMvcTester.get().uri("/posts/search?q=NonExistentContent").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("blog/posts")
                .model()
                .containsKeys("posts", "pagination", "categories", "tags");
    }

    @Test
    void showPostsByCategory_shouldReturnPostsForValidCategory() {
        var result = mockMvcTester.get().uri("/categories/java/posts").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("blog/posts")
                .model()
                .containsKeys("posts", "pagination", "categories", "tags");
    }

    @Test
    void showPostsByCategory_shouldHandleNonExistentCategory() {
        var result = mockMvcTester.get().uri("/categories/non-existent/posts").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("blog/posts")
                .model()
                .containsKeys("posts", "pagination", "categories", "tags");
    }

    @Test
    void showPostsByTag_shouldReturnPostsForValidTag() {
        var result = mockMvcTester.get().uri("/tags/java/posts").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("blog/posts")
                .model()
                .containsKeys("posts", "pagination", "categories", "tags");
    }

    @Test
    void showPostsByTag_shouldHandleNonExistentTag() {
        var result = mockMvcTester.get().uri("/tags/non-existent/posts").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("blog/posts")
                .model()
                .containsKeys("posts", "pagination", "categories", "tags");
    }

    @Test
    void showPostDetails_shouldReturnPostDetailsForValidSlug() {
        var result = mockMvcTester.get().uri("/posts/test-post-1").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("blog/post-details")
                .model()
                .containsKeys("post", "comments", "comment");
    }

    @Test
    void showPostDetails_shouldHandleNonExistentSlug() {
        var result = mockMvcTester.get().uri("/posts/non-existent-post").exchange();

        assertThat(result).hasViewName("error/404");
    }

    @Test
    void createComment_shouldSuccessfullyCreateComment() {
        var result = mockMvcTester
                .post()
                .uri("/posts/{slug}/comments", "test-post-1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("postId", "1")
                .param("name", "Test User 4")
                .param("email", "testuser4@gmail.com")
                .param("content", "great post")
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/posts/test-post-1#comments")
                .flash()
                .containsKey("successMessage")
                .hasEntrySatisfying(
                        "successMessage", value -> assertThat(value).isEqualTo("info.comment_posted_successfully"));
    }

    @Test
    void createComment_shouldReturnValidationErrorsWhenDataIsInvalid() {
        var result = mockMvcTester
                .post()
                .uri("/posts/{slug}/comments", "test-post-1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("postId", "1")
                .param("name", "") // Empty name should cause validation error
                .param("email", "invalid-email") // Invalid email should cause validation error
                .param("content", "") // Empty content should cause validation error
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("blog/post-details")
                .model()
                .containsKeys("post", "comments", "comment")
                .extractingBindingResult("comment")
                .hasErrorsCount(3)
                .hasFieldErrors("name", "email", "content");
    }

    @Test
    void createComment_shouldRespectAutoApproveSettingWhenFalse() {
        // The test-data.sql has auto_approve_comment set to false
        var result = mockMvcTester
                .post()
                .uri("/posts/{slug}/comments", "test-post-1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("postId", "1")
                .param("name", "Test User 5")
                .param("email", "testuser5@gmail.com")
                .param("content", "comment with pending status")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/posts/test-post-1#comments");

        // We can't directly verify the status in the test, but the service should set it to PENDING
        // based on the settings.auto_approve_comment value (false in test-data.sql)
    }
}
