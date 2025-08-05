package com.sivalabs.blog.admin.taxonomy;

import com.sivalabs.blog.shared.entities.Category;
import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);

    default Category getBySlug(String slug) {
        return findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for slug: " + slug));
    }

    boolean existsByLabelIgnoreCase(String label);

    boolean existsBySlugIgnoreCase(String slug);
}
