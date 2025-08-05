insert into users(id, email, password, name, role, bio, image) values
(1,'admin@gmail.com','$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS','SivaLabs', 'ROLE_ADMIN', 'Siva is a DevOps Specialist and Tech Enthusiast. He writes about Docker, Kubernetes, Linux, and AWS technologies.', '/images/authors/admin.png'),
(2,'siva@gmail.com','$2a$10$UFEPYW7Rx1qZqdHajzOnB.VBR3rvm7OI7uSix4RadfQiNhkZOi2fi','Siva', 'ROLE_AUTHOR', 'Siva is a Software Engineer. He writes about Java, Spring Boot, Microservices, and Cloud-Native technologies.', '/images/authors/siva.jpg');

insert into categories(id, label, slug) values
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

insert into tags(id, label, slug) values
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
