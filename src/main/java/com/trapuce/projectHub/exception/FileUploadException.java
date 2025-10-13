package com.trapuce.projectHub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super(message);
    }
}
