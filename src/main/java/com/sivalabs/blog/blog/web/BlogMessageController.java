package com.sivalabs.blog.blog.web;

import com.sivalabs.blog.blog.domain.BlogMessageService;
import com.sivalabs.blog.blog.domain.BlogSettingsService;
import com.sivalabs.blog.blog.domain.models.CreateMessageParams;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
class BlogMessageController {
    private static final Logger log = LoggerFactory.getLogger(BlogMessageController.class);
    private final BlogSettingsService settingsService;
    private final BlogMessageService messageService;

    BlogMessageController(BlogSettingsService settingsService, BlogMessageService messageService) {
        this.settingsService = settingsService;
        this.messageService = messageService;
    }

    @GetMapping("/contact")
    String contact(Model model) {
        log.info("Rendering contact page");
        addContactInfo(model);
        model.addAttribute("message", new MessageForm("", "", "", ""));
        return "blog/contact";
    }

    @PostMapping("/contact/messages")
    String postMessage(
            @ModelAttribute("message") @Valid MessageForm message,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        log.info("Received a new message from {}", message.name);
        if (result.hasErrors()) {
            addContactInfo(model);
            return "blog/contact";
        }
        var params = new CreateMessageParams(message.name, message.email, message.subject, message.content);
        messageService.createMessage(params);
        redirectAttributes.addFlashAttribute("successMessage", "info.message_sent");
        return "redirect:/contact";
    }

    private void addContactInfo(Model model) {
        var settings = settingsService.getSettings();
        var contact = new Contact(
                settings.getAdminContactName(),
                settings.getAdminContactEmail(),
                settings.getAdminContactAddress(),
                settings.getAdminContactTwitter(),
                settings.getAdminContactGithub(),
                settings.getAdminContactLinkedin(),
                settings.getAdminContactYoutube());
        model.addAttribute("contact", contact);
    }

    public record Contact(
            String name,
            String email,
            String address,
            String twitter,
            String github,
            String linkedIn,
            String youtube) {}

    record MessageForm(
            @NotBlank(message = "{validation.name.required}") String name,
            @NotBlank(message = "{validation.email.required}") @Email(message = "{validation.email.invalid}") String email,
            @NotBlank(message = "{validation.subject.required}") String subject,
            @NotBlank(message = "{validation.content.required}") String content) {}
}
