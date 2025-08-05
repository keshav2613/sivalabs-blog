package com.sivalabs.blog.auth;

import com.sivalabs.blog.shared.entities.User;
import com.sivalabs.blog.shared.models.SecurityUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
class SecurityUserDetailsService implements UserDetailsService {
    private final SecurityUserRepository userRepository;

    SecurityUserDetailsService(SecurityUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        return userRepository
                .findByEmailIgnoreCase(userName)
                .map(this::toSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("Email " + userName + " not found"));
    }

    private SecurityUser toSecurityUser(User user) {
        return new SecurityUser(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().name());
    }
}
