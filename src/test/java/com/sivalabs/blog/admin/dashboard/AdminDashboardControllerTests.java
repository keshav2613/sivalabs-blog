package com.sivalabs.blog.admin.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;

class AdminDashboardControllerTests extends AbstractIT {

    @Test
    @WithUserDetails("admin@gmail.com")
    void dashboard_shouldDisplayDashboardWithCounts() {
        var result = mockMvcTester.get().uri("/admin/dashboard").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/dashboard")
                .model()
                .containsKeys("postsCount", "categoriesCount", "tagsCount", "commentsCount")
                .satisfies(model -> {
                    assertThat(model.get("postsCount")).isEqualTo(5L);
                    assertThat(model.get("categoriesCount")).isEqualTo(9L);
                    assertThat(model.get("tagsCount")).isEqualTo(12L);
                    assertThat(model.get("commentsCount")).isEqualTo(5L);
                });
    }

    @Test
    void dashboard_shouldRequireAuthentication() {
        var result = mockMvcTester.get().uri("/admin/dashboard").exchange();

        // For unauthenticated requests, we expect a redirect to login (302 Found)
        assertThat(result).hasStatus(HttpStatus.FOUND); // 302 redirect to login
    }

    @Test
    @WithUserDetails("siva@gmail.com")
    void dashboard_shouldAllowAuthorRole() {
        var result = mockMvcTester.get().uri("/admin/dashboard").exchange();

        // The controller appears to allow ROLE_AUTHOR access as well
        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/dashboard")
                .model()
                .containsKeys("postsCount", "categoriesCount", "tagsCount", "commentsCount");
    }
}
