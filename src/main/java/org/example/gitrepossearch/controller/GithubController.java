package org.example.gitrepossearch.controller;

import org.example.gitrepossearch.dto.RepositoryDTO;
import org.example.gitrepossearch.service.GithubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/repos/{user}")
    public ResponseEntity<List<RepositoryDTO>> getAllRepositoriesNotForks(@PathVariable String user) {
        List<RepositoryDTO> repositoryDTOList = githubService.getAllRepositoriesNotForks(user);
        return ResponseEntity.ok(repositoryDTOList);
    }
}
