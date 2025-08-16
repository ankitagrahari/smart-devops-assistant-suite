package dbt.ai.dto.multiagent;

public class MultiAgentResponse {

    private String error;
    private Integer statusCode;
    private CodeReviewerResponse codeReviewerResponse;
    private TestGeneratorResponse testGeneratorResponse;

    public MultiAgentResponse() {
    }

    public MultiAgentResponse(String error,
                              Integer statusCode,
                              CodeReviewerResponse codeReviewerResponse,
                              TestGeneratorResponse testGeneratorResponse) {
        this.error = error;
        this.statusCode = statusCode;
        this.codeReviewerResponse = codeReviewerResponse;
        this.testGeneratorResponse = testGeneratorResponse;
    }

    @Override
    public String toString() {
        return "MultiAgentResponse{" +
                "error='" + error + '\'' +
                ", statusCode=" + statusCode +
                ", codeReviewerResponse=" + codeReviewerResponse +
                ", testGeneratorResponse=" + testGeneratorResponse +
                '}';
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public CodeReviewerResponse getCodeReviewerResponse() {
        return codeReviewerResponse;
    }

    public void setCodeReviewerResponse(CodeReviewerResponse codeReviewerResponse) {
        this.codeReviewerResponse = codeReviewerResponse;
    }

    public TestGeneratorResponse getTestGeneratorResponse() {
        return testGeneratorResponse;
    }

    public void setTestGeneratorResponse(TestGeneratorResponse testGeneratorResponse) {
        this.testGeneratorResponse = testGeneratorResponse;
    }
}
