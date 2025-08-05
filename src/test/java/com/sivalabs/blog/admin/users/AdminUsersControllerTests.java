package com.sivalabs.blog.admin.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.sivalabs.blog.AbstractIT;
import com.sivalabs.blog.shared.models.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

class AdminUsersControllerTests extends AbstractIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithUserDetails("admin@gmail.com")
    void showUsers_shouldDisplayAllUsersAndEmptyForm() {
        var result = mockMvcTester.get().uri("/admin/users").exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/users")
                .model()
                .containsKeys("users", "user", "roles")
                .satisfies(model -> {
                    // Verify that the users attribute contains all users from the database
                    assertThat(model.get("users"))
                            .asInstanceOf(LIST)
                            .hasSizeGreaterThanOrEqualTo(2); // Based on test-data.sql
                    // Verify that the roles attribute contains all roles
                    assertThat(model.get("roles")).asInstanceOf(LIST).hasSize(Role.values().length);
                });
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void createUser_shouldCreateNewUserWithValidData() {
        long countBefore = userRepository.count();

        var result = mockMvcTester
                .post()
                .uri("/admin/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Test User")
                .param("email", "testuser@example.com")
                .param("password", "password123")
                .param("role", Role.ROLE_AUTHOR.name())
                .exchange();

        assertThat(result).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/admin/users");

        // Verify user was created
        long countAfter = userRepository.count();
        assertThat(countAfter).isEqualTo(countBefore + 1);

        // Verify the user exists with the correct data
        var user = userRepository.findByEmailIgnoreCase("testuser@example.com");
        assertThat(user).isPresent();
        assertThat(user.get().getName()).isEqualTo("Test User");
        assertThat(user.get().getRole()).isEqualTo(Role.ROLE_AUTHOR);
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void createUser_shouldReturnValidationErrorsWhenDataIsInvalid() {
        var result = mockMvcTester
                .post()
                .uri("/admin/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "") // Empty name should cause validation error
                .param("email", "") // Empty email should cause validation error
                .param("password", "") // Empty password should cause validation error
                .param("role", "") // Empty role should cause validation error
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/users")
                .model()
                .containsKey("users")
                .extractingBindingResult("user")
                .hasErrorsCount(4)
                .hasFieldErrors("name", "email", "password", "role");
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void createUser_shouldHandleEmailAlreadyExistsException() {
        // Using an existing email from test-data.sql
        var result = mockMvcTester
                .post()
                .uri("/admin/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Duplicate Email User")
                .param("email", "admin@gmail.com") // Existing email in test-data.sql
                .param("password", "password123")
                .param("role", Role.ROLE_AUTHOR.name())
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .hasViewName("admin/users")
                .model()
                .containsKeys("users", "roles")
                .extractingBindingResult("user")
                .hasErrorsCount(1)
                .hasFieldErrors("email");
    }
}
