package org.example.gitrepossearch;

import org.example.gitrepossearch.dto.BranchDTO;
import org.example.gitrepossearch.dto.RepositoryDTO;
import org.example.gitrepossearch.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "github.token=ghp_yourRealGitHubTokenHere"
})
class GithubServiceIntegrationTest {

    @Autowired
    private GithubService githubService;

    @Test
    void shouldReturnNonForkRepositoriesWithBranches_givenValidUser() {
        // given
        String username = "Alexa13422";

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
    }
}
