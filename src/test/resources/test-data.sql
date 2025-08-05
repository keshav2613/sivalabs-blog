DELETE FROM subscribers;
DELETE FROM settings;
DELETE FROM messages;
DELETE FROM comments;
DELETE FROM posts_tags;
DELETE FROM tags;
DELETE FROM posts;
DELETE FROM categories;
DELETE FROM users;

INSERT INTO users(id, email, password, name, role, bio, image) VALUES
(1,'admin@gmail.com','$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS','SivaLabs', 'ROLE_ADMIN', 'Siva is a DevOps Specialist and Tech Enthusiast. He writes about Docker, Kubernetes, Linux, and AWS technologies.', '/images/authors/admin.png'),
(2,'siva@gmail.com','$2a$10$UFEPYW7Rx1qZqdHajzOnB.VBR3rvm7OI7uSix4RadfQiNhkZOi2fi','Siva', 'ROLE_AUTHOR', 'Siva is a Software Engineer. He writes about Java, Spring Boot, Microservices, and Cloud-Native technologies.', '/images/authors/siva.jpg');

INSERT INTO categories(id, label, slug) VALUES
(1,'Java', 'java'),
(2,'Spring Boot', 'spring-boot'),
(3,'Microservices', 'microservices'),
(4,'Testing', 'testing'),
(5,'Quarkus', 'quarkus'),
(6,'JUnit', 'junit'),
(7,'DevOps', 'devops'),
(8,'Architecture', 'architecture'),
(9,'Other', 'other')
;

INSERT INTO tags(id, label, slug) VALUES
(1,'Java', 'java'),
(2,'Spring Boot', 'spring-boot'),
(3,'Spring Security', 'spring-security'),
(4,'Spring Modulith', 'spring-modulith'),
(5,'OAuth', 'oauth'),
(6,'kubernetes', 'kubernetes'),
(7,'Maven', 'maven'),
(8,'Gradle', 'gradle'),
(9,'Spring AI', 'spring-ai'),
(10,'Thymeleaf', 'thymeleaf'),
(11,'Testcontainers', 'testcontainers'),
(12,'Best Practices', 'best-practices')
;

INSERT INTO posts(id, title, slug, md_content, content, cover_image, category_id, created_by, status, created_at) VALUES
(1, 'Test Post 1', 'test-post-1', '# Test Post 1', '<h1>Test Post 1</h1>', '/images/posts/default.jpg', 1, 1, 'PUBLISHED', '2023-01-01 10:00:00'),
(2, 'Test Post 2', 'test-post-2', '# Test Post 2', '<h1>Test Post 2</h1>', '/images/posts/default.jpg', 1, 2, 'PUBLISHED', '2023-01-02 10:00:00'),
(3, 'Test Post 3', 'test-post-3', '# Test Post 3', '<h1>Test Post 3</h1>', '/images/posts/default.jpg', 2, 2, 'PUBLISHED', '2023-01-03 10:00:00'),
(4, 'Test Post 4', 'test-post-4', '# Test Post 4', '<h1>Test Post 4</h1>', '/images/posts/default.jpg', 2, 1, 'PUBLISHED', '2023-01-04 10:00:00'),
(5, 'Test Post 5', 'test-post-5', '# Test Post 5', '<h1>Test Post 5</h1>', '/images/posts/default.jpg', 3, 2, 'DRAFT', '2023-01-05 10:00:00');

INSERT INTO posts_tags(post_id, tag_id) VALUES
(1, 1), (1, 2),
(2, 2), (2, 3),
(3, 1), (3, 3), (3, 4),
(4, 2), (4, 4),
(5, 1), (5, 4);

INSERT INTO comments(id, post_id, name, email, content, status, created_at) VALUES
(1, 1, 'Commenter 1', 'commenter1@example.com', 'This is a test comment 1', 'APPROVED', '2023-01-01 12:00:00'),
(2, 1, 'Commenter 2', 'commenter2@example.com', 'This is a test comment 2', 'APPROVED', '2023-01-01 12:30:00'),
(3, 2, 'Commenter 3', 'commenter3@example.com', 'This is a test comment 3', 'APPROVED', '2023-01-02 12:00:00'),
(4, 3, 'Commenter 4', 'commenter4@example.com', 'This is a test comment 4', 'PENDING', '2023-01-03 12:00:00'),
(5, 4, 'Commenter 5', 'commenter5@example.com', 'This is a test comment 5', 'APPROVED', '2023-01-04 12:00:00');

INSERT INTO settings(id, admin_contact_name, admin_contact_email, admin_contact_address, admin_contact_twitter, admin_contact_github, admin_contact_linkedin, admin_contact_youtube, auto_approve_comment) VALUES
(1, 'Test Admin', 'test.admin@example.com', 'Test Address', 'https://twitter.com/testadmin', 'https://github.com/testadmin', 'https://linkedin.com/in/testadmin', 'https://youtube.com/testadmin', false);

INSERT INTO messages(id, name, email, subject, content, created_at) VALUES
(1, 'Message Sender 1', 'sender1@example.com', 'Test Subject 1', 'This is test message content 1', '2023-01-10 10:00:00'),
(2, 'Message Sender 2', 'sender2@example.com', 'Test Subject 2', 'This is test message content 2', '2023-01-11 10:00:00'),
(3, 'Message Sender 3', 'sender3@example.com', 'Test Subject 3', 'This is test message content 3', '2023-01-12 10:00:00');

INSERT INTO subscribers(id, email, verified, created_at) VALUES
(1, 'subscriber1@example.com', true, '2023-01-15 10:00:00'),
(2, 'subscriber2@example.com', true, '2023-01-16 10:00:00'),
(3, 'subscriber3@example.com', false, '2023-01-17 10:00:00'),
(4, 'subscriber4@example.com', false, '2023-01-18 10:00:00'),
(5, 'subscriber5@example.com', true, '2023-01-19 10:00:00');
