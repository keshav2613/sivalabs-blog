package com.sivalabs.blog.admin.taxonomy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

class AdminTagsControllerTests extends AbstractIT {

    @Autowired
    private TagRepository tagRepository;

    @Test
    @WithUserDetails("admin@gmail.com")
    void showTags_shouldDisplayAllTags() {
        var result = mockMvcTester.get().uri("/admin/tags").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/tags")
                .model()
                .containsKey("tags")
                .satisfies(model -> {
                    // Verify that the tags attribute contains all tags from the database
                    assertThat(model.get("tags")).asList().hasSizeGreaterThanOrEqualTo(12); // Based on test-data.sql
                });
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void deleteTags_shouldDeleteSingleTag() {
        // Count tags before deletion
        long countBefore = tagRepository.count();

        // Delete tag with ID 1
        var result = mockMvcTester
                .delete()
                .uri("/admin/tags?tagIds=1")
                .with(csrf())
                .header("HX-Request", "true") // HTMX request header
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify tag was deleted
        long countAfter = tagRepository.count();
        assertThat(countAfter).isEqualTo(countBefore - 1);
        assertThat(tagRepository.findById(1L).isPresent()).isFalse();
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void deleteTags_shouldDeleteMultipleTags() {
        // Count tags before deletion
        long countBefore = tagRepository.count();

        // Delete tags with IDs 2 and 3
        var result = mockMvcTester
                .delete()
                .uri("/admin/tags?tagIds=2&tagIds=3")
                .with(csrf())
                .header("HX-Request", "true") // HTMX request header
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify tags were deleted
        long countAfter = tagRepository.count();
        assertThat(countAfter).isEqualTo(countBefore - 2);
        assertThat(tagRepository.findById(2L).isPresent()).isFalse();
        assertThat(tagRepository.findById(3L).isPresent()).isFalse();
    }
}
