package com.sivalabs.blog.analytics.web;

import com.sivalabs.blog.AbstractIT;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

class AnalyticsControllerTests extends AbstractIT {

    @Test
    @WithUserDetails("admin@gmail.com")
    void shouldShowAnalyticsDashboard() throws Exception {
        var result = mockMvcTester.get().uri("/admin/analytics").exchange();

        result.assertThat().hasStatus2xxSuccessful().hasViewName("admin/analytics");
    }

    @Test
    @WithUserDetails("siva@gmail.com")
    void shouldShowAnalyticsDashboardForAuthor() {
        var result = mockMvcTester.get().uri("/admin/analytics").exchange();
        result.assertThat().hasStatus2xxSuccessful().hasViewName("admin/analytics");
    }

    @Test
    void shouldRequireAuthenticationForAnalytics() {
        var result = mockMvcTester.get().uri("/admin/analytics").exchange();
        result.assertThat().hasStatus3xxRedirection();
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void shouldShowPageDetails() {
        var result = mockMvcTester
                .get()
                .uri("/admin/analytics/page-details")
                .param("path", "/posts")
                .exchange();

        result.assertThat().hasStatus2xxSuccessful().hasViewName("admin/page-analytics");
    }
}
