package com.sivalabs.blog.admin.users;

import com.sivalabs.blog.shared.entities.User;
import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    @Override
    default User getById(Long id) {
        return findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    default User getByEmail(String email) {
        return findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }

    boolean existsByEmailIgnoreCase(String email);
}
