package com.sivalabs.blog.admin.messages;

import com.sivalabs.blog.shared.entities.Message;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

@Controller
@RequestMapping("/admin")
class AdminMessagesController {
    private static final Logger log = LoggerFactory.getLogger(AdminMessagesController.class);
    private final MessageService messageService;

    AdminMessagesController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/messages")
    String showMessages(Model model, @RequestParam(defaultValue = "1") Integer page) {
        log.info("Fetching messages for page: {}", page);
        PagedResult<Message> messages = messageService.getMessages(page);
        model.addAttribute("messages", messages);
        var pagination = new Pagination<>(messages, "/admin/messages");
        model.addAttribute("pagination", pagination);
        return "admin/messages";
    }

    @DeleteMapping("/messages")
    @HxRequest
    View deleteMessages(@RequestParam("messageIds") List<Long> messageIds) {
        log.info("Deleting messages with IDs: {}", messageIds);
        messageService.deleteMessages(messageIds);
        log.info("Messages deleted successfully");
        return new HtmxRefreshView();
    }
}
