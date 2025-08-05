package com.sivalabs.blog.admin.users;

import com.sivalabs.blog.shared.models.Role;

public record CreateUserParams(String name, String email, String password, Role role) {}
