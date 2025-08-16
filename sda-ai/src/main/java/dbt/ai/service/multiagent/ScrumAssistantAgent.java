package dbt.ai.service.multiagent;

import dbt.ai.dto.multiagent.MultiAgentResponse;

/**
 * This agent should take PR summaries, commit messages, or task updates and turn them into concise,
 * structured Scrum-ready notes (like what a developer would say in daily standups).
 */
public class ScrumAssistantAgent implements Agents{

    @Override
    public String getName() {
        return "SCRUM Assistant Agent";
    }

    public MultiAgentResponse process(String context, String prDiff) {

        context = """
                Smart DevOps Assistant is an AI-powered tool that helps developers by:
                1. Analyzing GitHub pull requests for code quality and impact.
                2. Generating summaries of code changes for documentation and reviews.
                3. Creating unit test cases automatically using JUnit and Mockito.
                4. Producing daily Scrum-style updates from project commits and PRs.
                The project is built using Java, Spring Boot, Spring AI, and integrates with Chroma vector DB and GitHub APIs.
                """;
        String prompt = """
            You are acting as a Scrum Assistant for a software development team.
            Your role is to take project updates (pull request summaries, commit messages, or task updates) 
            and generate a daily standup-style report that is:
            - Clear and concise
            - Written in plain English (developer-friendly, non-technical stakeholders should also understand)
            - Structured in Scrum format: What was done, what will be done, and blockers if any.
            
            Your response MUST be in strict JSON format (UTF-8, no markdown, no commentary) with the following structure:
            
            {
              "yesterday": [
                "string - tasks completed yesterday, each as a separate entry"
              ],
              "today": [
                "string - tasks planned for today, each as a separate entry"
              ],
              "blockers": [
                "string - blockers or risks, if none then return []"
              ]
            }
            
            Context (Project Info):
            {context}
            
            Updates (from commits/PRs/issues):
            {updates}
            
            Remember:
            - If some section has no updates, return an empty array [].
            - Output only valid JSON, without extra text or markdown.
            """;
        return null;
    }
}
