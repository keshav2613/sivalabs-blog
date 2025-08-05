package com.sivalabs.blog.analytics.config;

import com.sivalabs.blog.analytics.web.PageViewInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class AnalyticsWebMvcConfig implements WebMvcConfigurer {
    private final PageViewInterceptor pageViewInterceptor;

    AnalyticsWebMvcConfig(PageViewInterceptor pageViewInterceptor) {
        this.pageViewInterceptor = pageViewInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pageViewInterceptor);
    }
}
