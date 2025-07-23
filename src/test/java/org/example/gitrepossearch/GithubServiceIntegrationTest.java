package org.example.gitrepossearch;

import org.example.gitrepossearch.dto.BranchDTO;
import org.example.gitrepossearch.dto.RepositoryDTO;
import org.example.gitrepossearch.exception.RepositoryNotFoundException;
import org.example.gitrepossearch.exception.UserNotFoundException;
import org.example.gitrepossearch.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = {
        "github.token=sometoken"
})
class GithubServiceIntegrationTest {

    @Autowired
    private GithubService githubService;

    @Test
    void shouldReturnNonForkRepositoriesWithBranches_givenValidUser() {
        // given existing use
        String username = "AlexA13422";

        // when
        List<RepositoryDTO> result = githubService.getAllRepositoriesNotForks(username);

        // then
        assertThat(result).isNotNull().isNotEmpty();

        for (RepositoryDTO repo : result) {
            assertThat(repo.isFork()).isFalse(); // only non-forks
            assertThat(repo.getName()).isNotEmpty();
            assertThat(repo.getBranches()).isNotNull();

            for (BranchDTO branch : repo.getBranches()) {
                assertThat(branch.getName()).isNotEmpty();
                assertThat(branch.getLastCommitSha()).isNotEmpty();
            }
        }
        //user does not exist
        String username1 = "unknownUser2342tfg34";
        assertThatThrownBy(() -> githubService.getAllRepositoriesNotForks(username1))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("GitHub user not found");

        //repo does not exist
        String fakeRepo = "nonexistent-repo-name-123";
        assertThatThrownBy(() -> githubService.getBranchesPerRepo(username1, fakeRepo))
                .isInstanceOf(RepositoryNotFoundException.class)
                .hasMessageContaining("Repository not found");

        // user cannot be empty
            assertThatThrownBy(() -> githubService.getAllRepositoriesNotForks(""))
                    .isInstanceOf(UserNotFoundException.class);

            assertThatThrownBy(() -> githubService.getAllRepositoriesNotForks(null))
                .isInstanceOf(UserNotFoundException.class);
    }
}
