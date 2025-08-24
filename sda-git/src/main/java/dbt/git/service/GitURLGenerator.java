package dbt.git.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GitURLGenerator {

    @Value("${git.repo.name}")
    private String GIT_REPO_NAME;
    @Value("${git.owner}")
    private String GIT_OWNER;
    @Value("${git.api.url}")
    private String GITHUB_API_URL;
    @Value("${git.url}")
    private String GITHUB_URL;

    public String generateGitURLBySHA(String sha){
        return GITHUB_API_URL + "repos/" + GIT_OWNER + "/" + GIT_REPO_NAME + "/git/blobs/" + sha;
    }

    public String generateGitPRFileURLByPRNumber(String prNumber){
        return GITHUB_API_URL + "repos/" + GIT_OWNER + "/" + GIT_REPO_NAME + "/pulls/" + prNumber + "/files";
    }

    public String generateGitBranchURLByName(String branchName){
        return GITHUB_API_URL + "repos/" + GIT_OWNER + "/" + GIT_REPO_NAME + "/branches/" + branchName;
    }

    public String generateGitFileMetaDataURLBySHA(String sha){
        return GITHUB_API_URL + "repos/" + GIT_OWNER + "/" + GIT_REPO_NAME + "/git/trees/" + sha + "?recursive=1";
    }

    /**
     * https://github.com/ankitagrahari/smart-devops-assistant/pull/6.diff
     * @param prNumber
     * @return
     */
    public String generateGitPRDiffURLByPRNumber(String prNumber){
        return GITHUB_URL + "/" +GIT_OWNER + "/" + GIT_REPO_NAME + "/pull/" + prNumber + ".diff";
    }
}
