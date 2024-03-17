package edu.java.models.pojo;

import edu.java.models.dto.GithubBranchesDTO;
import edu.java.models.dto.GithubRepositoryDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class GithubContent {
    private GithubRepositoryDTO githubRepositoryDTO;
    private GithubBranchesDTO[] githubBranchesDTO;
}
