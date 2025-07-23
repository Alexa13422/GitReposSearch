package org.example.gitrepossearch.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class GitHubApiException extends RuntimeException {

    private final HttpStatus status;

    protected GitHubApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
