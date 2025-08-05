package com.sivalabs.blog.analytics.web;

import com.sivalabs.blog.analytics.events.PageViewEvent;
import com.sivalabs.blog.analytics.events.PageViewEventPublisher;
import com.sivalabs.blog.shared.models.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class PageViewInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(PageViewInterceptor.class);

    private final PageViewEventPublisher eventPublisher;

    public PageViewInterceptor(PageViewEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

        if (shouldTrackPageView(request, response)) {
            var event = PageViewEvent.builder()
                    .path(request.getRequestURI())
                    .title(extractTitle(modelAndView))
                    .referer(request.getHeader("Referer"))
                    .userAgent(request.getHeader("User-Agent"))
                    .ipAddress(getClientIpAddress(request))
                    .sessionId(request.getSession().getId())
                    .userId(getCurrentUserId())
                    .timestamp(LocalDateTime.now())
                    .build();

            eventPublisher.publishPageView(event);
            log.debug("Tracked page view for path: {}", request.getRequestURI());
        }
    }

    private boolean shouldTrackPageView(HttpServletRequest request, HttpServletResponse response) {
        if (!"GET".equals(request.getMethod())) {
            return false;
        }

        if (response.getStatus() != 200) {
            return false;
        }

        var uri = request.getRequestURI();

        if (uri.startsWith("/admin")) {
            return false;
        }

        if (uri.startsWith("/api")) {
            return false;
        }

        if (uri.contains("/webjars")
                || uri.contains("/css")
                || uri.contains("/js")
                || uri.contains("/images")
                || uri.contains("/user-images")
                || uri.contains("/favicon.ico")) {
            return false;
        }

        return !uri.equals("/") && !uri.equals("/login") && !uri.equals("/logout") && !uri.equals("/error");
    }

    private String extractTitle(ModelAndView modelAndView) {
        if (modelAndView == null) {
            return null;
        } else {
            modelAndView.getModel();
        }

        var model = modelAndView.getModel();

        if (model.containsKey("post")) {
            var post = model.get("post");
            if (post != null) {
                try {
                    var titleMethod = post.getClass().getMethod("title");
                    return (String) titleMethod.invoke(post);
                } catch (Exception e) {
                    log.debug("Failed to extract post title", e);
                }
            }
        }

        var viewName = modelAndView.getViewName();
        if (viewName != null) {
            return formatViewNameAsTitle(viewName);
        }

        return null;
    }

    private String formatViewNameAsTitle(String viewName) {
        return switch (viewName) {
            case "blog/posts" -> "Blog Posts";
            case "blog/post-details" -> "Post Details";
            case "blog/contact" -> "Contact";
            case "index" -> "Home";
            default -> viewName.replace("/", " - ").replace("-", " ").trim();
        };
    }

    private String getClientIpAddress(HttpServletRequest request) {
        var xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        var xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null
                    && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {

                var principal = authentication.getPrincipal();
                if (principal instanceof SecurityUser securityUser) {
                    return securityUser.getId();
                }
            }
        } catch (Exception e) {
            log.debug("Failed to get current user ID", e);
        }
        return null;
    }
}
