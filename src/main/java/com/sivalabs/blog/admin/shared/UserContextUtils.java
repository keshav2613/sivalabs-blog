package com.sivalabs.blog.admin.shared;

import com.sivalabs.blog.shared.models.SecurityUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserContextUtils {
    public static Long getCurrentUserIdOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("Access denied");
        }
        var principal = authentication.getPrincipal();
        if (principal instanceof SecurityUser securityUser) {
            return securityUser.getId();
        }
        throw new AccessDeniedException("Access denied");
    }
}
