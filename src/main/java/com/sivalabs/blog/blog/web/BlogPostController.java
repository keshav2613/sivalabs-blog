package com.sivalabs.blog.blog.web;

import com.sivalabs.blog.blog.domain.BlogCategoryService;
import com.sivalabs.blog.blog.domain.BlogPostService;
import com.sivalabs.blog.blog.domain.BlogSettingsService;
import com.sivalabs.blog.blog.domain.BlogTagService;
import com.sivalabs.blog.blog.domain.models.CreateCommentParams;
import com.sivalabs.blog.blog.domain.models.PostSummaryDTO;
import com.sivalabs.blog.shared.models.CommentStatus;
import com.sivalabs.blog.shared.models.PagedResult;
import com.sivalabs.blog.shared.models.Pagination;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
class BlogPostController {
    private static final Logger log = LoggerFactory.getLogger(BlogPostController.class);
    private final BlogTagService tagService;
    private final BlogCategoryService categoryService;
    private final BlogPostService postService;
    private final BlogSettingsService settingsService;

    public BlogPostController(
            BlogPostService postService,
            BlogTagService tagService,
            BlogCategoryService categoryService,
            BlogSettingsService settingsService) {
        this.postService = postService;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.settingsService = settingsService;
    }

    @GetMapping("/posts")
    String showPosts(Model model, @RequestParam(defaultValue = "1") Integer page) {
        log.info("Fetching posts for page: {}", page);
        model.addAttribute("categories", categoryService.getCategoriesWithPostCounts());
        model.addAttribute("tags", tagService.findAll());
        PagedResult<PostSummaryDTO> posts = postService.getLatestPosts(page);
        model.addAttribute("posts", posts);
        var pagination = new Pagination<>(posts, "/posts");
        model.addAttribute("pagination", pagination);
        return "blog/posts";
    }

    @GetMapping("/posts/search")
    String searchPosts(
            Model model, @RequestParam(name = "q") String query, @RequestParam(defaultValue = "1") Integer page) {
        log.info("Searching posts with query '{}' for page: {}", query, page);
        model.addAttribute("categories", categoryService.getCategoriesWithPostCounts());
        model.addAttribute("tags", tagService.findAll());
        PagedResult<PostSummaryDTO> posts = postService.searchPosts(query, page);
        model.addAttribute("posts", posts);
        var pagination = new Pagination<>(posts, "/posts/search", Map.of("q", query));
        model.addAttribute("pagination", pagination);
        return "blog/posts";
    }

    @GetMapping("/categories/{categorySlug}/posts")
    String showPostsByCategory(
            Model model, @PathVariable String categorySlug, @RequestParam(defaultValue = "1") Integer page) {
        log.info("Fetching posts for category '{}' for page: {}", categorySlug, page);
        model.addAttribute("categories", categoryService.getCategoriesWithPostCounts());
        model.addAttribute("tags", tagService.findAll());
        PagedResult<PostSummaryDTO> posts = postService.getPostsByCategory(categorySlug, page);
        model.addAttribute("posts", posts);
        var pagination = new Pagination<>(posts, "/categories/" + categorySlug + "/posts");
        model.addAttribute("pagination", pagination);
        return "blog/posts";
    }

    @GetMapping("/tags/{tagSlug}/posts")
    String showPostsByTag(Model model, @PathVariable String tagSlug, @RequestParam(defaultValue = "1") Integer page) {
        log.info("Fetching posts for tag '{}' for page: {}", tagSlug, page);
        model.addAttribute("categories", categoryService.getCategoriesWithPostCounts());
        model.addAttribute("tags", tagService.findAll());
        PagedResult<PostSummaryDTO> posts = postService.getPostsByTag(tagSlug, page);
        model.addAttribute("posts", posts);
        var pagination = new Pagination<>(posts, "/tags/" + tagSlug + "/posts");
        model.addAttribute("pagination", pagination);
        return "blog/posts";
    }

    @GetMapping("/posts/{slug}")
    String showPostDetails(Model model, @PathVariable String slug) {
        log.info("Fetching post details for slug: {}", slug);
        var postDetails = postService.getPostBySlug(slug);
        model.addAttribute("post", postDetails);
        model.addAttribute("comments", postService.getCommentsByPostSlug(slug));
        model.addAttribute("comment", new CreateCommentForm(postDetails.id(), "", "", ""));
        return "blog/post-details";
    }

    @PostMapping("/posts/{slug}/comments")
    String createComment(
            @PathVariable String slug,
            @ModelAttribute("comment") @Valid CreateCommentForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        log.info("Creating comment for post with slug: {}", slug);
        if (result.hasErrors()) {
            var postDetails = postService.getPostBySlug(slug);
            model.addAttribute("post", postDetails);
            model.addAttribute("comments", postService.getCommentsByPostSlug(slug));
            return "blog/post-details";
        }
        Boolean autoApprove = settingsService.getSettings().getAutoApproveComment();
        var status = autoApprove ? CommentStatus.APPROVED : CommentStatus.PENDING;
        var params = new CreateCommentParams(form.name(), form.email(), form.content(), form.postId(), status);
        postService.createComment(params);
        redirectAttributes.addFlashAttribute("successMessage", "info.comment_posted_successfully");
        return "redirect:/posts/" + slug + "#comments";
    }

    record CreateCommentForm(
            @NotNull Long postId,
            @NotBlank(message = "{validation.name.required}") String name,
            @Email(message = "{validation.email.invalid}") String email,
            @NotBlank(message = "{validation.content.required}") String content) {}
}
