package com.sivalabs.blog.analytics.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sivalabs.blog.BaseServiceTest;
import com.sivalabs.blog.shared.entities.PageAnalyticsSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

class AnalyticsServiceTest extends BaseServiceTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private PageAnalyticsSummaryRepository summaryRepository;

    @Test
    void shouldReturnZeroAnalyticsForNonExistentPage() {
        var analytics = analyticsService.getAnalytics("/non-existent-page");

        assertThat(analytics).isNotNull();
        assertThat(analytics.path()).isEqualTo("/non-existent-page");
        assertThat(analytics.viewsAllTime()).isEqualTo(0L);
        assertThat(analytics.viewsToday()).isEqualTo(0L);
        assertThat(analytics.viewsThisWeek()).isEqualTo(0L);
        assertThat(analytics.viewsThisMonth()).isEqualTo(0L);
    }

    @Test
    @Sql("/analytics-test-data.sql")
    void shouldReturnAnalyticsFromSummary() {
        var summary = new PageAnalyticsSummary("/test-page");
        summary.setTotalViews(100L);
        summary.setViewsToday(10L);
        summary.setViewsThisWeek(20L);
        summary.setViewsThisMonth(30L);
        summaryRepository.save(summary);

        var analytics = analyticsService.getAnalytics("/test-page");

        assertThat(analytics).isNotNull();
        assertThat(analytics.path()).isEqualTo("/test-page");
        assertThat(analytics.viewsAllTime()).isEqualTo(100L);
        assertThat(analytics.viewsToday()).isEqualTo(10L);
        assertThat(analytics.viewsThisWeek()).isEqualTo(20L);
        assertThat(analytics.viewsThisMonth()).isEqualTo(30L);
    }

    @Test
    void shouldReturnEmptyTopPagesWhenNoData() {
        var topPages = analyticsService.getTopPages(10);
        assertThat(topPages).isEmpty();
    }

    @Test
    void shouldReturnTotalAnalytics() {
        var totalAnalytics = analyticsService.getTotalAnalytics();

        assertThat(totalAnalytics).isNotNull();
        assertThat(totalAnalytics.path()).isEqualTo("total");
        assertThat(totalAnalytics.viewsAllTime()).isNotNull();
        assertThat(totalAnalytics.viewsToday()).isNotNull();
        assertThat(totalAnalytics.viewsThisWeek()).isNotNull();
        assertThat(totalAnalytics.viewsThisMonth()).isNotNull();
    }
}
