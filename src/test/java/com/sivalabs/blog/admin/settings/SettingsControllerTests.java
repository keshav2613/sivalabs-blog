package com.sivalabs.blog.admin.settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.sivalabs.blog.AbstractIT;
import com.sivalabs.blog.shared.entities.Settings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

class SettingsControllerTests extends AbstractIT {

    @Autowired
    private SettingsService settingsService;

    @Test
    @WithUserDetails("admin@gmail.com")
    void showSettings_shouldDisplaySettingsForm() {
        var result = mockMvcTester.get().uri("/admin/settings").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/settings")
                .model()
                .containsKey("settings")
                .satisfies(model -> {
                    Settings settings = (Settings) model.get("settings");
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
                });
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void updateSettings_shouldUpdateSettingsWithValidData() {
        var result = mockMvcTester
                .put()
                .uri("/admin/settings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("adminContactName", "Updated Admin")
                .param("adminContactEmail", "updated.admin@example.com")
                .param("adminContactAddress", "Updated Address")
                .param("adminContactTwitter", "https://twitter.com/updatedadmin")
                .param("adminContactGithub", "https://github.com/updatedadmin")
                .param("adminContactLinkedin", "https://linkedin.com/in/updatedadmin")
                .param("adminContactYoutube", "https://youtube.com/updatedadmin")
                .param("autoApproveComment", "true")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/admin/settings");

        // Verify settings were updated in the database
        Settings updatedSettings = settingsService.getSettings();
        assertThat(updatedSettings).isNotNull();
        assertThat(updatedSettings.getId()).isEqualTo(1L);
        assertThat(updatedSettings.getAdminContactName()).isEqualTo("Updated Admin");
        assertThat(updatedSettings.getAdminContactEmail()).isEqualTo("updated.admin@example.com");
        assertThat(updatedSettings.getAdminContactAddress()).isEqualTo("Updated Address");
        assertThat(updatedSettings.getAdminContactTwitter()).isEqualTo("https://twitter.com/updatedadmin");
        assertThat(updatedSettings.getAdminContactGithub()).isEqualTo("https://github.com/updatedadmin");
        assertThat(updatedSettings.getAdminContactLinkedin()).isEqualTo("https://linkedin.com/in/updatedadmin");
        assertThat(updatedSettings.getAdminContactYoutube()).isEqualTo("https://youtube.com/updatedadmin");
        assertThat(updatedSettings.getAutoApproveComment()).isTrue();
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void updateSettings_shouldHandleNullValues() {
        var result = mockMvcTester
                .put()
                .uri("/admin/settings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("adminContactName", "Admin With Nulls")
                .param("adminContactEmail", "admin.nulls@example.com")
                .param("adminContactAddress", "Address With Nulls")
                // Twitter, GitHub, LinkedIn, and YouTube are not provided (null)
                .param("autoApproveComment", "true")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/admin/settings");

        // Verify settings were updated with null values
        Settings updatedSettings = settingsService.getSettings();
        assertThat(updatedSettings).isNotNull();
        assertThat(updatedSettings.getId()).isEqualTo(1L);
        assertThat(updatedSettings.getAdminContactName()).isEqualTo("Admin With Nulls");
        assertThat(updatedSettings.getAdminContactEmail()).isEqualTo("admin.nulls@example.com");
        assertThat(updatedSettings.getAdminContactAddress()).isEqualTo("Address With Nulls");
        assertThat(updatedSettings.getAdminContactTwitter()).isNull();
        assertThat(updatedSettings.getAdminContactGithub()).isNull();
        assertThat(updatedSettings.getAdminContactLinkedin()).isNull();
        assertThat(updatedSettings.getAdminContactYoutube()).isNull();
        assertThat(updatedSettings.getAutoApproveComment()).isTrue();
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void updateSettings_shouldHandleEmptyValues() {
        var result = mockMvcTester
                .put()
                .uri("/admin/settings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("adminContactName", "Admin With Empty Values")
                .param("adminContactEmail", "admin.empty@example.com")
                .param("adminContactAddress", "Address With Empty Values")
                .param("adminContactTwitter", "")
                .param("adminContactGithub", "")
                .param("adminContactLinkedin", "")
                .param("adminContactYoutube", "")
                .param("autoApproveComment", "false")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/admin/settings");

        // Verify settings were updated with empty values
        Settings updatedSettings = settingsService.getSettings();
        assertThat(updatedSettings).isNotNull();
        assertThat(updatedSettings.getId()).isEqualTo(1L);
        assertThat(updatedSettings.getAdminContactName()).isEqualTo("Admin With Empty Values");
        assertThat(updatedSettings.getAdminContactEmail()).isEqualTo("admin.empty@example.com");
        assertThat(updatedSettings.getAdminContactAddress()).isEqualTo("Address With Empty Values");
        assertThat(updatedSettings.getAdminContactTwitter()).isEmpty();
        assertThat(updatedSettings.getAdminContactGithub()).isEmpty();
        assertThat(updatedSettings.getAdminContactLinkedin()).isEmpty();
        assertThat(updatedSettings.getAdminContactYoutube()).isEmpty();
        assertThat(updatedSettings.getAutoApproveComment()).isFalse();
    }
}
