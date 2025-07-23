package org.example.gitrepossearch.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends GitHubApiException {
    public UserNotFoundException(String username) {
        super("GitHub user not found: " + username, HttpStatus.NOT_FOUND);
    }
}