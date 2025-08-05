package com.sivalabs.blog.shared.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MessageTests {
    @Test
    void getContentSummary_shouldReturnTruncatedContent() {
        // Given
        String content =
                "This is a test content with a long text abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
        Message message = new Message(1L, "Test", "test@example.com", "Test", content);

        // When
        String summary = message.getContentSummary();

        // Then
        assertThat(summary)
                .isEqualTo(
                        "This is a test content with a long text abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefgh...");
    }

    @Test
    void getContentSummary_shouldHandleNullContent() {
        // Given a message with null content (we'll create a mock for this test)
        Message messageWithNullContent = new Message(999L, "Test", "test@example.com", "Test", null);

        // When
        String summary = messageWithNullContent.getContentSummary();

        // Then
        assertThat(summary).isEmpty();
    }

    @Test
    void getContentSummary_shouldTruncateLongContent() {
        // Given a message with long content (we'll create a mock for this test)

        Message messageWithLongContent = new Message(999L, "Test", "test@example.com", "Test", "a".repeat(200));

        // When
        String summary = messageWithLongContent.getContentSummary();

        // Then
        assertThat(summary).hasSize(103); // 100 chars + "..."
        assertThat(summary).endsWith("...");
    }
}
