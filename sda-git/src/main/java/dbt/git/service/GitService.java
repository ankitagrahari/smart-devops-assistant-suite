package dbt.git.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dbt.git.dto.GitChangedFile;
import dbt.git.dto.GitFileContent;
import dbt.git.dto.GitSourceMetaDataDetailsDTO;
import dbt.git.dto.GitTreeResponse;
import dbt.git.entites.GitSourceMetaData;
import dbt.git.mapper.GitSourceMetaDataMapper;
import dbt.git.repo.GitFileMetaDataRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class GitService {

    private static final Logger logger = LoggerFactory.getLogger(GitService.class);
    private static final String GIT_FILE_TYPE_BLOB = "blob";

    @Value("${git.repo.name}")
    private String GIT_REPO_NAME;
    @Value("${git.owner}")
    private String GIT_OWNER;
    @Value("${git.api.url}")
    private String GITHUB_API_URL;
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RestClient restClient;
    private final GitFileMetaDataRepository gitFileMetaDataRepo;

    public GitService(RestClient.Builder restClientBuilder,
                      GitFileMetaDataRepository repo) {
        this.gitFileMetaDataRepo = repo;
        HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        this.restClient = restClientBuilder
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }

    private HttpEntity<String> generateHttpEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+ System.getenv("GIT_SDA_PAT"));
        headers.set("ContentType", MediaType.APPLICATION_JSON_VALUE);

        return new HttpEntity<>(headers);
    }

    public ResponseEntity<String> fetchPRDiff(String diffURL){
        if(Objects.nonNull(diffURL)){
            HttpEntity httpEntity = generateHttpEntity();
//            return restTemplate.exchange(diffURL, HttpMethod.GET, httpEntity, String.class);
            return restClient.get()
                    .uri(URI.create(diffURL))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "+ System.getenv("GIT_SDA_PAT"))
                    .header("ContentType", MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(String.class);
        }
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<List<GitChangedFile>> fetchPRFiles(String prNumber){
        try {
            if (Objects.nonNull(prNumber)) {
                HttpEntity<String> httpEntity = generateHttpEntity();
                //https://api.github.com/repos/ankitagrahari/smart-devops-assistant/pulls/3/files
                String gitURL = GITHUB_API_URL + "repos/" + GIT_OWNER + "/" + GIT_REPO_NAME + "/pulls/" + prNumber + "/files";
                logger.info("Fetch PR files: gitURL:{}", gitURL);
//                return restTemplate.exchange(gitURL, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {});
                return restClient.get()
                        .uri(URI.create(gitURL))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer "+ System.getenv("GIT_SDA_PAT"))
                        .header("ContentType", MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .toEntity(new ParameterizedTypeReference<>() {});
            }
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }

    public String fetchBranchSHA(String branchName) {
        try {
//            HttpEntity<String> httpEntity = generateHttpEntity();
            String gitURL = GITHUB_API_URL + "repos/" + GIT_OWNER + "/" + GIT_REPO_NAME + "/branches/" + branchName;
            logger.info("Fetch files from branch {} gitURL:{}", branchName, gitURL);

            ResponseEntity<String> response = restClient.get()
                    .uri(URI.create(gitURL))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "+ System.getenv("GIT_SDA_PAT"))
                    .header("ContentType", MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = OBJECT_MAPPER.readTree(response.getBody());
                logger.info("Branches Response:{}", response.getBody());
                return jsonNode.get("commit").get("sha").asText();
            } else {
                logger.error("Error fetching branch metadata URL:{} status:{}", gitURL, response.getStatusCode());
                if(response.getStatusCode().is3xxRedirection()){
                    logger.info("New Location:{}", response.getHeaders().getLocation());
                    String redirectedURL = Objects.requireNonNull(response.getHeaders().getLocation()).toString();
                    ResponseEntity<String> redirectedResponse = restClient.get()
                            .uri(URI.create(redirectedURL))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+ System.getenv("GIT_SDA_PAT"))
                            .header("ContentType", MediaType.APPLICATION_JSON_VALUE)
                            .retrieve()
                            .toEntity(String.class);
                    if (redirectedResponse.getStatusCode().is2xxSuccessful()) {
                        JsonNode jsonNode = OBJECT_MAPPER.readTree(response.getBody());
                        logger.info("redirect: Branches Response:{}", response.getBody());
                        if(Objects.nonNull(jsonNode.findPath("commit"))) {
                            return jsonNode.get("commit").get("sha").asText();
                        } else {
                            logger.error("Aborting....Redirected URL is not having expected data!!");
                        }
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON response", e);
        }
        return null;
    }

    public List<GitSourceMetaDataDetailsDTO> fetchGitFileMetaData(String sha) {

        if(Objects.isNull(sha)) {
            logger.error("Abort...SHA is null, cannot proceed!");
            return Collections.EMPTY_LIST;
        }
//        HttpEntity<String> httpEntity = generateHttpEntity();
//          https://api.github.com/repos/{{owner}}/{{repo}}/git/trees/{{sha}}?recursive=1
        String gitURL = GITHUB_API_URL + "repos/" + GIT_OWNER + "/" + GIT_REPO_NAME + "/git/trees/" + sha + "?recursive=1";
        logger.info("Fetch git tree gitURL:{}", gitURL);

        ResponseEntity<GitTreeResponse> response = restClient.get()
                .uri(URI.create(gitURL))
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+ System.getenv("GIT_SDA_PAT"))
                .header("ContentType", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toEntity(GitTreeResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            assert response.getBody() != null;
            return response.getBody().tree()
                    .stream()
                    .filter(t -> t.type().equals(GIT_FILE_TYPE_BLOB))
                    .toList();
        } else {
            logger.error("Error accessing URL:{}, status:{}", gitURL, response.getStatusCode());
        }

        return Collections.EMPTY_LIST;
    }

    public Pair<String, String> fetchChangedGitFileContent(GitSourceMetaDataDetailsDTO dto) {
        String encodedContent = fetchChangedFileContentFromGit(dto.url());
        return Pair.of(dto.path(), decodeContent(encodedContent));
    }

    public String fetchChangedFileContentFromGit(String url){
//      https://api.github.com/repos/{owner}/{repo}/git/blobs/{sha}
        logger.info("Fetch git changed file gitURL:{}", url);
        ResponseEntity<GitFileContent> response = restClient.get()
                .uri(URI.create(url))
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+ System.getenv("GIT_SDA_PAT"))
                .header("ContentType", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toEntity(GitFileContent.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            if(response.getBody() != null) {
                return response.getBody().content();
            }
        } else {
            logger.error("Error fetching file content URL:{}, status:{}", url, response.getStatusCode());
            return ResponseEntity.noContent().build().toString();
        }
        return "";
    }

    public String generateGitURL(String sha){
        return GITHUB_API_URL + "repos/" + GIT_OWNER + "/" + GIT_REPO_NAME + "/git/blobs/" + sha;
    }

    public GitSourceMetaDataDetailsDTO getGitFileMetaDataDTO(GitChangedFile gcf) {
        GitSourceMetaData entity = gitFileMetaDataRepo.findByPathAndType(gcf.filename(), GIT_FILE_TYPE_BLOB);
        if(Objects.nonNull(entity)) {
            logger.info("fetch from vector store:{}", entity);
        } else {
            String gitFileContentURL = generateGitURL(gcf.sha());
            logger.info("Not found in the H2 database. Fetch from URL {}", gitFileContentURL);
            entity = new GitSourceMetaData(gcf.filename(), GIT_FILE_TYPE_BLOB, gcf.sha(), 0, gitFileContentURL);
            gitFileMetaDataRepo.save(entity);
            logger.info("Entity saved to H2 for future use!");
        }
        return GitSourceMetaDataMapper.toDTO(entity);
    }

    private static String decodeContent(String encodedStr) {
        return new String(Base64.getDecoder().decode(encodedStr.replaceAll("\\n", "")));
    }
}
