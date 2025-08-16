package dbt.ai.service.multiagent;

import dbt.ai.dto.multiagent.MultiAgentResponse;
import dbt.ai.dto.multiagent.TestGeneratorResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class TestGeneratorAgent implements Agents{

    ChatClient chatClient;

    public TestGeneratorAgent(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @Override
    public String getName() {
        return "Test Generator Agent";
    }

    @Async
    public CompletableFuture<TestGeneratorResponse> process(String context, String prDiff) {
        String prompt = """
            You are a senior Java test engineer. You will be given:
            1. Context about the project
            2. The pull request (PR) code diff
            
            Your task:
            - Identify the classes/methods changed in this PR.
            - Propose new unit tests that validate the new or modified logic.
            - Use JUnit 5 and Mockito syntax in Java.
            - Keep the code compilable and runnable without external dependencies (other than JUnit/Mockito).
            - Cover both positive and negative cases.
            - Each test case must include a brief description.
            - You MUST output only valid JSON (UTF-8), without extra commentary or markdown.
            
            JSON format to return:
            {
              "summary": "string - short description of what was tested",
              "affectedClasses": ["ClassName1", "ClassName2"],
              "testCases": [
                {
                  "className": "string - name of the test class",
                  "description": "string - what the test verifies",
                  "code": "string - Java test code, escaped for JSON"
                }
              ]
            }
            
            Context:
            {context}
            
            Pull Request Diff:
            {prDiff}
            
            Remember:
            - DO NOT wrap the JSON in code fences.
            - Ensure that the code field escapes backslashes and double quotes properly.
            - Output nothing except the JSON.
            """;

        PromptTemplate promptTemplate = new PromptTemplate(prompt);
        Prompt request = promptTemplate.create(Map.of("context", context, "prDiff", prDiff));
        TestGeneratorResponse response = chatClient
                .prompt(request)
                .call()
                .entity(TestGeneratorResponse.class);

        return CompletableFuture.completedFuture(response);
    }
}
