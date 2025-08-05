package com.sivalabs.blog.admin.messages;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.BaseServiceTest;
import com.sivalabs.blog.shared.entities.Message;
import com.sivalabs.blog.shared.models.PagedResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MessageServiceTest extends BaseServiceTest {
    @Autowired
    MessageService messageService;

    @Autowired
    MessageRepository messageRepository;

    @Test
    void shouldDeleteMessages() {
        // given
        long initialCount = messageRepository.count();
        assertThat(initialCount).isGreaterThanOrEqualTo(3); // From test-data.sql
        List<Long> messageIds = List.of(1L, 2L);

        // when
        messageService.deleteMessages(messageIds);

        // then
        long finalCount = messageRepository.count();
        assertThat(finalCount).isEqualTo(initialCount - 2);
        assertThat(messageRepository.existsById(1L)).isFalse();
        assertThat(messageRepository.existsById(2L)).isFalse();
        assertThat(messageRepository.existsById(3L)).isTrue();
    }

    @Test
    void shouldNotDeleteMessagesWhenIdsListIsEmpty() {
        // given
        long initialCount = messageRepository.count();
        List<Long> messageIds = List.of();

        // when
        messageService.deleteMessages(messageIds);

        // then
        long finalCount = messageRepository.count();
        assertThat(finalCount).isEqualTo(initialCount);
    }

    @Test
    void shouldNotDeleteMessagesWhenIdsListIsNull() {
        // given
        long initialCount = messageRepository.count();

        // when
        messageService.deleteMessages(null);

        // then
        long finalCount = messageRepository.count();
        assertThat(finalCount).isEqualTo(initialCount);
    }

    @Test
    void shouldGetMessagesWithPagination() {
        // given
        int pageNo = 1;

        // when
        PagedResult<Message> result = messageService.getMessages(pageNo);

        // then
        assertThat(result).isNotNull();
        assertThat(result.data()).isNotEmpty();
        assertThat(result.currentPageNo()).isEqualTo(1);
        assertThat(result.totalElements()).isGreaterThanOrEqualTo(3); // From test-data.sql
    }
}
