package dbt.ai.feignclient;

import dbt.ai.dto.GitChangedFile;
import dbt.ai.dto.GitSourceMetaDataDetailsDTO;
import dbt.ai.dto.git.GitPRDiffRequest;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="sda-git")
@Headers("Content-Type: application/json")
public interface GitClient {

    @PostMapping("/git/pr/diff")
    ResponseEntity<String> fetchPRDiff(@RequestBody GitPRDiffRequest request);

    @PostMapping("/git/pr/metadata")
    GitSourceMetaDataDetailsDTO getGitFileMetaDataDTO(@RequestBody GitChangedFile request);

    @PostMapping("/git/content")
    Pair<String, String> fetchChangedGitFileContent(@RequestBody GitSourceMetaDataDetailsDTO dto);
}
