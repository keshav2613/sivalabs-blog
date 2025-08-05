package com.sivalabs.blog.blog.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

class BlogSubscriberControllerTests extends AbstractIT {

    @Test
    @WithUserDetails("admin@gmail.com")
    void shouldSubscribeWithNewEmail() {
        var result = mockMvcTester
                .post()
                .uri("/newsletter/subscribe")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .queryParam("email", "testuser4@gmail.com")
                .header("HX-Request", "true")
                .exchange();
        assertThat(result).bodyText().isEqualTo("Subscribed successfully");
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void shouldHandleSubscriptionWithExistingEmail() {
        // Using an email that already exists in test-data.sql
        var result = mockMvcTester
                .post()
                .uri("/newsletter/subscribe")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .queryParam("email", "subscriber1@example.com")
                .header("HX-Request", "true")
                .exchange();

        assertThat(result).bodyText().isEqualTo("Subscribed successfully");
    }

    @Test
    void shouldAllowSubscriptionWithoutAuthentication() {
        // Test without @WithUserDetails annotation to verify if authentication is required
        var result = mockMvcTester
                .post()
                .uri("/newsletter/subscribe")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .queryParam("email", "unauthenticated@example.com")
                .header("HX-Request", "true")
                .exchange();

        assertThat(result).bodyText().isEqualTo("Subscribed successfully");
    }
}
