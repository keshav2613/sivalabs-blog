package com.sivalabs.blog.admin.subscribers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

class AdminSubscribersControllerTests extends AbstractIT {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Test
    @WithUserDetails("admin@gmail.com")
    void showSubscribers_shouldDisplaySubscribersWithDefaultPagination() {
        var result = mockMvcTester.get().uri("/admin/subscribers").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/subscribers")
                .model()
                .containsKeys("subscribers", "pagination");
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void showSubscribers_shouldDisplaySubscribersWithSpecificPage() {
        var result = mockMvcTester.get().uri("/admin/subscribers?page=2").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/subscribers")
                .model()
                .containsKeys("subscribers", "pagination");
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void deleteSubscribers_shouldDeleteSingleSubscriber() {
        // Count subscribers before deletion
        long countBefore = subscriberRepository.count();

        // Delete subscriber with ID 1
        var result = mockMvcTester
                .delete()
                .uri("/admin/subscribers?subscriberIds=1")
                .with(csrf())
                .header("HX-Request", "true") // HTMX request header
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify subscriber was deleted
        long countAfter = subscriberRepository.count();
        assertThat(countAfter).isEqualTo(countBefore - 1);
        assertThat(subscriberRepository.findById(1L).isPresent()).isFalse();
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void deleteSubscribers_shouldDeleteMultipleSubscribers() {
        // Count subscribers before deletion
        long countBefore = subscriberRepository.count();

        // Delete subscribers with IDs 2 and 3
        var result = mockMvcTester
                .delete()
                .uri("/admin/subscribers?subscriberIds=2&subscriberIds=3")
                .with(csrf())
                .header("HX-Request", "true") // HTMX request header
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify subscribers were deleted
        long countAfter = subscriberRepository.count();
        assertThat(countAfter).isEqualTo(countBefore - 2);
        assertThat(subscriberRepository.findById(2L).isPresent()).isFalse();
        assertThat(subscriberRepository.findById(3L).isPresent()).isFalse();
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void deleteSubscribers_shouldHandleEmptySubscriberIds() {
        // Count subscribers before deletion
        long countBefore = subscriberRepository.count();

        // Try to delete with empty subscriber IDs
        var result = mockMvcTester
                .delete()
                .uri("/admin/subscribers")
                .with(csrf())
                .header("HX-Request", "true") // HTMX request header
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify no subscribers were deleted
        long countAfter = subscriberRepository.count();
        assertThat(countAfter).isEqualTo(countBefore);
    }
}
