package com.sivalabs.blog.blog.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.BaseServiceTest;
import com.sivalabs.blog.blog.domain.models.CategorySummaryDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BlogCategoryServiceTest extends BaseServiceTest {
    @Autowired
    BlogCategoryService categoryService;

    @Test
    void getCategoriesWithPostCounts() {
        List<CategorySummaryDTO> categoriesWithCounts = categoryService.getCategoriesWithPostCounts();

        assertThat(categoriesWithCounts).isNotEmpty();

        assertThat(categoriesWithCounts)
                .anyMatch(c -> c.label().equals("Java") && c.slug().equals("java") && c.postCount() == 2);

        assertThat(categoriesWithCounts)
                .anyMatch(c -> c.label().equals("Spring Boot") && c.slug().equals("spring-boot") && c.postCount() == 2);

        assertThat(categoriesWithCounts)
                .anyMatch(c ->
                        c.label().equals("Microservices") && c.slug().equals("microservices") && c.postCount() == 0);
    }
}
