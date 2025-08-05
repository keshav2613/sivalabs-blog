package com.sivalabs.blog.admin.posts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.sivalabs.blog.AbstractIT;
import com.sivalabs.blog.shared.entities.Comment;
import com.sivalabs.blog.shared.models.CommentStatus;
import com.sivalabs.blog.shared.models.PagedResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;

class AdminCommentsControllerTests extends AbstractIT {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @WithUserDetails("admin@gmail.com")
    void showComments_shouldDisplayAllCommentsWithPagination() {
        var result = mockMvcTester.get().uri("/admin/comments").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/comments")
                .model()
                .containsKeys("comments", "pagination")
                .satisfies(model -> {
                    // Verify that the comments attribute contains comments from the database
                    var pagedResult = model.get("comments");
                    assertThat(pagedResult).isInstanceOf(PagedResult.class);

                    @SuppressWarnings("unchecked")
                    PagedResult<Comment> comments = (PagedResult<Comment>) pagedResult;
                    // There are 5 comments in test-data.sql
                    assertThat(comments.data()).isNotEmpty();
                    assertThat(comments.totalElements()).isEqualTo(5);
                });
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void deleteComments_shouldDeleteSelectedComments() {
        long countBefore = commentRepository.count();

        // Delete comments with IDs 1 and 2
        var result = mockMvcTester
                .delete()
                .uri("/admin/comments?commentIds=1&commentIds=2")
                .with(csrf())
                .header("HX-Request", "true")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify comments were deleted
        long countAfter = commentRepository.count();
        assertThat(countAfter).isEqualTo(countBefore - 2);

        // Verify the specific comments no longer exist
        assertThat(commentRepository.findById(1L)).isEmpty();
        assertThat(commentRepository.findById(2L)).isEmpty();
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void approveComments_shouldUpdateCommentStatusToApproved() {
        // Use comment ID 4 which is PENDING in test-data.sql
        var result = mockMvcTester
                .put()
                .uri("/admin/comments/approve?commentIds=4")
                .with(csrf())
                .header("HX-Request", "true")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify the comment status was updated to APPROVED
        var comment = commentRepository.findById(4L).orElseThrow();
        assertThat(comment.getStatus()).isEqualTo(CommentStatus.APPROVED);
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void rejectComments_shouldUpdateCommentStatusToSpam() {
        // Use comment ID 3 which is APPROVED in test-data.sql
        var result = mockMvcTester
                .put()
                .uri("/admin/comments/spam?commentIds=3")
                .with(csrf())
                .header("HX-Request", "true")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify the comment status was updated to SPAM
        var comment = commentRepository.findById(3L).orElseThrow();
        assertThat(comment.getStatus()).isEqualTo(CommentStatus.SPAM);
    }
}
