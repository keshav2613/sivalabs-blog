package com.sivalabs.blog.blog.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.BaseServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BlogSubscriberServiceTest extends BaseServiceTest {
    @Autowired
    private BlogSubscriberService subscriberService;

    @Autowired
    private BlogSubscriberRepository subscriberRepository;

    @Test
    void subscribe_shouldAddNewSubscriber() {
        // Given
        String newEmail = "newsubscriber@example.com";
        long initialCount = subscriberRepository.count();

        // When
        subscriberService.subscribe(newEmail);

        // Then
        long newCount = subscriberRepository.count();
        assertThat(newCount).isEqualTo(initialCount + 1);
    }

    @Test
    void subscribe_shouldNotAddDuplicateSubscriber() {
        // Given
        String existingEmail = "subscriber1@example.com";
        long initialCount = subscriberRepository.count();

        // When
        subscriberService.subscribe(existingEmail);

        // Then
        long newCount = subscriberRepository.count();
        assertThat(newCount).isEqualTo(initialCount);
    }
}
