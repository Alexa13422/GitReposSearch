package org.example.gitrepossearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoryDTO {
    private String name;
    private String owner;
    private boolean fork;
    private List<BranchDTO> branches;

    @JsonProperty("owner")
    private void extractOwner(Map<String, Object> owner) {
        this.owner = (String) owner.get("login");
    }

}
