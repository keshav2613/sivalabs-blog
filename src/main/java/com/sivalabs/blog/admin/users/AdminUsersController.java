package com.sivalabs.blog.admin.users;

import com.sivalabs.blog.shared.exceptions.EmailAlreadyExistsException;
import com.sivalabs.blog.shared.models.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
class AdminUsersController {
    private static final Logger log = LoggerFactory.getLogger(AdminUsersController.class);

    private final UserService userService;

    AdminUsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    String showUsers(Model model) {
        log.info("Fetching all users");
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", new CreateUserForm("", "", "", Role.ROLE_AUTHOR.name()));
        model.addAttribute("roles", Arrays.stream(Role.values()).map(Enum::name).toList());
        return "admin/users";
    }

    @PostMapping("/users")
    String createUser(@ModelAttribute("user") @Valid CreateUserForm form, BindingResult result, Model model) {
        log.info("Creating a new user with email: {}", form.email);
        if (result.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            model.addAttribute(
                    "roles", Arrays.stream(Role.values()).map(Enum::name).toList());
            return "admin/users";
        }
        try {
            var params = new CreateUserParams(form.name, form.email, form.password, Role.valueOf(form.role));
            userService.createUser(params);
        } catch (EmailAlreadyExistsException e) {
            log.error("Error creating user. Exception: {}", e.getMessage());
            result.rejectValue(
                    "email", "error.user_email_exists", new Object[] {e.getMessage()}, "Error creating user");
            model.addAttribute("users", userService.findAll());
            model.addAttribute(
                    "roles", Arrays.stream(Role.values()).map(Enum::name).toList());
            return "admin/users";
        }
        return "redirect:/admin/users";
    }

    record CreateUserForm(
            @NotBlank(message = "{validation.name.required}") String name,
            @NotBlank(message = "{validation.email.required}") String email,
            @NotBlank(message = "{validation.password.required}") String password,
            @NotBlank(message = "{validation.role.required}") String role) {}
}
