package com.sivalabs.blog.admin.posts;

import com.sivalabs.blog.shared.entities.Comment;
import com.sivalabs.blog.shared.models.CommentStatus;
import com.sivalabs.blog.shared.models.PagedResult;
import com.sivalabs.blog.shared.models.Pagination;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRefreshView;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/admin")
class AdminCommentsController {
    private static final Logger log = LoggerFactory.getLogger(AdminCommentsController.class);
    private final PostService postService;

    AdminCommentsController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/comments")
    String showComments(Model model, @RequestParam(defaultValue = "1") Integer page) {
        log.info("Fetching comments for page: {}", page);
        PagedResult<Comment> comments = postService.getComments(page);
        model.addAttribute("comments", comments);
        var pagination = new Pagination<>(comments, "/admin/comments");
        model.addAttribute("pagination", pagination);
        return "admin/comments";
    }

    @DeleteMapping("/comments")
    @HxRequest
    View deleteComments(@RequestParam("commentIds") List<Long> commentIds) {
        log.info("Deleting comments with IDs: {}", commentIds);
        postService.deleteComments(commentIds);
        log.info("Comments deleted successfully");
        return new HtmxRefreshView();
    }

    @PutMapping("/comments/approve")
    @HxRequest
    View approveComments(@RequestParam("commentIds") List<Long> commentIds) {
        log.info("Approving comments with IDs: {}", commentIds);
        postService.updateCommentStatus(commentIds, CommentStatus.APPROVED);
        log.info("Comments approved successfully");
        return new HtmxRefreshView();
    }

    @PutMapping("/comments/spam")
    @HxRequest
    View rejectComments(@RequestParam("commentIds") List<Long> commentIds) {
        log.info("Marking comments as spam with IDs: {}", commentIds);
        postService.updateCommentStatus(commentIds, CommentStatus.SPAM);
        log.info("Comments marked as spam successfully");
        return new HtmxRefreshView();
    }
}
