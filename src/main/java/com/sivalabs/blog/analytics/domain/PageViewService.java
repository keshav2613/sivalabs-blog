package com.sivalabs.blog.analytics.domain;

import com.sivalabs.blog.analytics.events.PageViewEvent;
import com.sivalabs.blog.shared.entities.PageAnalyticsSummary;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PageViewService {
    private static final Logger logger = LoggerFactory.getLogger(PageViewService.class);

    private final PageViewRepository pageViewRepository;
    private final PageAnalyticsSummaryRepository summaryRepository;

    public PageViewService(PageViewRepository pageViewRepository, PageAnalyticsSummaryRepository summaryRepository) {
        this.pageViewRepository = pageViewRepository;
        this.summaryRepository = summaryRepository;
    }

    public void updatePageAnalyticsSummary(PageViewEvent event) {
        try {
            var summary = summaryRepository.findByPath(event.path()).orElse(new PageAnalyticsSummary(event.path()));

            var now = LocalDateTime.now();
            var startOfDay = now.toLocalDate().atStartOfDay();
            var startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
            var startOfMonth =
                    now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();

            summary.setTotalViews(pageViewRepository.countTotalViewsForPath(event.path()));
            summary.setViewsToday(pageViewRepository.countViewsSince(event.path(), startOfDay));
            summary.setViewsThisWeek(pageViewRepository.countViewsSince(event.path(), startOfWeek));
            summary.setViewsThisMonth(pageViewRepository.countViewsSince(event.path(), startOfMonth));

            summary.setUniqueVisitorsTotal(pageViewRepository.countTotalUniqueVisitorsForPath(event.path()));
            summary.setUniqueVisitorsToday(pageViewRepository.countUniqueVisitorsSince(event.path(), startOfDay));
            summary.setUniqueVisitorsThisWeek(pageViewRepository.countUniqueVisitorsSince(event.path(), startOfWeek));
            summary.setUniqueVisitorsThisMonth(pageViewRepository.countUniqueVisitorsSince(event.path(), startOfMonth));

            summary.setLastViewedAt(now);

            summaryRepository.save(summary);

            logger.debug("Updated analytics summary for path: {}", event.path());
        } catch (Exception e) {
            logger.error("Failed to update analytics summary for path: {}", event.path(), e);
        }
    }

    @Transactional(readOnly = true)
    public void refreshAllSummaries() {
        logger.info("Starting refresh of all page analytics summaries");

        try {
            var allSummaries = summaryRepository.findAll();
            var now = LocalDateTime.now();
            var startOfDay = now.toLocalDate().atStartOfDay();
            var startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
            var startOfMonth =
                    now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();

            for (var summary : allSummaries) {
                var path = summary.getPath();

                summary.setTotalViews(pageViewRepository.countTotalViewsForPath(path));
                summary.setViewsToday(pageViewRepository.countViewsSince(path, startOfDay));
                summary.setViewsThisWeek(pageViewRepository.countViewsSince(path, startOfWeek));
                summary.setViewsThisMonth(pageViewRepository.countViewsSince(path, startOfMonth));

                summary.setUniqueVisitorsTotal(pageViewRepository.countTotalUniqueVisitorsForPath(path));
                summary.setUniqueVisitorsToday(pageViewRepository.countUniqueVisitorsSince(path, startOfDay));
                summary.setUniqueVisitorsThisWeek(pageViewRepository.countUniqueVisitorsSince(path, startOfWeek));
                summary.setUniqueVisitorsThisMonth(pageViewRepository.countUniqueVisitorsSince(path, startOfMonth));

                summary.setLastViewedAt(pageViewRepository.findLastViewedAt(path));

                summaryRepository.save(summary);
            }

            logger.info("Completed refresh of {} page analytics summaries", allSummaries.size());
        } catch (Exception e) {
            logger.error("Failed to refresh analytics summaries", e);
        }
    }
}
