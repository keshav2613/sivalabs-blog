package com.sivalabs.blog.admin.taxonomy;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRefreshView;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/admin")
class AdminTagsController {
    private static final Logger log = LoggerFactory.getLogger(AdminTagsController.class);
    private final TagService tagService;

    AdminTagsController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/tags")
    String showTags(Model model) {
        log.info("Fetching all tags");
        model.addAttribute("tags", tagService.findAll());
        return "admin/tags";
    }

    @DeleteMapping("/tags")
    @HxRequest
    View deleteTags(@RequestParam("tagIds") List<Long> tagIds) {
        log.info("Deleting tags with ids: {}", tagIds);
        tagService.deleteTags(tagIds);
        return new HtmxRefreshView();
    }
}
