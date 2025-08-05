package com.sivalabs.blog.admin.subscribers;

import com.sivalabs.blog.shared.entities.Subscriber;
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
class AdminSubscribersController {
    private static final Logger log = LoggerFactory.getLogger(AdminSubscribersController.class);
    private final SubscriberService subscriberService;

    AdminSubscribersController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping("/subscribers")
    String showSubscribers(Model model, @RequestParam(defaultValue = "1") Integer page) {
        log.info("Fetching subscribers for page: {}", page);
        PagedResult<Subscriber> subscribers = subscriberService.getSubscribers(page);
        model.addAttribute("subscribers", subscribers);
        var pagination = new Pagination<>(subscribers, "/admin/subscribers");
        model.addAttribute("pagination", pagination);
        return "admin/subscribers";
    }

    @DeleteMapping("/subscribers")
    @HxRequest
    View deleteSubscribers(@RequestParam("subscriberIds") List<Long> subscriberIds) {
        log.info("Deleting subscribers with ids: {}", subscriberIds);
        subscriberService.deleteSubscribers(subscriberIds);
        return new HtmxRefreshView();
    }
}
