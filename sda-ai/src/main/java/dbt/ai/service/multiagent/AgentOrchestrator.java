package dbt.ai.service.multiagent;

import dbt.ai.dto.multiagent.MultiAgentResponse;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AgentOrchestrator {

    private final CodeReviewerAgent codeReviewerAgent;
    private final TestGeneratorAgent testGeneratorAgent;

    public AgentOrchestrator(CodeReviewerAgent codeReviewerAgent, TestGeneratorAgent testGeneratorAgent){
        this.codeReviewerAgent = codeReviewerAgent;
        this.testGeneratorAgent = testGeneratorAgent;
    }

    public MultiAgentResponse executeAgents(String context, String prDiff){

        MultiAgentResponse response = new MultiAgentResponse();
        CompletableFuture<Void> codeReviewerFuture = codeReviewerAgent.process(context, prDiff)
                .thenAccept(response::setCodeReviewerResponse);
        CompletableFuture<Void> testGeneratorFuture = testGeneratorAgent.process(context, prDiff)
                .thenAccept(response::setTestGeneratorResponse);

        //Wait for all to finish
        CompletableFuture.allOf(codeReviewerFuture, testGeneratorFuture);
        return response;
    }
}
