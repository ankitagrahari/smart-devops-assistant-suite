package dbt.git.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitFileContent(
        String sha,
        @JsonProperty("node_id")
        String nodeId,
        String url,
        String content,
        String encoding,
        Long size
) {
}
