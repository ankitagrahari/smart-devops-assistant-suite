package dbt.ai.controller;

import dbt.ai.dto.git.GitPRDiffRequest;
import dbt.ai.dto.multiagent.MultiAgentResponse;
import dbt.ai.feignclient.GitClient;
import dbt.ai.service.multiagent.AgentOrchestrator;
import org.springframework.ai.document.Document;
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

        ResponseEntity<String> prDiffRE = gitClient.fetchPRDiff(new GitPRDiffRequest(prNumber));
        if(prDiffRE.getStatusCode().is2xxSuccessful()) {
            String prDiff = prDiffRE.getBody();

            if(Objects.nonNull(prDiff)) {
                String context = Objects.requireNonNull(vectorStore.similaritySearch(prDiff))
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
