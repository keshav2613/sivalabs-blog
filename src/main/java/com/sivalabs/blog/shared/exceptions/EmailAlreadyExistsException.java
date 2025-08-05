package com.sivalabs.blog.shared.exceptions;

public class EmailAlreadyExistsException extends BadRequestException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
