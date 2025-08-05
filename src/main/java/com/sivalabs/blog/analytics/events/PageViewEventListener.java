package com.sivalabs.blog.analytics.events;

import com.sivalabs.blog.analytics.domain.PageViewRepository;
import com.sivalabs.blog.analytics.domain.PageViewService;
import com.sivalabs.blog.shared.entities.PageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class PageViewEventListener {
    private static final Logger logger = LoggerFactory.getLogger(PageViewEventListener.class);

    private final PageViewService pageViewService;
    private final PageViewRepository pageViewRepository;

    public PageViewEventListener(PageViewService pageViewService, PageViewRepository pageViewRepository) {
        this.pageViewService = pageViewService;
        this.pageViewRepository = pageViewRepository;
    }

    @ApplicationModuleListener
    public void handlePageViewEvent(PageViewEvent event) {
        try {
            logger.debug("Processing page view event for path: {}", event.path());

            var pageView = new PageView(
                    event.path(),
                    event.title(),
                    event.referer(),
                    event.userAgent(),
                    event.ipAddress(),
                    event.sessionId(),
                    event.userId());

            pageViewRepository.save(pageView);
            pageViewService.updatePageAnalyticsSummary(event);

            logger.debug("Successfully processed page view event for path: {}", event.path());
        } catch (Exception e) {
            logger.error("Failed to process page view event for path: {}", event.path(), e);
        }
    }
}
