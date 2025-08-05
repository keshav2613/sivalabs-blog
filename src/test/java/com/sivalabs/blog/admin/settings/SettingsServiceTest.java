package com.sivalabs.blog.admin.settings;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.BaseServiceTest;
import com.sivalabs.blog.shared.entities.Settings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SettingsServiceTest extends BaseServiceTest {
    @Autowired
    private SettingsService settingsService;

    @Test
    void getSettings_shouldReturnSettings() {
        // When
        Settings settings = settingsService.getSettings();

        // Then
        assertThat(settings).isNotNull();
        assertThat(settings.getId()).isEqualTo(1L);
        assertThat(settings.getAdminContactName()).isEqualTo("Test Admin");
        assertThat(settings.getAdminContactEmail()).isEqualTo("test.admin@example.com");
        assertThat(settings.getAdminContactAddress()).isEqualTo("Test Address");
        assertThat(settings.getAdminContactTwitter()).isEqualTo("https://twitter.com/testadmin");
        assertThat(settings.getAdminContactGithub()).isEqualTo("https://github.com/testadmin");
        assertThat(settings.getAdminContactLinkedin()).isEqualTo("https://linkedin.com/in/testadmin");
        assertThat(settings.getAdminContactYoutube()).isEqualTo("https://youtube.com/testadmin");
        assertThat(settings.getAutoApproveComment()).isFalse();
    }

    @Test
    void update_shouldUpdateSettings() {
        // Given
        Settings originalSettings = settingsService.getSettings();
        Settings updatedSettings = new Settings(
                originalSettings.getId(),
                "Updated Admin",
                "updated.admin@example.com",
                "Updated Address",
                "https://twitter.com/updatedadmin",
                "https://github.com/updatedadmin",
                "https://linkedin.com/in/updatedadmin",
                "https://youtube.com/updatedadmin",
                true);

        // When
        settingsService.update(updatedSettings);

        // Then
        Settings retrievedSettings = settingsService.getSettings();
        assertThat(retrievedSettings).isNotNull();
        assertThat(retrievedSettings.getId()).isEqualTo(originalSettings.getId());
        assertThat(retrievedSettings.getAdminContactName()).isEqualTo("Updated Admin");
        assertThat(retrievedSettings.getAdminContactEmail()).isEqualTo("updated.admin@example.com");
        assertThat(retrievedSettings.getAdminContactAddress()).isEqualTo("Updated Address");
        assertThat(retrievedSettings.getAdminContactTwitter()).isEqualTo("https://twitter.com/updatedadmin");
        assertThat(retrievedSettings.getAdminContactGithub()).isEqualTo("https://github.com/updatedadmin");
        assertThat(retrievedSettings.getAdminContactLinkedin()).isEqualTo("https://linkedin.com/in/updatedadmin");
        assertThat(retrievedSettings.getAdminContactYoutube()).isEqualTo("https://youtube.com/updatedadmin");
        assertThat(retrievedSettings.getAutoApproveComment()).isTrue();
    }

    @Test
    void update_shouldHandleNullValues() {
        // Given
        Settings originalSettings = settingsService.getSettings();
        Settings settingsWithNulls = new Settings(
                originalSettings.getId(),
                "Admin With Nulls",
                "admin.nulls@example.com",
                "Address With Nulls",
                null, // Twitter is null
                null, // GitHub is null
                null, // LinkedIn is null
                null, // YouTube is null
                true);

        // When
        settingsService.update(settingsWithNulls);

        // Then
        Settings retrievedSettings = settingsService.getSettings();
        assertThat(retrievedSettings).isNotNull();
        assertThat(retrievedSettings.getId()).isEqualTo(originalSettings.getId());
        assertThat(retrievedSettings.getAdminContactName()).isEqualTo("Admin With Nulls");
        assertThat(retrievedSettings.getAdminContactEmail()).isEqualTo("admin.nulls@example.com");
        assertThat(retrievedSettings.getAdminContactAddress()).isEqualTo("Address With Nulls");
        assertThat(retrievedSettings.getAdminContactTwitter()).isNull();
        assertThat(retrievedSettings.getAdminContactGithub()).isNull();
        assertThat(retrievedSettings.getAdminContactLinkedin()).isNull();
        assertThat(retrievedSettings.getAdminContactYoutube()).isNull();
        assertThat(retrievedSettings.getAutoApproveComment()).isTrue();
    }

    @Test
    void update_shouldHandleEmptyValues() {
        // Given
        Settings originalSettings = settingsService.getSettings();
        Settings settingsWithEmptyValues = new Settings(
                originalSettings.getId(),
                "Admin With Empty Values",
                "admin.empty@example.com",
                "Address With Empty Values",
                "", // Twitter is empty
                "", // GitHub is empty
                "", // LinkedIn is empty
                "", // YouTube is empty
                false);

        // When
        settingsService.update(settingsWithEmptyValues);

        // Then
        Settings retrievedSettings = settingsService.getSettings();
        assertThat(retrievedSettings).isNotNull();
        assertThat(retrievedSettings.getId()).isEqualTo(originalSettings.getId());
        assertThat(retrievedSettings.getAdminContactName()).isEqualTo("Admin With Empty Values");
        assertThat(retrievedSettings.getAdminContactEmail()).isEqualTo("admin.empty@example.com");
        assertThat(retrievedSettings.getAdminContactAddress()).isEqualTo("Address With Empty Values");
        assertThat(retrievedSettings.getAdminContactTwitter()).isEmpty();
        assertThat(retrievedSettings.getAdminContactGithub()).isEmpty();
        assertThat(retrievedSettings.getAdminContactLinkedin()).isEmpty();
        assertThat(retrievedSettings.getAdminContactYoutube()).isEmpty();
        assertThat(retrievedSettings.getAutoApproveComment()).isFalse();
    }
}
