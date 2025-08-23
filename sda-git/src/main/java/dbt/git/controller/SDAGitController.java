package dbt.git.controller;

import dbt.git.dto.GitChangedFile;
import dbt.git.dto.GitPRDiffRequest;
import dbt.git.dto.GitSourceMetaDataDetailsDTO;
import dbt.git.service.GitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/git")
public class SDAGitController {

    private static final Logger logger = LoggerFactory.getLogger(SDAGitController.class);

    GitService gitService;

    public SDAGitController(GitService gitService) {
        this.gitService = gitService;
    }

    @GetMapping("/echo")
    public ResponseEntity<String> allOK(){
        return ResponseEntity.ok("Git working...!");
    }

    @GetMapping("/pr/{prNumber}")
    public ResponseEntity<List<GitChangedFile>> fetchPRFiles(@PathVariable String prNumber){
        ResponseEntity<List<GitChangedFile>> responseEntity = gitService.fetchPRFiles(prNumber);
        if(responseEntity.getStatusCode().is2xxSuccessful()){
            List<GitChangedFile> filesChanged = responseEntity.getBody();
            assert filesChanged != null;


            return responseEntity;
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/file/content/{sha}")
    public String fetchFileContentBySHA(@PathVariable String sha){
        String  encodedContent = gitService.fetchChangedFileContentFromGit(gitService.generateGitURL(sha));
        encodedContent = encodedContent.replaceAll("\\n", "");
        logger.debug("encoded file content {}", encodedContent);
        String decodedStr = new String(Base64.getDecoder().decode(encodedContent));
        logger.debug("decodeStr {}", decodedStr);
        return decodedStr;
    }

    /**
     * Fetch the file content from Git based on path and
     *
     * @param dto
     * @return
     */
    @PostMapping("/content")
    public Pair<String, String> fetchChangedFileContent(@RequestBody GitSourceMetaDataDetailsDTO dto){
        return gitService.fetchChangedGitFileContent(dto);
    }

    /**
     * Fetches the PR difference for the PR URL like
     * https://api.github.com/repos/<owner>/<repo-name>/pulls/<prNumber>.diff
     *
     */
    @PostMapping("/pr/diff")
    public ResponseEntity<String> fetchPRDiff(@RequestBody GitPRDiffRequest request){
        return gitService.fetchPRDiff(request.prURL());
    }

    /**
     * Validate if the file is already saved in H2 database, else
     * gets the file content from Git, and saves the entity.
     *
     * @param gitChangedFile
     * @return
     */
    @PostMapping("/pr/metadata")
    public GitSourceMetaDataDetailsDTO getGitFileMetaDataDTO(@RequestBody GitChangedFile gitChangedFile){
        return gitService.getGitFileMetaDataDTO(gitChangedFile);
    }
}
