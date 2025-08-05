package com.sivalabs.blog.admin.subscribers;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.BaseServiceTest;
import com.sivalabs.blog.shared.entities.Subscriber;
import com.sivalabs.blog.shared.models.PagedResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SubscriberServiceTest extends BaseServiceTest {
    @Autowired
    private SubscriberService subscriberService;

    @Test
    void getAllActiveSubscribers_shouldReturnAllVerifiedSubscribers() {
        // When
        List<String> activeSubscribers = subscriberService.getAllActiveSubscribers();

        // Then
        assertThat(activeSubscribers).isNotEmpty();
        assertThat(activeSubscribers).hasSize(3);
        assertThat(activeSubscribers)
                .contains("subscriber1@example.com", "subscriber2@example.com", "subscriber5@example.com");
    }

    @Test
    void getSubscribers_shouldReturnPagedSubscribers() {
        // When
        PagedResult<Subscriber> result = subscriberService.getSubscribers(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.data()).isNotEmpty();
        assertThat(result.data()).hasSize(5);
        assertThat(result.totalElements()).isEqualTo(5);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.currentPageNo()).isEqualTo(1);
        assertThat(result.hasPreviousPage()).isFalse();
        assertThat(result.hasNextPage()).isFalse();

        // Verify order (descending by createdAt)
        assertThat(result.data().get(0).getEmail()).isEqualTo("subscriber5@example.com");
        assertThat(result.data().get(1).getEmail()).isEqualTo("subscriber4@example.com");
        assertThat(result.data().get(2).getEmail()).isEqualTo("subscriber3@example.com");
        assertThat(result.data().get(3).getEmail()).isEqualTo("subscriber2@example.com");
        assertThat(result.data().get(4).getEmail()).isEqualTo("subscriber1@example.com");
    }

    @Test
    void getSubscribers_shouldHandleNegativePageNumber() {
        // When
        PagedResult<Subscriber> result = subscriberService.getSubscribers(-1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.data()).isNotEmpty();
        assertThat(result.data()).hasSize(5);
        assertThat(result.totalElements()).isEqualTo(5);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.currentPageNo()).isEqualTo(1);
    }

    @Test
    void deleteSubscribers_shouldDeleteSpecifiedSubscribers() {
        // Given
        long initialCount = subscriberService.getSubscribers(1).totalElements();
        List<Long> subscriberIdsToDelete = List.of(1L, 2L);

        // When
        subscriberService.deleteSubscribers(subscriberIdsToDelete);

        // Then
        PagedResult<Subscriber> result = subscriberService.getSubscribers(1);
        assertThat(result.totalElements()).isEqualTo(initialCount - 2);

        List<Subscriber> remainingSubscribers = result.data();
        assertThat(remainingSubscribers)
                .extracting(Subscriber::getId)
                .doesNotContain(1L, 2L)
                .contains(3L, 4L, 5L);
    }

    @Test
    void deleteSubscribers_shouldDoNothingWhenSubscriberIdsIsNull() {
        // Given
        long initialCount = subscriberService.getSubscribers(1).totalElements();

        // When
        subscriberService.deleteSubscribers(null);

        // Then
        PagedResult<Subscriber> result = subscriberService.getSubscribers(1);
        assertThat(result.totalElements()).isEqualTo(initialCount);
    }

    @Test
    void deleteSubscribers_shouldDoNothingWhenSubscriberIdsIsEmpty() {
        // Given
        long initialCount = subscriberService.getSubscribers(1).totalElements();

        // When
        subscriberService.deleteSubscribers(List.of());

        // Then
        PagedResult<Subscriber> result = subscriberService.getSubscribers(1);
        assertThat(result.totalElements()).isEqualTo(initialCount);
    }
}
