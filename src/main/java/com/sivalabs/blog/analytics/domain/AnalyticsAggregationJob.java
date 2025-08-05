package com.sivalabs.blog.analytics.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsAggregationJob {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsAggregationJob.class);

    private final PageViewService pageViewService;

    public AnalyticsAggregationJob(PageViewService pageViewService) {
        this.pageViewService = pageViewService;
    }

    @Scheduled(cron = "${app.refresh-analytics-summaries-job-cron}")
    public void refreshAnalyticsSummaries() {
        logger.info("Starting scheduled analytics summaries refresh");
        try {
            pageViewService.refreshAllSummaries();
            logger.info("Completed scheduled analytics summaries refresh");
        } catch (Exception e) {
            logger.error("Failed to refresh analytics summaries", e);
        }
    }
}
