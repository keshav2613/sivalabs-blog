package com.sivalabs.blog.admin.dashboard;

import com.sivalabs.blog.admin.posts.PostService;
import com.sivalabs.blog.admin.taxonomy.CategoryService;
import com.sivalabs.blog.admin.taxonomy.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
class AdminDashboardController {
    private static final Logger log = LoggerFactory.getLogger(AdminDashboardController.class);
    private final PostService postService;
    private final CategoryService categoryService;
    private final TagService tagService;

    AdminDashboardController(PostService postService, TagService tagService, CategoryService categoryService) {
        this.postService = postService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    @GetMapping("/dashboard")
    String dashboard(Model model) {
        log.info("Fetching dashboard data");
        model.addAttribute("postsCount", postService.getPostsCount());
        model.addAttribute("categoriesCount", categoryService.getCategoriesCount());
        model.addAttribute("tagsCount", tagService.getTagsCount());
        model.addAttribute("commentsCount", postService.getCommentsCount());
        return "admin/dashboard";
    }
}
