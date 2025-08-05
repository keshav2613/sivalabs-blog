package com.sivalabs.blog.blog.domain;

import com.sivalabs.blog.shared.entities.Post;
import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlogPostRepository extends JpaRepository<Post, Long> {
    @Query(
            value =
                    """
    select p
    from Post p join fetch p.createdBy u join fetch p.category c
    where p.status = com.sivalabs.blog.shared.models.PostStatus.PUBLISHED
""",
            countQuery =
                    """
select count(p) from Post p where p.status = com.sivalabs.blog.shared.models.PostStatus.PUBLISHED
""")
    Page<Post> findPostSummaries(Pageable pageable);

    @Query(
            value =
                    """
    select p
    from Post p join fetch p.createdBy u join fetch p.category c
    where p.status = com.sivalabs.blog.shared.models.PostStatus.PUBLISHED
    and lower(p.title) like ?1 or lower(p.content) like ?1
""",
            countQuery =
                    """
select count(p) from Post p where p.status = com.sivalabs.blog.shared.models.PostStatus.PUBLISHED
and lower(p.title) like ?1 or lower(p.content) like ?1
""")
    Page<Post> searchPosts(String query, Pageable pageable);

    @Query(
            value =
                    """
    select p
    from Post p join fetch p.createdBy u join fetch p.category c
    where p.status = com.sivalabs.blog.shared.models.PostStatus.PUBLISHED
    and c.slug = :categorySlug
""",
            countQuery =
                    """
select count(p) from Post p where p.status = com.sivalabs.blog.shared.models.PostStatus.PUBLISHED
and p.category.slug = :categorySlug
""")
    Page<Post> findPostsByCategory(@Param("categorySlug") String categorySlug, Pageable pageable);

    @Query(
            """
    select p
    from Post p join fetch p.createdBy u join fetch p.category c
    left join p.tags t
    where p.status = com.sivalabs.blog.shared.models.PostStatus.PUBLISHED
    and t.slug = :tagSlug
""")
    Page<Post> findPostsByTag(@Param("tagSlug") String tagSlug, Pageable pageable);

    @Query(
            """
select p from Post p join fetch p.createdBy u join fetch p.category c
    left join p.tags t
where p.slug = :slug
and p.status = com.sivalabs.blog.shared.models.PostStatus.PUBLISHED
""")
    Optional<Post> findBySlug(String slug);

    default Post getBySlug(String slug) {
        return findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Post not found for slug: " + slug));
    }
}
