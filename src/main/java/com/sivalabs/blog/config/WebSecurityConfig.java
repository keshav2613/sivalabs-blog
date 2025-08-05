package com.sivalabs.blog.config;

import com.sivalabs.blog.shared.models.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] publicPaths = {
            "/",
            "/favicon.ico",
            "/actuator/**",
            "/error",
            "/webjars/**",
            "/assets/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/user-images/**",
            "/login",
            "/contact",
        };
        http.securityMatcher("/**");

        http.csrf(csrf -> csrf.ignoringRequestMatchers("/my-profile/image"));

        http.authorizeHttpRequests(r -> r.requestMatchers(publicPaths)
                .permitAll()
                .requestMatchers("/admin/messages", "/admin/settings", "/admin/users")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/admin/posts", "/admin/comments", "/admin/tags")
                .hasRole("ADMIN")
                .requestMatchers("/admin/**")
                .hasAnyRole("AUTHOR", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/posts", "/posts/{slug}", "/categories/*/posts", "/tags/*/posts")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/posts/*/comments", "/contact/messages", "/newsletter/subscribe")
                .permitAll()
                .anyRequest()
                .authenticated());

        http.formLogin(formLogin ->
                formLogin.loginPage("/login").defaultSuccessUrl("/", true).permitAll());

        http.logout(logout -> logout.logoutRequestMatcher(
                        PathPatternRequestMatcher.withDefaults().matcher("/logout"))
                .logoutSuccessUrl("/")
                .permitAll());

        return http.build();
    }

    @Bean
    RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(Role.getRoleHierarchy());
    }
}
