package com.sivalabs.blog.admin.posts;

import com.sivalabs.blog.ApplicationProperties;
import com.sivalabs.blog.admin.shared.UserContextUtils;
import com.sivalabs.blog.admin.taxonomy.CategoryService;
import com.sivalabs.blog.shared.entities.Post;
import com.sivalabs.blog.shared.entities.Tag;
import com.sivalabs.blog.shared.models.PagedResult;
import com.sivalabs.blog.shared.models.Pagination;
import com.sivalabs.blog.shared.models.PostStatus;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRefreshView;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/admin")
class AdminPostsController {
    private static final Logger log = LoggerFactory.getLogger(AdminPostsController.class);
    private final PostService postService;
    private final CategoryService categoryService;
    private final ApplicationProperties properties;

    AdminPostsController(PostService postService, CategoryService categoryService, ApplicationProperties properties) {
        this.postService = postService;
        this.categoryService = categoryService;
        this.properties = properties;
    }

    @GetMapping("/posts")
    String showPosts(Model model, @RequestParam(defaultValue = "1") Integer page) {
        log.info("Fetching posts for page: {}", page);
        PagedResult<Post> posts = postService.getAllPosts(page, properties.adminDefaultPageSize());
        model.addAttribute("posts", posts);
        var pagination = new Pagination<>(posts, "/admin/posts");
        model.addAttribute("pagination", pagination);
        return "admin/posts";
    }

    @GetMapping("/posts/new")
    String showCreatePostForm(Model model) {
        log.info("Showing create post form");
        model.addAttribute("postForm", new PostForm("", "", "", "", PostStatus.DRAFT));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute(
                "postStatuses",
                Arrays.stream(PostStatus.values()).map(Enum::name).toList());
        return "admin/new-post";
    }

    @PostMapping("/posts")
    String createPost(@ModelAttribute("postForm") @Valid PostForm form, BindingResult result, Model model) {
        log.info("Creating new post with title: {}", form.title);
        if (result.hasErrors()) {
            log.warn("Validation errors occurred while creating post");
            model.addAttribute("postForm", form);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute(
                    "postStatuses",
                    Arrays.stream(PostStatus.values()).map(Enum::name).toList());
            return "admin/new-post";
        }
        Long userId = UserContextUtils.getCurrentUserIdOrThrow();
        Set<String> tags = Arrays.stream(form.tags.split(",")).collect(Collectors.toSet());
        CreatePostParams params =
                new CreatePostParams(form.title, form.mdContent, form.category, tags, form.status, userId);
        postService.createPost(params);
        log.info("Post created successfully");
        return "redirect:/admin/posts";
    }

    @DeleteMapping("/posts")
    @HxRequest
    View deletePosts(@RequestParam("postIds") List<Long> postIds) {
        log.info("Deleting posts with IDs: {}", postIds);
        postService.deletePosts(postIds);
        log.info("Posts deleted successfully");
        return new HtmxRefreshView();
    }

    @PutMapping("/posts/unpublish")
    @HxRequest
    View unpublishPosts(@RequestParam("postIds") List<Long> postIds) {
        log.info("Unpublishing posts with IDs: {}", postIds);
        postService.updatePostStatus(postIds, PostStatus.DRAFT);
        log.info("Posts unpublished successfully");
        return new HtmxRefreshView();
    }

    @PutMapping("/posts/publish")
    @HxRequest
    View publishPosts(@RequestParam("postIds") List<Long> postIds) {
        log.info("Publishing posts with IDs: {}", postIds);
        postService.updatePostStatus(postIds, PostStatus.PUBLISHED);
        log.info("Posts published successfully");
        return new HtmxRefreshView();
    }

    @PutMapping("/posts/archive")
    @HxRequest
    View archivePosts(@RequestParam("postIds") List<Long> postIds) {
        log.info("Archiving posts with IDs: {}", postIds);
        postService.updatePostStatus(postIds, PostStatus.ARCHIVED);
        log.info("Posts archived successfully");
        return new HtmxRefreshView();
    }

    @GetMapping("/posts/{id}/edit")
    String showEditPostForm(@PathVariable Long id, Model model) {
        log.info("Showing edit post form for post ID: {}", id);
        Post post = postService.getPostById(id);
        List<String> tags = post.getTags().stream().map(Tag::getLabel).toList();
        String tagsList = String.join(",", tags);

        PostForm form = new PostForm(
                post.getTitle(), post.getMdContent(), post.getCategory().getSlug(), tagsList, post.getStatus());

        model.addAttribute("postForm", form);
        model.addAttribute("postId", id);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute(
                "postStatuses",
                Arrays.stream(PostStatus.values()).map(Enum::name).toList());
        return "admin/edit-post";
    }

    @PutMapping("/posts/{id}")
    String updatePost(
            @PathVariable Long id,
            @ModelAttribute("postForm") @Valid PostForm form,
            BindingResult result,
            Model model) {
        log.info("Updating post with ID: {}", id);
        if (result.hasErrors()) {
            log.warn("Validation errors occurred while updating post with ID: {}", id);
            model.addAttribute("postForm", form);
            model.addAttribute("postId", id);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute(
                    "postStatuses",
                    Arrays.stream(PostStatus.values()).map(Enum::name).toList());
            return "admin/edit-post";
        }
        Long userId = UserContextUtils.getCurrentUserIdOrThrow();
        Set<String> tags = Arrays.stream(form.tags.split(",")).collect(Collectors.toSet());
        var params = new UpdatePostParams(id, form.title, form.mdContent, form.category, tags, form.status, userId);
        postService.updatePost(params);
        log.info("Post with ID {} updated successfully", id);
        return "redirect:/admin/posts";
    }

    record PostForm(
            @NotBlank(message = "{validation.title.required}") String title,
            @NotBlank(message = "{validation.content.required}") String mdContent,
            @NotBlank(message = "{validation.category.required}") String category,
            String tags,
            @NotNull(message = "{validation.status.required}") PostStatus status) {}
}
