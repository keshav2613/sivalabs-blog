package com.sivalabs.blog.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sivalabs.blog.ApplicationProperties;
import com.sivalabs.blog.admin.messages.MessageRepository;
import com.sivalabs.blog.admin.posts.CommentRepository;
import com.sivalabs.blog.admin.posts.PostRepository;
import com.sivalabs.blog.admin.subscribers.SubscriberRepository;
import com.sivalabs.blog.admin.taxonomy.CategoryRepository;
import com.sivalabs.blog.admin.taxonomy.TagRepository;
import com.sivalabs.blog.admin.users.UserRepository;
import com.sivalabs.blog.shared.entities.Category;
import com.sivalabs.blog.shared.entities.Comment;
import com.sivalabs.blog.shared.entities.Message;
import com.sivalabs.blog.shared.entities.Post;
import com.sivalabs.blog.shared.entities.Tag;
import com.sivalabs.blog.shared.entities.User;
import com.sivalabs.blog.shared.models.CommentStatus;
import com.sivalabs.blog.shared.models.PostStatus;
import com.sivalabs.blog.shared.utils.CommonUtils;
import com.sivalabs.blog.shared.utils.MarkdownUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    public static final String POSTS_JSON = "classpath:data/posts.json";

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final CommentRepository commentRepository;
    private final SubscriberRepository subscriberRepository;
    private final ApplicationProperties applicationProperties;
    private final MessageRepository messageRepository;

    DataInitializer(
            UserRepository userRepository,
            TagRepository tagRepository,
            CategoryRepository categoryRepository,
            PostRepository postRepository,
            ResourceLoader resourceLoader,
            ObjectMapper objectMapper,
            CommentRepository commentRepository,
            SubscriberRepository subscriberRepository,
            ApplicationProperties applicationProperties,
            MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.commentRepository = commentRepository;
        this.subscriberRepository = subscriberRepository;
        this.applicationProperties = applicationProperties;
        this.messageRepository = messageRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!applicationProperties.initSampleData()) {
            log.info("Initialization of sample data is disabled");
            return;
        }
        this.loadPostEntries();
        this.loadSubscribers();
        this.loadMessages();
    }

    private void loadMessages() {
        if (messageRepository.count() > 0) {
            log.info("Messages have already been initialized");
            return;
        }
        String[] messages = {
            "I like your articles",
            "Can you please write articles on Quarkus?",
            "Great tutorials on Spring Boot!",
            "Would love to see more content on microservices",
            "Your Docker guides are very helpful",
            "Please write about Kubernetes deployment strategies",
            "The Java best practices articles are excellent",
            "How about some content on REST API design?",
            "Need help with JPA relationships",
            "Looking forward to more AWS tutorials",
            "Your explanations are very clear and concise",
            "Can you cover Spring Security in depth?",
            "Would appreciate articles on testing strategies",
            "More content on DevOps practices please",
            "Interested in CI/CD pipeline tutorials",
            "Could you write about performance optimization?",
            "Your MongoDB series was very informative"
        };
        for (String message : messages) {
            Message msg = new Message();
            msg.setName("Siva");
            msg.setEmail("siva@yahoo.com");
            msg.setSubject("Hi");
            msg.setContent(message);
            messageRepository.save(msg);
        }
        log.info("Loaded {} messages", messages.length);
    }

    private void loadSubscribers() {
        if (subscriberRepository.count() > 0) {
            log.info("Subscribers have already been initialized");
            return;
        }
        String[] emails = {
            "siva@gmail.com",
            "prasad@gmail.com",
            "sivalabs.in@gmail.com",
            "siva@sivalans.in",
            "john.doe@example.com",
            "jane.smith@company.net",
            "developer123@coding.org",
            "tech.support@helpdesk.com",
            "marketing@business.co",
            "info@startups.io",
            "contact@webdev.net",
            "admin@systems.org",
            "sales@enterprise.com",
            "support@platform.tech"
        };
        for (String email : emails) {
            subscriberRepository.subscribe(email);
        }
        log.info("Loaded {} subscribers", emails.length);
    }

    private void loadPostEntries() throws IOException {
        if (postRepository.count() > 0) {
            log.info("Posts have already been initialized");
            return;
        }
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();

        Resource json = resourceLoader.getResource(POSTS_JSON);
        PostEntries postEntries = objectMapper.readValue(json.getInputStream(), PostEntries.class);
        User admin = userRepository.getByEmail("admin@gmail.com");
        User author = userRepository.getByEmail("siva@gmail.com");
        Map<String, Tag> tagLabelMap = tagRepository.findAll().stream()
                .collect(Collectors.toMap(t -> t.getLabel().toLowerCase(), t -> t));
        for (PostEntry postEntry : postEntries.posts()) {
            // logger.info("Loaded post entry: {}", postEntry);
            Category category = categoryRepository.getBySlug(postEntry.category);
            List<String> tags = postEntry.tags();
            Set<Tag> tagEntities = new HashSet<>();
            for (String tagLabel : tags) {
                Tag tagEntity = tagLabelMap.get(tagLabel.toLowerCase());
                if (tagEntity == null) {
                    tagEntity = new Tag();
                    tagEntity.setLabel(tagLabel);
                    tagEntity.setSlug(CommonUtils.toSlug(tagLabel));
                    tagRepository.save(tagEntity);
                    tagLabelMap.put(tagLabel.toLowerCase(), tagEntity);
                }
                tagEntities.add(tagEntity);
            }
            String file = postEntry.file();
            Resource markdownFile = resourceLoader.getResource("classpath:data/" + file);
            String mdContent = new String(markdownFile.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String htmlContent = MarkdownUtils.toHTML(mdContent);
            // logger.info("Converted markdown to HTML: {}", htmlContent);

            Post post = new Post();
            post.setTitle(postEntry.title());
            post.setCategory(category);
            post.setSlug(postEntry.slug());
            post.setMdContent(mdContent);
            post.setContent(htmlContent);
            post.setCoverImage(getRandomCoverImage());
            post.setCreatedBy(getRandomUser(List.of(admin, author)));
            post.setTags(tagEntities);
            post.setStatus(PostStatus.PUBLISHED);

            postRepository.save(post);

            // insert sample comment
            if (RandomUtils.secure().randomBoolean()) {
                Comment comment = new Comment();
                comment.setName("Siva");
                comment.setEmail("siva@mail.com");
                comment.setContent(getRandomComment());
                comment.setStatus(getRandomCommentStatus());
                comment.setPost(post);

                commentRepository.save(comment);
            }
        }
        log.info("Loaded post entries");
    }

    private User getRandomUser(List<User> users) {
        return users.get(RandomUtils.secure().randomInt(0, users.size()));
    }

    private String getRandomCoverImage() {
        String[] images = {
            "/images/covers/blog-cover-1.jpg",
            "/images/covers/blog-cover-2.jpg",
            "/images/covers/blog-cover-3.jpg",
            "/images/covers/blog-cover-4.jpg",
            "/images/covers/blog-cover-5.jpg",
            "/images/covers/blog-cover-6.jpg",
            "/images/covers/blog-cover-7.jpg",
            "/images/covers/blog-cover-8.jpg",
            "/images/covers/blog-cover-9.jpg",
            "/images/covers/blog-cover-10.jpg",
        };
        return images[RandomUtils.secure().randomInt(0, images.length)];
    }

    private CommentStatus getRandomCommentStatus() {
        CommentStatus[] values = CommentStatus.values();
        return values[RandomUtils.secure().randomInt(0, values.length)];
    }

    private String getRandomComment() {
        String[] comments = {
            "Great post! Learned a lot from this post.",
            "I love this post. I learned a lot from reading it.",
            "This post is really interesting. I learned a lot from reading it."
        };
        return comments[RandomUtils.secure().randomInt(0, comments.length)];
    }

    record PostEntries(List<PostEntry> posts) {}

    record PostEntry(
            String title,
            String slug,
            LocalDate publishedDate,
            String author,
            String category,
            List<String> tags,
            String image,
            String file,
            String mdContent,
            String htmlContent) {}
}
