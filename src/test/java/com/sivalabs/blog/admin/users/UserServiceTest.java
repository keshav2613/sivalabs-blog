package com.sivalabs.blog.admin.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sivalabs.blog.BaseServiceTest;
import com.sivalabs.blog.shared.entities.User;
import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import com.sivalabs.blog.shared.models.Role;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserServiceTest extends BaseServiceTest {
    @Autowired
    private UserService userService;

    @Test
    void findByEmail_shouldReturnUser_whenUserExists() {
        // When
        Optional<User> userOpt = userService.findByEmail("admin@gmail.com");

        // Then
        assertThat(userOpt).isPresent();
        User user = userOpt.get();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("SivaLabs");
        assertThat(user.getEmail()).isEqualTo("admin@gmail.com");
        assertThat(user.getPassword()).isNotNull();
        assertThat(user.getRole()).isEqualTo(Role.ROLE_ADMIN);
        assertThat(user.getBio())
                .isEqualTo(
                        "Siva is a DevOps Specialist and Tech Enthusiast. He writes about Docker, Kubernetes, Linux, and AWS technologies.");
        assertThat(user.getImage()).isEqualTo("/images/authors/admin.png");
    }

    @Test
    void createUser_shouldCreateNewUser() {
        // Given
        String name = "Test User";
        String email = "testuser@example.com";
        String password = "password";
        Role role = Role.ROLE_AUTHOR;
        CreateUserParams params = new CreateUserParams(name, email, password, role);

        // When
        userService.createUser(params);

        // Then
        Optional<User> userOpt = userService.findByEmail(email);
        assertThat(userOpt).isPresent();
        User user = userOpt.get();
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getRole()).isEqualTo(role);
    }

    @Test
    void getById_shouldReturnUser_whenUserExists() {
        // When
        User user = userService.getById(1L);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("SivaLabs");
        assertThat(user.getEmail()).isEqualTo("admin@gmail.com");
    }

    @Test
    void getById_shouldThrowException_whenUserDoesNotExist() {
        // When/Then
        assertThatThrownBy(() -> userService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id 999 not found");
    }

    @Test
    void updateUser_shouldUpdateUserNameAndBio() {
        // Given
        Long userId = 1L;
        String newName = "Updated Name";
        String newBio = "Updated Bio";
        UpdateUserParams params = new UpdateUserParams(userId, newName, newBio);

        // When
        userService.updateUser(params);

        // Then
        User updatedUser = userService.getById(userId);
        assertThat(updatedUser.getName()).isEqualTo(newName);
        assertThat(updatedUser.getBio()).isEqualTo(newBio);
    }

    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExist() {
        // Given
        Long nonExistentUserId = 999L;
        UpdateUserParams params = new UpdateUserParams(nonExistentUserId, "Name", "Bio");

        // When/Then
        assertThatThrownBy(() -> userService.updateUser(params))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id 999 not found");
    }

    @Test
    void updateUserImage_shouldUpdateUserImage() {
        // Given
        Long userId = 1L;
        String newImage = "/images/new-image.jpg";

        // When
        userService.updateUserImage(userId, newImage);

        // Then
        User updatedUser = userService.getById(userId);
        assertThat(updatedUser.getImage()).isEqualTo(newImage);
    }

    @Test
    void updateUserImage_shouldThrowException_whenUserDoesNotExist() {
        // Given
        Long nonExistentUserId = 999L;
        String newImage = "/images/new-image.jpg";

        // When/Then
        assertThatThrownBy(() -> userService.updateUserImage(nonExistentUserId, newImage))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id 999 not found");
    }
}
