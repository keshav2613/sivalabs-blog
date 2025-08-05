package com.sivalabs.blog.config;

import com.sivalabs.blog.shared.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    public static final String UPDATE_MY_PROFILE_IMAGE_URL = "/my-profile/image";
    public static final String MY_PROFILE_URL = "/my-profile";

    @ExceptionHandler(ResourceNotFoundException.class)
    ModelAndView handle(ResourceNotFoundException e) {
        log.error("Resource not found", e);
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", e.getMessage());
        mav.addObject("exception", e);
        mav.setViewName("error/404");
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    ModelAndView handle(AccessDeniedException e) {
        log.error("Access denied", e);
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", e.getMessage());
        mav.addObject("exception", e);
        mav.setViewName("error/403");
        return mav;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(
            MaxUploadSizeExceededException e, HttpServletRequest req, RedirectAttributes redirectAttributes) {
        log.error("Max upload size exceeded", e);
        String requestURI = req.getRequestURI();
        if (UPDATE_MY_PROFILE_IMAGE_URL.equals(requestURI)) {
            redirectAttributes.addFlashAttribute("errorMessage", "error.profile_image_max_size_exceeded");
            return "redirect:" + MY_PROFILE_URL;
        }
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    ModelAndView handle(Exception e) {
        log.error("Unhandled exception", e);
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", e.getMessage());
        mav.addObject("exception", e);
        mav.setViewName("error/500");
        return mav;
    }
}
