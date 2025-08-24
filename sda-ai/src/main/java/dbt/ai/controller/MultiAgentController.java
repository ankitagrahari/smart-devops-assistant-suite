package dbt.ai.controller;

import dbt.ai.dto.git.GitPRDiffRequest;
import dbt.ai.dto.multiagent.MultiAgentResponse;
import dbt.ai.clients.GitClient;
import dbt.ai.service.multiagent.AgentOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai/multiagent")
public class MultiAgentController {

    private static final Logger log = LoggerFactory.getLogger(MultiAgentController.class);
    VectorStore vectorStore;
    AgentOrchestrator orchestrator;
    GitClient gitClient;

    public MultiAgentController(VectorStore vectorStore,
                                AgentOrchestrator orchestrator,
                                GitClient gitClient) {
        this.vectorStore = vectorStore;
        this.orchestrator = orchestrator;
        this.gitClient = gitClient;
    }

    @GetMapping("/echo")
    ResponseEntity<String> echo(){
        return ResponseEntity.ok("ai/multiagent working...!");
    }

    @GetMapping("/analyze-pr/{prNumber}")
    ResponseEntity<MultiAgentResponse> analyzePRAndGenerateTestCase(@PathVariable String prNumber){

        ResponseEntity<String> prDiffRE = gitClient.fetchPRDiff(new GitPRDiffRequest("", prNumber));
        if(prDiffRE.getStatusCode().is2xxSuccessful()) {
            String prDiff = prDiffRE.getBody();
            log.info("multiagent: prDiff:{}", prDiff);
            if(Objects.nonNull(prDiff)) {
                SearchRequest request = SearchRequest.builder()
                        .query(prDiff)
                        .topK(2)
                        .similarityThreshold(0.8)
                        .build();
                String context = Objects.requireNonNull(vectorStore.similaritySearch(request))
                        .stream()
                        .map(Document::getFormattedContent)
                        .collect(Collectors.joining("\n"));

                return ResponseEntity.ok(orchestrator.executeAgents(context, prDiff));
            }
        } else {
            MultiAgentResponse errorResponse = new MultiAgentResponse();
            errorResponse.setError("Error finding PR difference");
            errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorResponse);
        }

        return null;
    }
}
