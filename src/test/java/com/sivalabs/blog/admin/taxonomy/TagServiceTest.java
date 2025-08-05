package com.sivalabs.blog.admin.taxonomy;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.BaseServiceTest;
import com.sivalabs.blog.shared.entities.Tag;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TagServiceTest extends BaseServiceTest {
    @Autowired
    private TagService tagService;

    @Test
    void getTagsCount_shouldReturnTotalNumberOfTags() {
        // When
        Long count = tagService.getTagsCount();

        // Then
        assertThat(count).isEqualTo(12L);
    }

    @Test
    void findAll_shouldReturnAllTags() {
        // When
        List<Tag> tags = tagService.findAll();

        // Then
        assertThat(tags).isNotNull();
        assertThat(tags).hasSize(12);

        // Verify some specific tags exist
        assertThat(tags).extracting(Tag::getLabel).contains("Java", "Spring Boot", "Spring Security", "Testcontainers");

        // Verify the structure of a specific tag
        Tag javaTag = tags.stream().filter(tag -> tag.getId() == 1L).findFirst().orElseThrow();

        assertThat(javaTag.getLabel()).isEqualTo("Java");
        assertThat(javaTag.getSlug()).isEqualTo("java");
    }

    @Test
    void deleteTags_shouldDeleteSpecifiedTags() {
        // Given
        Long initialCount = tagService.getTagsCount();
        List<Long> tagIdsToDelete = List.of(1L, 2L);

        // When
        tagService.deleteTags(tagIdsToDelete);

        // Then
        Long newCount = tagService.getTagsCount();
        assertThat(newCount).isEqualTo(initialCount - 2);

        List<Tag> remainingTags = tagService.findAll();
        assertThat(remainingTags).extracting(Tag::getId).doesNotContain(1L, 2L).contains(3L, 4L, 5L);
    }

    @Test
    void deleteTags_shouldDoNothingWhenTagIdsIsNull() {
        // Given
        Long initialCount = tagService.getTagsCount();

        // When
        tagService.deleteTags(null);

        // Then
        Long newCount = tagService.getTagsCount();
        assertThat(newCount).isEqualTo(initialCount);
    }

    @Test
    void deleteTags_shouldDoNothingWhenTagIdsIsEmpty() {
        // Given
        Long initialCount = tagService.getTagsCount();

        // When
        tagService.deleteTags(List.of());

        // Then
        Long newCount = tagService.getTagsCount();
        assertThat(newCount).isEqualTo(initialCount);
    }
}
