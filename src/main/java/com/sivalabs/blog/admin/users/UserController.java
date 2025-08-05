package com.sivalabs.blog.admin.users;

import com.sivalabs.blog.admin.media.FileStorageService;
import com.sivalabs.blog.admin.shared.UserContextUtils;
import com.sivalabs.blog.shared.entities.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public static final String USER_IMAGES_DIR = "user-images";
    private final UserService userService;
    private final FileStorageService fileStorageService;

    UserController(UserService userService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/my-profile")
    String getProfile(Model model) {
        log.info("Fetching user profile");
        Long userId = UserContextUtils.getCurrentUserIdOrThrow();
        addProfileToModel(model);
        model.addAttribute("passwordChangeForm", new PasswordChangeForm(userId, "", "", ""));
        return "user/my-profile";
    }

    private void addProfileToModel(Model model) {
        Long userId = UserContextUtils.getCurrentUserIdOrThrow();
        User user = userService.getById(userId);
        UserProfileForm profile =
                new UserProfileForm(user.getId(), user.getName(), user.getEmail(), user.getBio(), user.getImage());
        model.addAttribute("profile", profile);
    }

    @PutMapping("/my-profile")
    String updateProfile(
            @ModelAttribute("profile") @Valid UserProfileForm profile,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        log.info("Updating user profile for user id: {}", profile.id);
        if (result.hasErrors()) {
            return "user/my-profile";
        }
        UpdateUserParams params = new UpdateUserParams(profile.id, profile.name, profile.bio);
        userService.updateUser(params);
        redirectAttributes.addFlashAttribute("successMessage", "info.profile_update_success");
        return "redirect:/my-profile";
    }

    @PutMapping("/my-profile/password")
    String changePassword(
            @ModelAttribute("passwordChangeForm") @Valid PasswordChangeForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        log.info("Changing password for user id: {}", form.id);
        if (result.hasErrors()) {
            addProfileToModel(model);
            return "user/my-profile";
        }

        if (!form.newPassword().equals(form.confirmPassword())) {
            result.rejectValue("confirmPassword", "validation.passwords.not_match");
            addProfileToModel(model);
            return "user/my-profile";
        }

        try {
            userService.changePassword(form.id(), form.oldPassword(), form.newPassword());
            redirectAttributes.addFlashAttribute("successMessage", "info.password_change_success");
        } catch (RuntimeException e) {
            log.error("Error changing password", e);
            redirectAttributes.addFlashAttribute("errorMessage", "info.password_change_failure");
            return "redirect:/my-profile";
        }
        return "redirect:/my-profile";
    }

    @PutMapping("/my-profile/image")
    String updateProfileImage(
            @RequestParam("id") Long id,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes)
            throws IOException {
        log.info("Updating profile image for user id: {}", id);
        try {
            String filePath = fileStorageService.storeFile(imageFile, USER_IMAGES_DIR);
            log.info("Image uploaded to path: {}", filePath);
            userService.updateUserImage(id, filePath);
            redirectAttributes.addFlashAttribute("successMessage", "info.profile_image_update_success");
        } catch (RuntimeException e) {
            log.error("Error uploading image", e);
            redirectAttributes.addFlashAttribute("errorMessage", "info.profile_image_update_failure");
        }
        return "redirect:/my-profile";
    }

    record UserProfileForm(
            @NotNull(message = "{validation.id.required}") Long id,
            @NotBlank(message = "{validation.name.required}") String name,
            @NotBlank(message = "{validation.email.required}") @Email(message = "{validation.email.invalid}") String email,
            @NotBlank(message = "{validation.bio.required}") String bio,
            String image) {}

    record PasswordChangeForm(
            @NotNull(message = "{validation.id.required}") Long id,
            @NotBlank(message = "{validation.old_password.required}") String oldPassword,
            @NotBlank(message = "{validation.new_password.required}") String newPassword,
            @NotBlank(message = "{validation.confirm_password.required}") String confirmPassword) {}
}
