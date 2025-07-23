package org.example.gitrepossearch.exception;

import org.springframework.http.HttpStatus;

public class GitHubClientException extends GitHubApiException {
    public GitHubClientException() {
        super("Failed to fetch data from GitHub", HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
