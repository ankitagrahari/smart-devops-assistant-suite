package dbt.ai.dto.multiagent;

import java.util.List;

public class TestGeneratorResponse {

    private String summary;
    private List<String> affectedClasses;
    private List<TestCase> testCases;

    public TestGeneratorResponse(String summary, List<String> affectedClasses, List<TestCase> testCases) {
        this.summary = summary;
        this.affectedClasses = affectedClasses;
        this.testCases = testCases;
    }

    @Override
    public String toString() {
        return "TestGeneratorResponse{" +
                "summary='" + summary + '\'' +
                ", affectedClasses=" + affectedClasses +
                ", testCases=" + testCases +
                '}';
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getAffectedClasses() {
        return affectedClasses;
    }

    public void setAffectedClasses(List<String> affectedClasses) {
        this.affectedClasses = affectedClasses;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }
}
