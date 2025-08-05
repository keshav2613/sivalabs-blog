package com.sivalabs.blog.admin.posts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.sivalabs.blog.AbstractIT;
import com.sivalabs.blog.shared.entities.Post;
import com.sivalabs.blog.shared.models.PagedResult;
import com.sivalabs.blog.shared.models.PostStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

class AdminPostsControllerTests extends AbstractIT {

    @Autowired
    private PostRepository postRepository;

    @Test
    @WithUserDetails("admin@gmail.com")
    void showPosts_shouldDisplayAllPostsWithPagination() {
        var result = mockMvcTester.get().uri("/admin/posts").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/posts")
                .model()
                .containsKeys("posts", "pagination")
                .satisfies(model -> {
                    // Verify that the posts attribute contains posts from the database
                    var pagedResult = model.get("posts");
                    assertThat(pagedResult).isInstanceOf(PagedResult.class);

                    @SuppressWarnings("unchecked")
                    PagedResult<Post> posts = (PagedResult<Post>) pagedResult;
                    // There are 5 posts in test-data.sql
                    assertThat(posts.data()).isNotEmpty();
                    assertThat(posts.totalElements()).isEqualTo(5);
                });
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void showCreatePostForm_shouldDisplayEmptyForm() {
        var result = mockMvcTester.get().uri("/admin/posts/new").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/new-post")
                .model()
                .containsKeys("postForm", "categories", "postStatuses")
                .satisfies(model -> {
                    // Verify that the postForm attribute is an empty form
                    var postForm = model.get("postForm");
                    assertThat(postForm).isInstanceOf(AdminPostsController.PostForm.class);

                    // Verify that the postStatuses attribute contains all PostStatus values
                    var postStatuses = model.get("postStatuses");
                    assertThat(postStatuses).asInstanceOf(LIST).hasSize(PostStatus.values().length);
                });
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void createPost_shouldCreateNewPostWithValidData() {
        long countBefore = postRepository.count();

        var result = mockMvcTester
                .post()
                .uri("/admin/posts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Test New Post")
                .param("mdContent", "# Test New Post Content")
                .param("category", "java") // Using existing category slug from test-data.sql
                .param("tags", "java,spring-boot") // Using existing tag labels from test-data.sql
                .param("status", PostStatus.DRAFT.name())
                .exchange();

        assertThat(result).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/admin/posts");

        // Verify post was created
        long countAfter = postRepository.count();
        assertThat(countAfter).isEqualTo(countBefore + 1);
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void createPost_shouldReturnValidationErrorsWhenDataIsInvalid() {
        var result = mockMvcTester
                .post()
                .uri("/admin/posts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "") // Empty title should cause validation error
                .param("mdContent", "") // Empty content should cause validation error
                .param("category", "") // Empty category should cause validation error
                .param("tags", "java,spring-boot")
                .param("status", "") // Empty status should cause validation error
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/new-post")
                .model()
                .containsKeys("postForm", "categories", "postStatuses")
                .extractingBindingResult("postForm")
                .hasErrorsCount(4)
                .hasFieldErrors("title", "mdContent", "category", "status");
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void deletePosts_shouldDeleteSelectedPosts() {
        long countBefore = postRepository.count();

        // Delete posts with IDs 1 and 2
        var result = mockMvcTester
                .delete()
                .uri("/admin/posts?postIds=1&postIds=2")
                .with(csrf())
                .header("HX-Request", "true")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify posts were deleted
        long countAfter = postRepository.count();
        assertThat(countAfter).isEqualTo(countBefore - 2);

        // Verify the specific posts no longer exist
        assertThat(postRepository.findById(1L)).isEmpty();
        assertThat(postRepository.findById(2L)).isEmpty();
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void unpublishPosts_shouldUpdatePostStatusToDraft() {
        // Use post ID 3 which is PUBLISHED in test-data.sql
        var result = mockMvcTester
                .put()
                .uri("/admin/posts/unpublish?postIds=3")
                .with(csrf())
                .header("HX-Request", "true")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify the post status was updated to DRAFT
        var post = postRepository.findById(3L).orElseThrow();
        assertThat(post.getStatus()).isEqualTo(PostStatus.DRAFT);
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void publishPosts_shouldUpdatePostStatusToPublished() {
        // Use post ID 5 which is DRAFT in test-data.sql
        var result = mockMvcTester
                .put()
                .uri("/admin/posts/publish?postIds=5")
                .with(csrf())
                .header("HX-Request", "true")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify the post status was updated to PUBLISHED
        var post = postRepository.findById(5L).orElseThrow();
        assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void archivePosts_shouldUpdatePostStatusToArchived() {
        // Use post ID 4 which is PUBLISHED in test-data.sql
        var result = mockMvcTester
                .put()
                .uri("/admin/posts/archive?postIds=4")
                .with(csrf())
                .header("HX-Request", "true")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify the post status was updated to ARCHIVED
        var post = postRepository.findById(4L).orElseThrow();
        assertThat(post.getStatus()).isEqualTo(PostStatus.ARCHIVED);
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void showEditPostForm_shouldDisplayFormWithPostData() {
        // Use post ID 1 from test-data.sql
        var result = mockMvcTester.get().uri("/admin/posts/1/edit").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/edit-post")
                .model()
                .containsKeys("postForm", "postId", "categories", "postStatuses")
                .satisfies(model -> {
                    // Verify that the postForm attribute contains the post data
                    var postForm = model.get("postForm");
                    assertThat(postForm).isInstanceOf(AdminPostsController.PostForm.class);

                    AdminPostsController.PostForm form = (AdminPostsController.PostForm) postForm;
                    assertThat(form.title()).isEqualTo("Test Post 1");
                    assertThat(form.mdContent()).isEqualTo("# Test Post 1");
                    assertThat(form.category()).isEqualTo("java");
                    assertThat(form.status()).isEqualTo(PostStatus.PUBLISHED);

                    // Verify that the postId attribute is correct
                    assertThat(model.get("postId")).isEqualTo(1L);

                    // Verify that the postStatuses attribute contains all PostStatus values
                    var postStatuses = model.get("postStatuses");
                    assertThat(postStatuses).asInstanceOf(LIST).hasSize(PostStatus.values().length);
                });
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void updatePost_shouldUpdatePostWithValidData() {
        // Use post ID 1 from test-data.sql
        var result = mockMvcTester
                .put()
                .uri("/admin/posts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Updated Test Post 1")
                .param("mdContent", "# Updated Test Post 1 Content")
                .param("category", "spring-boot") // Using existing category slug from test-data.sql
                .param("tags", "java,spring-boot,best-practices") // Using existing tag labels from test-data.sql
                .param("status", PostStatus.DRAFT.name())
                .exchange();

        assertThat(result).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/admin/posts");

        // Verify the post was updated with the correct data
        var post = postRepository.findById(1L).orElseThrow();
        assertThat(post.getTitle()).isEqualTo("Updated Test Post 1");
        assertThat(post.getMdContent()).isEqualTo("# Updated Test Post 1 Content");
        assertThat(post.getCategory().getId()).isEqualTo(2L); // spring-boot category has ID 2 in test-data.sql
        assertThat(post.getStatus()).isEqualTo(PostStatus.DRAFT);
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void updatePost_shouldReturnValidationErrorsWhenDataIsInvalid() {
        // Use post ID 1 from test-data.sql
        var result = mockMvcTester
                .put()
                .uri("/admin/posts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "") // Empty title should cause validation error
                .param("mdContent", "") // Empty content should cause validation error
                .param("category", "") // Empty category should cause validation error
                .param("tags", "java,spring-boot")
                .param("status", "") // Empty status should cause validation error
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/edit-post")
                .model()
                .containsKeys("postForm", "postId", "categories", "postStatuses")
                .extractingBindingResult("postForm")
                .hasErrorsCount(4)
                .hasFieldErrors("title", "mdContent", "category", "status");
    }
}
