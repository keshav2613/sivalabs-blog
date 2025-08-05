package com.sivalabs.blog.admin.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

class UserControllerTests extends AbstractIT {

    @Test
    @WithUserDetails("siva@gmail.com")
    void shouldGetUserProfile() {
        var result = mockMvcTester.get().uri("/my-profile").exchange();
        assertThat(result).hasStatusOk().hasViewName("user/my-profile").model().containsKeys("profile");
    }

    @Test
    @WithUserDetails("siva@gmail.com")
    void shouldUpdateUserProfile() {
        var result = mockMvcTester
                .put()
                .uri("/my-profile")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "2")
                .param("name", "Test User 4")
                .param("email", "testuser4@gmail.com")
                .param("bio", "testuser4 biodata")
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/my-profile")
                .flash()
                .containsKey("successMessage")
                .hasEntrySatisfying(
                        "successMessage", value -> assertThat(value).isEqualTo("info.profile_update_success"));
    }
}
