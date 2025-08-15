package dbt.git.service;

import dbt.git.dto.GitChangedFile;
import dbt.git.dto.ai.AnalyzePRRequest;
import dbt.git.dto.ai.PRSuggestionResponse;
import dbt.git.dto.webhook.WebhookRequest;
import dbt.git.feignclient.AIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GitWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(GitWebhookService.class);

    private final AIClient aiClient;
    private final GitService gitService;

    public GitWebhookService(AIClient aiClient,
                             GitService gitService) {
        this.aiClient = aiClient;
        this.gitService = gitService;
    }

    public ResponseEntity<PRSuggestionResponse> analyzePR(WebhookRequest request) {
        if (Objects.nonNull(request.pullRequest()) && request.pullRequest().number() > 0) {
            logger.info("Request Data:{}--{}--{}", request.pullRequest().number(), request.pullRequest().url(), request.pullRequest().state());

            ResponseEntity<String> prDiffResponse = gitService.fetchPRDiff(request.pullRequest().diffUrl());
            String prDiffStr = "";
            if(prDiffResponse.getStatusCode().is2xxSuccessful())
                prDiffStr = prDiffResponse.getBody();
            else
                return ResponseEntity.notFound().build();

            List<String> fileNameChanged = null;
            ResponseEntity<List<GitChangedFile>> response = gitService.fetchPRFiles(request.pullRequest().number().toString());
            if(response.getStatusCode().is2xxSuccessful()){
                List<GitChangedFile> changedFiles = response.getBody();

                if(Objects.nonNull(changedFiles) && !changedFiles.isEmpty()) {

                    fileNameChanged = changedFiles.stream().map(GitChangedFile::filename).toList();

                    logger.info("Start loading Vector Store with changed files...");
                    long start = System.currentTimeMillis();
                    aiClient.addToVectorStore(changedFiles);
                    logger.info("Vector store loading completes in {}ms !!", (System.currentTimeMillis()-start));
                } else {
                    return ResponseEntity.noContent().build();
                }
            }

            return ResponseEntity.ok(aiClient.analyzePR(new AnalyzePRRequest(prDiffStr, fileNameChanged)));
        }
        return ResponseEntity.badRequest().build();
    }

}
