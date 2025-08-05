package com.sivalabs.blog.admin.posts;

import com.sivalabs.blog.shared.entities.Post;
import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import com.sivalabs.blog.shared.models.PostStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(
            value = """
    select p
    from Post p join fetch p.createdBy u join fetch p.category c
""",
            countQuery = """
select count(p) from Post p
""")
    Page<Post> findAllPostSummaries(Pageable pageable);

    @Query(
            """
select p from Post p join fetch p.createdBy u join fetch p.category c
    left join fetch p.tags t
where p.id = :id
""")
    Optional<Post> findByIdWithDetails(@Param("id") Long id);

    default Post getByIdWithDetails(Long id) {
        return findByIdWithDetails(id).orElseThrow(() -> new ResourceNotFoundException("Post not found for id: " + id));
    }

    @Query(
            """
            select p from Post p join fetch p.createdBy u join fetch p.category c
            where p.status = com.sivalabs.blog.shared.models.PostStatus.PUBLISHED
            and p.createdAt >= :start and p.createdAt <= :end
            """)
    List<Post> findByCreatedDate(LocalDateTime start, LocalDateTime end);

    @Modifying
    @Query("""
    update Post p set p.status = :status
    where p.id in :postIds
    """)
    void updateStatus(List<Long> postIds, PostStatus status);
}
