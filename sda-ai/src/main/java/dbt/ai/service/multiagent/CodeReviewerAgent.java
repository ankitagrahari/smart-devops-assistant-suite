package dbt.ai.service.multiagent;

import dbt.ai.dto.multiagent.CodeReviewerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class CodeReviewerAgent implements Agents{

    private static final Logger log = LoggerFactory.getLogger(CodeReviewerAgent.class);

    ChatClient chatClient;

    public CodeReviewerAgent(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @Override
    public String getName() {
        return "Code Reviewer Agent";
    }

    @Async
    public CompletableFuture<CodeReviewerResponse> process(String context, String prDiff) {
        String prompt = """
                You are a senior software engineer.
                Given the context: {context}
                And this pull request difference {prDiff},
                Review the code, list any issues or improvements, and rate the code
                quality from 1-10 with 1 being bad and 10 being no issues at all.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(prompt);
        Prompt request = promptTemplate.create(Map.of("context", context, "prDiff", prDiff));

        log.info("Code Reviewer Prompt---------");
        log.info(request.toString());
        log.info("-----------------------------");
        CodeReviewerResponse response = chatClient
                .prompt(request)
                .call()
                .entity(CodeReviewerResponse.class);

        return CompletableFuture.completedFuture(response);
    }
}
