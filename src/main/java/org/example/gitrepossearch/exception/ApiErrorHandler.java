package org.example.gitrepossearch.exception;

import org.example.gitrepossearch.dto.ErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(GitHubApiException.class)
    public ResponseEntity<ErrorDto> handleGitHubException(GitHubApiException ex) {
        ErrorDto body = new ErrorDto(ex.getStatus().value(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }
}