package com.sivalabs.blog.analytics.domain;

public record TopPageDTO(String path, Long views, String title) {

    public TopPageDTO(String path, Long views) {
        this(path, views, extractTitleFromPath(path));
    }

    private static String extractTitleFromPath(String path) {
        if (path == null || path.isEmpty()) {
            return "Unknown";
        }

        if ("/posts".equals(path) || "/".equals(path)) {
            return "Blog Home";
        }

        if (path.startsWith("/posts/")) {
            String slug = path.substring("/posts/".length());
            return formatTitle(slug);
        }

        if (path.startsWith("/categories/")) {
            String categorySlug = path.substring("/categories/".length()).replace("/posts", "");
            return "Category: " + formatTitle(categorySlug);
        }

        if (path.startsWith("/tags/")) {
            String tagSlug = path.substring("/tags/".length()).replace("/posts", "");
            return "Tag: " + formatTitle(tagSlug);
        }

        return formatTitle(path.replaceFirst("^/", ""));
    }

    private static String formatTitle(String slug) {
        return slug.replace("-", " ").replace("_", " ").trim();
    }
}
