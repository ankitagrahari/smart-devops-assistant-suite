package dbt.ai.dto.multiagent;

import java.util.List;
import java.util.Map;

public class CodeReviewerResponse {

    private String summary;
    private List<String> suggestions;
    private Map<String, Object> metadata;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "MultiAgentResponse{" +
                "summary='" + summary + '\'' +
                ", suggestions=" + suggestions +
                ", metadata=" + metadata +
                '}';
    }
}
