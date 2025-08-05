package com.sivalabs.blog.config;

import com.sivalabs.blog.ApplicationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebMvcConfig implements WebMvcConfigurer {
    private final ApplicationProperties properties;

    WebMvcConfig(ApplicationProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/posts");
        registry.addRedirectViewController("/admin", "/admin/dashboard");
        registry.addViewController("/login").setViewName("user/login");
        // To handle annoying .well-known/appspecific/com.chrome.devtools.json requests from Chrome Dev Tools
        registry.addStatusController("/.well-known/**", HttpStatus.OK);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/user-images/**")
                .addResourceLocations("file:" + properties.fileUploadsDir() + "/user-images/");
    }
}
