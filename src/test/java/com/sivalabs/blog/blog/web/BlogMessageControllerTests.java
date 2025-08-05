package com.sivalabs.blog.blog.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class BlogMessageControllerTests extends AbstractIT {

    @Test
    void contact_shouldDisplayContactPage() {
        var result = mockMvcTester.get().uri("/contact").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("blog/contact")
                .model()
                .containsKeys("contact", "message");
    }

    @Test
    void postMessage_shouldCreateMessageSuccessfully() {
        var result = mockMvcTester
                .post()
                .uri("/contact/messages")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Test User")
                .param("email", "testuser@example.com")
                .param("subject", "Test Subject")
                .param("content", "This is a test message content")
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/contact")
                .flash()
                .containsKey("successMessage")
                .hasEntrySatisfying("successMessage", value -> assertThat(value).isEqualTo("info.message_sent"));
    }

    @Test
    void postMessage_shouldReturnValidationErrorsWhenDataIsInvalid() {
        var result = mockMvcTester
                .post()
                .uri("/contact/messages")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "") // Empty name should cause validation error
                .param("email", "invalid-email") // Invalid email should cause validation error
                .param("subject", "") // Empty subject should cause validation error
                .param("content", "") // Empty content should cause validation error
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("blog/contact")
                .model()
                .containsKeys("contact", "message")
                .extractingBindingResult("message")
                .hasErrorsCount(4)
                .hasFieldErrors("name", "email", "subject", "content");
    }
}
