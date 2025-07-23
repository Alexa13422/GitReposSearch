package org.example.gitrepossearch.service;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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
    @Cacheable(value = "repos", key = "#user")
    public List<RepositoryDTO> getAllRepositoriesNotForks(String user) {
        if (user == null || user.isEmpty()) {
            throw new UserNotFoundException(user + " cannot be empty");
        }

        String url = String.format("/users/%s/repos?per_page=100", user);
        RepositoryDTO[] repos;
        try {
            ResponseEntity<RepositoryDTO[]> response =
                    restTemplate.getForEntity(url, RepositoryDTO[].class);
            repos = response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException(user);
        } catch (RestClientException ex) {
            throw new GitHubClientException();
        }

        if (repos == null || repos.length == 0) {
            return List.of();
        }

        List<CompletableFuture<RepositoryDTO>> futures = Arrays.stream(repos)
                .filter(r -> !r.isFork())
                .map(repo -> CompletableFuture.supplyAsync(() -> {
                    List<BranchDTO> branches = getBranchesPerRepo(user, repo.getName());
                    repo.setBranches(branches);
                    return repo;
                }, executor))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    @Cacheable(value = "branches", key = "#user + ':' + #repo")
    public List<BranchDTO> getBranchesPerRepo(String user, String repo) {
        List<BranchDTO> branches = new ArrayList<>();

        String url = String.format(
                "/repos/%s/%s/branches?per_page=100", user, repo);
        ResponseEntity<BranchDTO[]> response;

        try {
            response = restTemplate.getForEntity(url, BranchDTO[].class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RepositoryNotFoundException(user, repo);
        } catch (RestClientException ex) {
            throw new GitHubClientException();
        }

        BranchDTO[] branchesResponse = response.getBody();
        if (branchesResponse != null && branchesResponse.length != 0) {
            branches.addAll(Arrays.asList(branchesResponse));
        }
        return branches;

    }
}
