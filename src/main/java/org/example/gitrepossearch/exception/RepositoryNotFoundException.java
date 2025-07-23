package org.example.gitrepossearch.exception;

import org.springframework.http.HttpStatus;

public class RepositoryNotFoundException extends GitHubApiException {
    public RepositoryNotFoundException(String name, String repo) {
        super("Repository not found in GitHub: " + name + ":" + repo, HttpStatus.NOT_FOUND);
    }
}
