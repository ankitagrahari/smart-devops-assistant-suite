package dbt.ai.service;

import dbt.ai.dto.PRSuggestionResponse;
import dbt.ai.dto.PRSummaryRequest;
import dbt.ai.dto.PRSummaryResponse;
import dbt.ai.dto.git.GitPRDiffRequest;
import dbt.ai.feignclient.GitClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final ChatClient chatClient;

    private final VectorStore vectorStore;
    private final GitClient gitClient;
    private final SlackService slackService;

    private static final Double SIMILARITY_THRESHOLD = 0.7;

    public AIService(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore,
            GitClient gitClient,
            SlackService slackService) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.vectorStore = vectorStore;
        this.gitClient = gitClient;
        this.slackService = slackService;
    }

    public ResponseEntity<PRSummaryResponse> generateSummary(PRSummaryRequest prSummaryRequest) {
        if (Objects.nonNull(prSummaryRequest.getPrUrl())) {
            logger.debug("Request Data:{}", prSummaryRequest.getPrUrl());

            ResponseEntity<String> prDiffResponse = gitClient.fetchPRDiff(
                    new GitPRDiffRequest(prSummaryRequest.getPrUrl()+".diff"));
            String prDiffStr = "";
            if(prDiffResponse.getStatusCode().is2xxSuccessful())
                prDiffStr = prDiffResponse.getBody();
            else
                return ResponseEntity.notFound().build();

            prSummaryRequest.setDiff(prDiffStr);
            PRSummaryResponse response = generateAISummary(prSummaryRequest);

            //TODO: Add PR Url and the Author of the PR also to the request.
            slackService.sendPRReviewToSlack(response);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();
    }


    public PRSuggestionResponse analyzePR(String prDiff, List<String> fileNames){
        PromptTemplate pt = new PromptTemplate("""
            Given the following context from the codebase and this PR diff {prDiff}, summarize and suggest improvements.
        """);

        String files = String.join("','", fileNames);
        logger.info("files: {}", files);
        //This query will list down the file content from vector db, and inject it with the prompt to provide context-aware suggestion
        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(SIMILARITY_THRESHOLD)
                        .filterExpression("path in ['"+ String.join("','", fileNames) + "']")
                        .topK(5)
                        .build())
                .build();

        PRSuggestionResponse response = chatClient
                .prompt(pt.create(Map.of("prDiff", prDiff)))
                .advisors(qaAdvisor)
                .call()
                .entity(PRSuggestionResponse.class);

        logger.debug("Response:{}", response);
        return response;
    }

    public PRSummaryResponse generateAISummary(PRSummaryRequest prSummaryRequest){
        PromptTemplate pt = new PromptTemplate("""
            Given the following PR title {title}, description {description} and optional difference {diff}, generate a clear 1-2 sentence summary
            describing what this PR does ?
        """);
        return chatClient
                .prompt(pt.create(Map.of("title", prSummaryRequest.getTitle(),
                        "description", prSummaryRequest.getDescription(),
                        "diff", prSummaryRequest.getDiff())))
                .call()
                .entity(PRSummaryResponse.class);
    }
}
