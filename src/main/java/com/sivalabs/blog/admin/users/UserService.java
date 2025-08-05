package com.sivalabs.blog.admin.users;

import com.sivalabs.blog.shared.entities.User;
import com.sivalabs.blog.shared.exceptions.EmailAlreadyExistsException;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Transactional
    public void createUser(CreateUserParams params) {
        if (userRepository.existsByEmailIgnoreCase(params.email())) {
            throw new EmailAlreadyExistsException("User with email " + params.email() + " already exists");
        }
        var user = new User();
        user.setName(params.name());
        user.setEmail(params.email());
        user.setPassword(passwordEncoder.encode(params.password()));
        user.setRole(params.role());
        user.setImage("/images/authors/user.jpg");
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getById(Long userId) {
        return userRepository.getById(userId);
    }

    @Transactional
    public void updateUser(UpdateUserParams params) {
        var user = userRepository.getById(params.id());
        user.setName(params.name());
        user.setBio(params.bio());
        userRepository.save(user);
    }

    @Transactional
    public void updateUserImage(Long userId, String image) {
        var user = userRepository.getById(userId);
        user.setImage(image);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        var user = userRepository.getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
