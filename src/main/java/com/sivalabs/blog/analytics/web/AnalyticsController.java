package com.sivalabs.blog.analytics.web;

import com.sivalabs.blog.analytics.domain.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/analytics")
class AnalyticsController {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsController.class);

    private final AnalyticsService analyticsService;

    AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    String showAnalytics(Model model) {
        log.info("Fetching analytics data");
        var totalAnalytics = analyticsService.getTotalAnalytics();

        model.addAttribute("totalAnalytics", totalAnalytics);
        model.addAttribute("topPagesAllTime", analyticsService.getTopPages(10));
        model.addAttribute("topPagesToday", analyticsService.getTopPagesToday(10));
        model.addAttribute("topPagesThisWeek", analyticsService.getTopPagesThisWeek(10));
        model.addAttribute("topPagesThisMonth", analyticsService.getTopPagesThisMonth(10));

        return "admin/analytics";
    }

    @GetMapping("/page-details")
    String showPageDetails(@RequestParam String path, Model model) {
        log.info("Fetching analytics data for path: {}", path);
        var analytics = analyticsService.getAnalytics(path);
        model.addAttribute("analytics", analytics);
        model.addAttribute("path", path);
        return "admin/page-analytics";
    }
}
