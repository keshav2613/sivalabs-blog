package com.sivalabs.blog.admin.taxonomy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

class AdminCategoriesControllerTests extends AbstractIT {

    @Test
    @WithUserDetails("admin@gmail.com")
    void showCategories_shouldDisplayAllCategoriesAndEmptyForm() {
        var result = mockMvcTester.get().uri("/admin/categories").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/categories")
                .model()
                .containsKeys("categories", "category");
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void createCategory_shouldCreateNewCategoryWithValidData() {
        var result = mockMvcTester
                .post()
                .uri("/admin/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("label", "New Test Category")
                .param("slug", "new-test-category")
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/admin/categories")
                .flash()
                .containsKey("successMessage")
                .hasEntrySatisfying(
                        "successMessage", value -> assertThat(value).isEqualTo("info.category_created_successfully"));
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void createCategory_shouldReturnValidationErrorsWhenDataIsInvalid() {
        var result = mockMvcTester
                .post()
                .uri("/admin/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("label", "") // Empty label should cause validation error
                .param("slug", "") // Empty slug should cause validation error
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/categories")
                .model()
                .containsKey("categories")
                .extractingBindingResult("category")
                .hasErrorsCount(2)
                .hasFieldErrors("label", "slug");
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void createCategory_shouldHandleDuplicateLabelException() {
        // Using an existing label from test-data.sql
        var result = mockMvcTester
                .post()
                .uri("/admin/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("label", "Java") // Existing label in test-data.sql
                .param("slug", "unique-test-slug")
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/categories")
                .model()
                .containsKey("categories")
                .extractingBindingResult("category")
                .hasErrorsCount(1);
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void createCategory_shouldHandleDuplicateSlugException() {
        // Using an existing slug from test-data.sql
        var result = mockMvcTester
                .post()
                .uri("/admin/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("label", "Unique Test Label")
                .param("slug", "java") // Existing slug in test-data.sql
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/categories")
                .model()
                .containsKey("categories")
                .extractingBindingResult("category")
                .hasErrorsCount(1);
    }
}
