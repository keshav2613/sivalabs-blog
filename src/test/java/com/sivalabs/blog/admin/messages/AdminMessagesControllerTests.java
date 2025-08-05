package com.sivalabs.blog.admin.messages;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

class AdminMessagesControllerTests extends AbstractIT {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    @WithUserDetails("admin@gmail.com")
    void showMessages_shouldDisplayMessagesWithDefaultPagination() {
        var result = mockMvcTester.get().uri("/admin/messages").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/messages")
                .model()
                .containsKeys("messages", "pagination");
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void showMessages_shouldDisplayMessagesWithSpecificPage() {
        var result = mockMvcTester.get().uri("/admin/messages?page=2").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/messages")
                .model()
                .containsKeys("messages", "pagination");
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void deleteMessages_shouldDeleteSingleMessage() {
        // Count messages before deletion
        long countBefore = messageRepository.count();

        // Delete message with ID 1
        var result = mockMvcTester
                .delete()
                .uri("/admin/messages?messageIds=1")
                .with(csrf())
                .header("HX-Request", "true") // HTMX request header
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify message was deleted
        long countAfter = messageRepository.count();
        assertThat(countAfter).isEqualTo(countBefore - 1);
        assertThat(messageRepository.findById(1L).isPresent()).isFalse();
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void deleteMessages_shouldDeleteMultipleMessages() {
        // Count messages before deletion
        long countBefore = messageRepository.count();

        // Delete messages with IDs 2 and 3
        var result = mockMvcTester
                .delete()
                .uri("/admin/messages?messageIds=2&messageIds=3")
                .with(csrf())
                .header("HX-Request", "true") // HTMX request header
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        // Verify messages were deleted
        long countAfter = messageRepository.count();
        assertThat(countAfter).isEqualTo(countBefore - 2);
        assertThat(messageRepository.findById(2L).isPresent()).isFalse();
        assertThat(messageRepository.findById(3L).isPresent()).isFalse();
    }
}
