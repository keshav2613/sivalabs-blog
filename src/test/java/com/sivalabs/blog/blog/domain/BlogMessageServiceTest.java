package com.sivalabs.blog.blog.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.BaseServiceTest;
import com.sivalabs.blog.blog.domain.models.CreateMessageParams;
import com.sivalabs.blog.shared.entities.Message;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BlogMessageServiceTest extends BaseServiceTest {
    @Autowired
    BlogMessageService messageService;

    @Autowired
    BlogMessageRepository messageRepository;

    @Test
    void shouldCreateMessage() {
        // given
        CreateMessageParams params =
                new CreateMessageParams("Test Name", "test@example.com", "Test Subject", "Test Content");

        // when
        messageService.createMessage(params);

        // then
        Optional<Message> messageOptional = messageRepository.findAll().stream()
                .filter(m -> m.getEmail().equals("test@example.com"))
                .findFirst();

        assertThat(messageOptional).isPresent();
        Message message = messageOptional.get();
        assertThat(message.getName()).isEqualTo("Test Name");
        assertThat(message.getEmail()).isEqualTo("test@example.com");
        assertThat(message.getSubject()).isEqualTo("Test Subject");
        assertThat(message.getContent()).isEqualTo("Test Content");
        assertThat(message.getCreatedAt()).isNotNull();
    }
}
