package com.sivalabs.blog.admin.taxonomy;

import com.sivalabs.blog.shared.entities.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByLabelIgnoreCase(String label);

    Optional<Tag> findBySlugIgnoreCase(String slug);

    @Modifying
    @Query(value = "delete from posts_tags pt where pt.tag_id in :tagIds", nativeQuery = true)
    void deletePostTags(List<Long> tagIds);
}
