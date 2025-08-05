package com.sivalabs.blog.admin.taxonomy;

import com.sivalabs.blog.shared.exceptions.BadRequestException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
class AdminCategoriesController {
    private static final Logger log = LoggerFactory.getLogger(AdminCategoriesController.class);

    private final CategoryService categoryService;

    AdminCategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    String showCategories(Model model) {
        log.info("Fetching all categories");
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("category", new CreateCategoryForm("", ""));
        return "admin/categories";
    }

    @PostMapping("/categories")
    String createCategory(
            @ModelAttribute("category") @Valid CreateCategoryForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        log.info("Creating a new category with label: {}", form.label);
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "admin/categories";
        }
        try {
            categoryService.createCategory(form.label, form.slug);
            redirectAttributes.addFlashAttribute("successMessage", "info.category_created_successfully");
        } catch (BadRequestException e) {
            log.error("Error creating category", e);
            result.reject("error.create_category", new Object[] {e.getMessage()}, "Error creating category");
            model.addAttribute("categories", categoryService.findAll());
            return "admin/categories";
        }
        return "redirect:/admin/categories";
    }

    record CreateCategoryForm(
            @NotBlank(message = "{validation.label.required}") String label,
            @NotBlank(message = "{validation.slug.required}") String slug) {}
}
