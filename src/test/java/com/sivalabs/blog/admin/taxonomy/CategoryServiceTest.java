package com.sivalabs.blog.admin.taxonomy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sivalabs.blog.BaseServiceTest;
import com.sivalabs.blog.shared.entities.Category;
import com.sivalabs.blog.shared.exceptions.BadRequestException;
import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CategoryServiceTest extends BaseServiceTest {
    @Autowired
    CategoryService categoryService;

    @Test
    void getCategoriesCount() {
        Long count = categoryService.getCategoriesCount();
        assertThat(count).isGreaterThan(0L);
    }

    @Test
    void findAll() {
        List<Category> categories = categoryService.findAll();

        assertThat(categories).isNotEmpty();
        assertThat(categories).hasSize(9);
    }

    @Test
    void getBySlug_shouldReturnCategory_whenSlugExists() {
        Category category = categoryService.getBySlug("java");

        assertThat(category).isNotNull();
        assertThat(category.getLabel()).isEqualTo("Java");
        assertThat(category.getSlug()).isEqualTo("java");
    }

    @Test
    void getBySlug_shouldThrowException_whenSlugDoesNotExist() {
        assertThatThrownBy(() -> categoryService.getBySlug("non-existent-slug"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found for slug: non-existent-slug");
    }

    @Test
    void createCategory_shouldCreateNewCategory_whenLabelAndSlugDoNotExist() {
        // Get initial count
        Long initialCount = categoryService.getCategoriesCount();

        // Create new category
        categoryService.createCategory("New Category", "new-category");

        // Verify count increased
        Long newCount = categoryService.getCategoriesCount();
        assertThat(newCount).isEqualTo(initialCount + 1);

        // Verify the new category exists
        Category category = categoryService.getBySlug("new-category");
        assertThat(category).isNotNull();
        assertThat(category.getLabel()).isEqualTo("New Category");
        assertThat(category.getSlug()).isEqualTo("new-category");
    }

    @Test
    void createCategory_shouldThrowException_whenLabelAlreadyExists() {
        assertThatThrownBy(() -> categoryService.createCategory("Java", "some-slug"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Category with label Java already exists");
    }

    @Test
    void createCategory_shouldThrowException_whenSlugAlreadyExists() {
        assertThatThrownBy(() -> categoryService.createCategory("Some Label", "java"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Category with slug java already exists");
    }
}
