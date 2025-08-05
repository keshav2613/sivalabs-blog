package com.sivalabs.blog.admin.settings;

import com.sivalabs.blog.shared.entities.Settings;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
class SettingsController {
    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
    private final SettingsService settingsService;

    SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/settings")
    String showSettings(Model model) {
        log.info("Fetching application settings");
        var settings = settingsService.getSettings();
        model.addAttribute("settings", settings);
        return "admin/settings";
    }

    @PutMapping("/settings")
    String updateSettings(@ModelAttribute("settings") @Valid Settings settings, BindingResult result) {
        log.info("Updating application settings");
        if (result.hasErrors()) {
            return "admin/settings";
        }
        settingsService.update(settings);
        return "redirect:/admin/settings";
    }
}
