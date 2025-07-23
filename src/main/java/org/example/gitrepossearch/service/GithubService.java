package org.example.gitrepossearch.service;

import lombok.extern.slf4j.Slf4j;
import org.example.gitrepossearch.dto.BranchDTO;
import org.example.gitrepossearch.dto.RepositoryDTO;
import org.example.gitrepossearch.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Slf4j
@Service
public class GithubService {

    @Value("${github.token}")
    private String githubToken;
    private final RestTemplate restTemplate;
    private final Executor executor;

    public GithubService(RestTemplateBuilder builder, Executor githubExecutor) {
        this.executor = githubExecutor;
        this.restTemplate = builder
                .rootUri("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer: " + githubToken)
                .build();
    }

    public List<RepositoryDTO> getAllRepositoriesNotForks(String user) {
        validateUser(user);
        
        return getRepos(user)
                .stream()
                .filter(repo -> !repo.isFork())
                .map(repo -> fetchBranchesAsync(user, repo))
                .collect(toList());
    }

    private void validateUser(String user) {
        if (user == null || user.isEmpty()) {
            throw new UserNotFoundException("User cannot be null or empty");
        }
    }

    private RepositoryDTO fetchBranchesAsync(String user, RepositoryDTO repo) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                List<BranchDTO> branches = getBranchesPerRepo(user, repo.getName());
                repo.setBranches(branches);
                return repo;
            }, executor)
            .join();
        } catch (Exception e) {
            log.error("Error fetching branches for repository: {}", repo.getName(), e);
            throw new GitHubClientException();
        }
    }

    @Cacheable(value = "repos", key = "#user", condition = "#user != null")
    public List<RepositoryDTO> getRepos(String user) {
        String url = String.format("/users/%s/repos?per_page=100", user);
        
        try {
            ResponseEntity<RepositoryDTO[]> response = restTemplate.getForEntity(url, RepositoryDTO[].class);
            RepositoryDTO[] repositories = response.getBody();
            
            if (repositories == null || repositories.length == 0) {
                return List.of();
            }
            
            return Arrays.stream(repositories)
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException.NotFound ex) {
                throw new UserNotFoundException(user);
        } catch (RestClientException ex) {
            throw new GitHubClientException();
        }
    }

    @Cacheable(value = "branches", key = "#user + ':' + #repo")
    public List<BranchDTO> getBranchesPerRepo(String user, String repo) {
        String url = String.format("/repos/%s/%s/branches?per_page=100", user, repo);
        
        try {
            ResponseEntity<BranchDTO[]> response = restTemplate.getForEntity(url, BranchDTO[].class);
            BranchDTO[] branches = response.getBody();
            
            if (branches == null) {
                return List.of();
            }
            
            return Arrays.stream(branches)
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException.NotFound ex) {
                throw new RepositoryNotFoundException(user, repo);
        } catch (RestClientException ex) {
            throw new GitHubClientException();
        }
    }
}
