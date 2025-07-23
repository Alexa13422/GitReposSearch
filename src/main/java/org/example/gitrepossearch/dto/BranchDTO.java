package org.example.gitrepossearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchDTO {
    private String name;
    private String lastCommitSha;

    @JsonProperty("commit")
    private void unpackCommit(Map<String, Object> commit) {
        this.lastCommitSha = (String) commit.get("sha");
    }
}
