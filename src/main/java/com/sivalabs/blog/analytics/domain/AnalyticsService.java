package com.sivalabs.blog.analytics.domain;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final PageViewRepository pageViewRepository;
    private final PageAnalyticsSummaryRepository summaryRepository;

    public AnalyticsService(PageViewRepository pageViewRepository, PageAnalyticsSummaryRepository summaryRepository) {
        this.pageViewRepository = pageViewRepository;
        this.summaryRepository = summaryRepository;
    }

    public AnalyticsDTO getAnalytics(String path) {
        var summary = summaryRepository.findByPath(path);

        if (summary.isPresent()) {
            var s = summary.get();
            return AnalyticsDTO.builder()
                    .path(path)
                    .viewsToday(s.getViewsToday())
                    .viewsThisWeek(s.getViewsThisWeek())
                    .viewsThisMonth(s.getViewsThisMonth())
                    .viewsAllTime(s.getTotalViews())
                    .uniqueVisitorsToday(s.getUniqueVisitorsToday())
                    .uniqueVisitorsThisWeek(s.getUniqueVisitorsThisWeek())
                    .uniqueVisitorsThisMonth(s.getUniqueVisitorsThisMonth())
                    .uniqueVisitorsAllTime(s.getUniqueVisitorsTotal())
                    .lastViewedAt(s.getLastViewedAt())
                    .build();
        }

        var now = LocalDateTime.now();
        var startOfDay = now.toLocalDate().atStartOfDay();
        var startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        var startOfMonth =
                now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();

        return AnalyticsDTO.builder()
                .path(path)
                .viewsToday(pageViewRepository.countViewsSince(path, startOfDay))
                .viewsThisWeek(pageViewRepository.countViewsSince(path, startOfWeek))
                .viewsThisMonth(pageViewRepository.countViewsSince(path, startOfMonth))
                .viewsAllTime(pageViewRepository.countTotalViewsForPath(path))
                .uniqueVisitorsToday(pageViewRepository.countUniqueVisitorsSince(path, startOfDay))
                .uniqueVisitorsThisWeek(pageViewRepository.countUniqueVisitorsSince(path, startOfWeek))
                .uniqueVisitorsThisMonth(pageViewRepository.countUniqueVisitorsSince(path, startOfMonth))
                .uniqueVisitorsAllTime(pageViewRepository.countTotalUniqueVisitorsForPath(path))
                .lastViewedAt(pageViewRepository.findLastViewedAt(path))
                .build();
    }

    public List<TopPageDTO> getTopPages(int limit) {
        return summaryRepository.findTopPagesByTotalViews(PageRequest.of(0, limit)).stream()
                .map(summary -> new TopPageDTO(summary.getPath(), summary.getTotalViews()))
                .collect(Collectors.toList());
    }

    public List<TopPageDTO> getTopPagesToday(int limit) {
        return summaryRepository.findTopPagesByViewsToday(PageRequest.of(0, limit)).stream()
                .map(summary -> new TopPageDTO(summary.getPath(), summary.getViewsToday()))
                .collect(Collectors.toList());
    }

    public List<TopPageDTO> getTopPagesThisWeek(int limit) {
        return summaryRepository.findTopPagesByViewsThisWeek(PageRequest.of(0, limit)).stream()
                .map(summary -> new TopPageDTO(summary.getPath(), summary.getViewsThisWeek()))
                .collect(Collectors.toList());
    }

    public List<TopPageDTO> getTopPagesThisMonth(int limit) {
        return summaryRepository.findTopPagesByViewsThisMonth(PageRequest.of(0, limit)).stream()
                .map(summary -> new TopPageDTO(summary.getPath(), summary.getViewsThisMonth()))
                .collect(Collectors.toList());
    }

    public AnalyticsDTO getTotalAnalytics() {
        var totalViews = summaryRepository.sumTotalViews();
        var viewsToday = summaryRepository.sumViewsToday();
        var viewsThisWeek = summaryRepository.sumViewsThisWeek();
        var viewsThisMonth = summaryRepository.sumViewsThisMonth();

        var totalUniqueVisitors = summaryRepository.sumUniqueVisitorsTotal();
        var uniqueVisitorsToday = summaryRepository.sumUniqueVisitorsToday();
        var uniqueVisitorsThisWeek = summaryRepository.sumUniqueVisitorsThisWeek();
        var uniqueVisitorsThisMonth = summaryRepository.sumUniqueVisitorsThisMonth();

        return AnalyticsDTO.builder()
                .path("total")
                .viewsToday(viewsToday)
                .viewsThisWeek(viewsThisWeek)
                .viewsThisMonth(viewsThisMonth)
                .viewsAllTime(totalViews)
                .uniqueVisitorsToday(uniqueVisitorsToday)
                .uniqueVisitorsThisWeek(uniqueVisitorsThisWeek)
                .uniqueVisitorsThisMonth(uniqueVisitorsThisMonth)
                .uniqueVisitorsAllTime(totalUniqueVisitors)
                .build();
    }

    public Long getTotalViews() {
        return summaryRepository.sumTotalViews();
    }

    public Long getViewsToday() {
        return summaryRepository.sumViewsToday();
    }

    public Long getViewsThisWeek() {
        return summaryRepository.sumViewsThisWeek();
    }

    public Long getViewsThisMonth() {
        return summaryRepository.sumViewsThisMonth();
    }
}
