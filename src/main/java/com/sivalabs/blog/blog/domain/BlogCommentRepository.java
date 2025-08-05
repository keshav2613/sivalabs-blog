package com.sivalabs.blog.blog.domain;

import com.sivalabs.blog.shared.entities.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlogCommentRepository extends JpaRepository<Comment, Long> {
    @Query(
            """
            select c from Comment c
            where c.post.slug = :postSlug
            and c.status=com.sivalabs.blog.shared.models.CommentStatus.APPROVED
            """)
    List<Comment> findCommentsByPost(String postSlug);
}
