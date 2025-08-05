package com.sivalabs.blog.admin.posts;

import com.sivalabs.blog.shared.entities.Comment;
import com.sivalabs.blog.shared.models.CommentStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c join fetch c.post")
    Page<Comment> findComments(Pageable pageable);

    @Modifying
    @Query("delete from Comment c where c.post.id in :postIds")
    void deleteByPostIds(List<Long> postIds);

    @Modifying
    @Query("update Comment c set c.status= :status where c.id in :commentIds")
    void updateStatus(List<Long> commentIds, CommentStatus status);
}
