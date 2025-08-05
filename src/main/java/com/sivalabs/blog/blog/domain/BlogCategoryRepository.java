package com.sivalabs.blog.blog.domain;

import com.sivalabs.blog.blog.domain.models.CategorySummaryDTO;
import com.sivalabs.blog.shared.entities.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlogCategoryRepository extends JpaRepository<Category, Long> {
    @Query(
            """
    SELECT new com.sivalabs.blog.blog.domain.models.CategorySummaryDTO(
        c.id, c.label, c.slug, COUNT(CASE WHEN p.status = 'PUBLISHED' THEN 1 END)
    )
    FROM Category c
    LEFT JOIN Post p ON p.category.id = c.id
    GROUP BY c.id, c.label, c.slug
    ORDER BY c.label
    """)
    List<CategorySummaryDTO> getCategoriesWithPostCounts();
}
