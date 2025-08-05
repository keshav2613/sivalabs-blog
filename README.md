# SivaLabs Blog
![Spring Boot](https://img.shields.io/badge/Spring-Boot-6DB33F?logo=spring-boot)
[![Maven Build](https://github.com/sivaprasadreddy/sivalabs-blog/actions/workflows/maven.yml/badge.svg)](https://github.com/sivaprasadreddy/sivalabs-blog/actions/workflows/maven.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sivaprasadreddy_sivalabs-blog&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=sivaprasadreddy_sivalabs-blog)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sivaprasadreddy_sivalabs-blog&metric=coverage)](https://sonarcloud.io/summary/new_code?id=sivaprasadreddy_sivalabs-blog)

SivaLabs Blog is a blog application developed using Java and Spring Boot.
It provides a platform for publishing blog posts, managing categories and tags, handling user comments, and engaging with subscribers through email notifications.
The application includes both public-facing features for readers and an administrative interface for content management.

## Tech Stack
- **Backend**:
    - Java
    - Spring Boot
    - Spring Data JPA, PostgreSQL, Flyway
    - Spring Security
    - Maven
    - JUnit, Testcontainers
    - Docker Compose

- **Frontend**:
    - Thymeleaf
    - HTMX, Alpine.js, jQuery
    - Tailwind CSS
    - Font Awesome

## Prerequisites
* JDK 24
* Docker and Docker Compose
* Your favourite IDE (Recommended: [IntelliJ IDEA](https://www.jetbrains.com/idea/))

Install JDK using [SDKMAN](https://sdkman.io/)

```shell
$ curl -s "https://get.sdkman.io" | bash
$ source "$HOME/.sdkman/bin/sdkman-init.sh"
$ sdk install java 24.0.1-tem
$ sdk install maven
```

## Local Development

```shell
$ git clone https://github.com/sivaprasadreddy/sivalabs-blog.git
$ cd sivalabs-blog
```

This project uses NPM to have live reloading.

Use the following steps to get it working:

1. Run the Spring Boot application with the `local` profile
2. From a terminal:
   * Run `npm install`
   * Run `npm run build && npm run watch` (You can also run `npm run --silent build && npm run --silent watch` if you want less output in the terminal)
3. Your default browser will open at http://localhost:3000

**NOTE:** If the page doesn't load properly, stop and re-run the `npm run build && npm run watch` command.

You should now be able to change any HTML or CSS and have the browser reload upon saving the file.

## Architecture

The application is designed as a **Modular Monolith**.
While it's a single deployable unit, the codebase is organized into distinct, independent modules,
each responsible for a specific business capability.
This modular approach enhances maintainability, scalability, and allows for clear separation of concerns.

The key modules in the application are:
- **`admin`**: Handles administrative functionalities like content management, user administration, and system settings.
- **`analytics`**: Manages tracking and reporting of application metrics, such as page views and events.
- **`auth`**: Implements user authentication and authorization using Spring Security.
- **`blog`**: Contains the core logic for blog posts, categories, tags, and comments.
- **`notification`**: Responsible for sending email notifications to subscribers and users.
- **`shared`**: Includes common components, utilities, and entities that are used across multiple modules.

This architecture provides the benefits of a microservices-style design—such as improved organization and scalability—while avoiding the complexities of a distributed system.
It allows for independent development and testing of modules, making the application easier to manage and evolve over time.

## Features

### Blog (Public facing)
Anyone (without logging in) can perform the following actions:

1. View the list of posts by reverse chronological order with pagination
2. View the list of posts by category with pagination
3. View the list of posts by tag with pagination
4. Search posts by keyword
5. View post details
6. Add comment to a post
7. Subscribe to the newsletter
8. View Contact details
9. Sent a message to the Admin

### Administration
Administrators and authors can log in and perform the following actions:

1. **Posts:**
    * View the list of posts
    * Create, update, delete
    * Publish, unpublish, archive

2. **Comments:**
    * View the list of comments
    * Delete, approve, mark as span

3. **Categories:**
    * View the list of categories
    * Create a new category

4. **Tags:**
    * View the list of tags
    * Delete tags

5. **Messages:**
    * View the list of messages
    * Delete messages

6. **Subscribers:**
    * View the list of subscribers
    * Delete subscribers

7. **Users:**
    * View the list of users(authors, administrators)

8. **My Profile:**
    * Update the login user profile details
    * Change Password
    * Upload profile image

9. **Settings:**
    * Configure application-level settings

**NOTE:** Some actions (delete posts, update settings, etc.) can only be performed by Administrators, but not authors.
