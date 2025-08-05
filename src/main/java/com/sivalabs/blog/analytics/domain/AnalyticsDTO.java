package com.sivalabs.blog.analytics.domain;

import java.time.LocalDateTime;

public record AnalyticsDTO(
        String path,
        Long viewsToday,
        Long viewsThisWeek,
        Long viewsThisMonth,
        Long viewsAllTime,
        Long uniqueVisitorsToday,
        Long uniqueVisitorsThisWeek,
        Long uniqueVisitorsThisMonth,
        Long uniqueVisitorsAllTime,
        LocalDateTime lastViewedAt) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String path;
        private Long viewsToday = 0L;
        private Long viewsThisWeek = 0L;
        private Long viewsThisMonth = 0L;
        private Long viewsAllTime = 0L;
        private Long uniqueVisitorsToday = 0L;
        private Long uniqueVisitorsThisWeek = 0L;
        private Long uniqueVisitorsThisMonth = 0L;
        private Long uniqueVisitorsAllTime = 0L;
        private LocalDateTime lastViewedAt;

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder viewsToday(Long viewsToday) {
            this.viewsToday = viewsToday != null ? viewsToday : 0L;
            return this;
        }

        public Builder viewsThisWeek(Long viewsThisWeek) {
            this.viewsThisWeek = viewsThisWeek != null ? viewsThisWeek : 0L;
            return this;
        }

        public Builder viewsThisMonth(Long viewsThisMonth) {
            this.viewsThisMonth = viewsThisMonth != null ? viewsThisMonth : 0L;
            return this;
        }

        public Builder viewsAllTime(Long viewsAllTime) {
            this.viewsAllTime = viewsAllTime != null ? viewsAllTime : 0L;
            return this;
        }

        public Builder uniqueVisitorsToday(Long uniqueVisitorsToday) {
            this.uniqueVisitorsToday = uniqueVisitorsToday != null ? uniqueVisitorsToday : 0L;
            return this;
        }

        public Builder uniqueVisitorsThisWeek(Long uniqueVisitorsThisWeek) {
            this.uniqueVisitorsThisWeek = uniqueVisitorsThisWeek != null ? uniqueVisitorsThisWeek : 0L;
            return this;
        }

        public Builder uniqueVisitorsThisMonth(Long uniqueVisitorsThisMonth) {
            this.uniqueVisitorsThisMonth = uniqueVisitorsThisMonth != null ? uniqueVisitorsThisMonth : 0L;
            return this;
        }

        public Builder uniqueVisitorsAllTime(Long uniqueVisitorsAllTime) {
            this.uniqueVisitorsAllTime = uniqueVisitorsAllTime != null ? uniqueVisitorsAllTime : 0L;
            return this;
        }

        public Builder lastViewedAt(LocalDateTime lastViewedAt) {
            this.lastViewedAt = lastViewedAt;
            return this;
        }

        public AnalyticsDTO build() {
            return new AnalyticsDTO(
                    path,
                    viewsToday,
                    viewsThisWeek,
                    viewsThisMonth,
                    viewsAllTime,
                    uniqueVisitorsToday,
                    uniqueVisitorsThisWeek,
                    uniqueVisitorsThisMonth,
                    uniqueVisitorsAllTime,
                    lastViewedAt);
        }
    }
}
