package com.sivalabs.blog.analytics.events;

import java.time.LocalDateTime;

public record PageViewEvent(
        String path,
        String title,
        String referer,
        String userAgent,
        String ipAddress,
        String sessionId,
        Long userId,
        LocalDateTime timestamp) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String path;
        private String title;
        private String referer;
        private String userAgent;
        private String ipAddress;
        private String sessionId;
        private Long userId;
        private LocalDateTime timestamp;

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder referer(String referer) {
            this.referer = referer;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public PageViewEvent build() {
            return new PageViewEvent(path, title, referer, userAgent, ipAddress, sessionId, userId, timestamp);
        }
    }
}
