package com.sivalabs.blog.admin.jobs;

import com.sivalabs.blog.admin.posts.PostService;
import com.sivalabs.blog.admin.subscribers.SubscriberService;
import com.sivalabs.blog.notification.EmailService;
import com.sivalabs.blog.shared.entities.Post;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class WeeklyEmailSenderJob {
    private static final Logger log = LoggerFactory.getLogger(WeeklyEmailSenderJob.class);
    private final PostService postService;
    private final SubscriberService subscriberService;
    private final EmailService emailService;

    WeeklyEmailSenderJob(PostService postServiceImpl, SubscriberService subscriberService, EmailService emailService) {
        this.postService = postServiceImpl;
        this.subscriberService = subscriberService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "${app.newsletter-job-cron}")
    void sendNewsLetter() {
        log.info("Sending newsletter at {}", Instant.now());
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        List<Post> posts = postService.findPostsCreatedBetween(startOfWeek, end);
        if (posts.isEmpty()) {
            log.info("No posts found for this week. Skipping newsletter");
            return;
        }
        String newsLetterContent = createNewsLetterContent(posts);
        List<String> userEmails = subscriberService.getAllActiveSubscribers();
        if (userEmails.isEmpty()) {
            log.info("No users found for this week. Skipping newsletter");
            return;
        }
        emailService.send("Weekly Newsletter", userEmails, newsLetterContent);
        log.info("Sent newsletter at {} to {} users", Instant.now(), userEmails.size());
    }

    private String createNewsLetterContent(List<Post> posts) {
        StringBuilder emailContent = new StringBuilder();
        for (Post post : posts) {
            // Externalize base url
            String postUrl = "http://localhost:8080/posts/" + post.getSlug();
            var fragment =
                    """
                    <h2><a href="%s">%s</a></h2>
                    <p>%s</p>
                    """
                            .formatted(postUrl, post.getTitle(), post.getSummary());
            emailContent.append(fragment);
        }
        return emailContent.toString();
    }
}
