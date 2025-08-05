package com.sivalabs.blog.analytics.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PageViewEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(PageViewEventPublisher.class);

    private final ApplicationEventPublisher eventPublisher;

    public PageViewEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void publishPageView(PageViewEvent event) {
        try {
            eventPublisher.publishEvent(event);
            logger.debug("Published page view event for path: {}", event.path());
        } catch (Exception e) {
            logger.error("Failed to publish page view event for path: {}", event.path(), e);
        }
    }
}
