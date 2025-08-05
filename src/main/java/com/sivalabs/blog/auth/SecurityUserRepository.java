package com.sivalabs.blog.auth;

import com.sivalabs.blog.shared.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
}
