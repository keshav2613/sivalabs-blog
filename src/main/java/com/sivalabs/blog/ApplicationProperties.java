package com.sivalabs.blog;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Validated
public record ApplicationProperties(
        String supportEmail,
        String newsletterJobCron,
        String refreshAnalyticsSummariesJobCron,
        @DefaultValue("6") int blogPostsPageSize,
        @DefaultValue("10") int adminDefaultPageSize,
        @NotBlank String fileUploadsDir,
        boolean initSampleData) {}
