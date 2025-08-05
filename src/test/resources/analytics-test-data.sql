-- Test data for analytics tests
INSERT INTO page_views (path, title, referer, user_agent, ip_address, session_id, user_id, created_at)
VALUES 
    ('/posts', 'Blog Home', null, 'Mozilla/5.0', '192.168.1.1', 'session1', null, NOW() - INTERVAL '1 hour'),
    ('/posts/spring-boot-guide', 'Spring Boot Guide', '/posts', 'Mozilla/5.0', '192.168.1.2', 'session2', null, NOW() - INTERVAL '2 hours'),
    ('/posts/spring-boot-guide', 'Spring Boot Guide', '/posts', 'Mozilla/5.0', '192.168.1.1', 'session1', null, NOW() - INTERVAL '3 hours'),
    ('/posts/java-basics', 'Java Basics', '/posts', 'Mozilla/5.0', '192.168.1.3', 'session3', null, NOW() - INTERVAL '1 day'),
    ('/categories/java/posts', 'Java Category', null, 'Mozilla/5.0', '192.168.1.4', 'session4', null, NOW() - INTERVAL '2 days');

-- Sample page analytics summary data
INSERT INTO page_analytics_summary (path, total_views, views_today, views_this_week, views_this_month, 
                                   unique_visitors_total, unique_visitors_today, unique_visitors_this_week, unique_visitors_this_month, 
                                   last_viewed_at)
VALUES 
    ('/posts', 25, 5, 15, 20, 20, 4, 12, 16, NOW()),
    ('/posts/spring-boot-guide', 45, 8, 25, 35, 35, 6, 18, 25, NOW()),
    ('/posts/java-basics', 15, 2, 8, 12, 12, 2, 6, 9, NOW());