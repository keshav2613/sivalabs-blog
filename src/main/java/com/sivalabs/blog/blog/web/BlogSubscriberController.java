package com.sivalabs.blog.blog.web;

import com.sivalabs.blog.blog.domain.BlogSubscriberService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class BlogSubscriberController {
    private static final Logger log = LoggerFactory.getLogger(BlogSubscriberController.class);
    private final BlogSubscriberService subscriberService;
    private final MessageSource messageSource;

    BlogSubscriberController(BlogSubscriberService subscriberService, MessageSource messageSource) {
        this.subscriberService = subscriberService;
        this.messageSource = messageSource;
    }

    @PostMapping("/newsletter/subscribe")
    @ResponseBody
    @HxRequest
    String subscribe(@RequestParam("email") String email) {
        log.info("Subscribing to newsletter with email: {}", email);
        subscriberService.subscribe(email);
        return messageSource.getMessage(
                "info.subscribed_successfully", null, "Subscribed successfully", Locale.getDefault());
    }
}
