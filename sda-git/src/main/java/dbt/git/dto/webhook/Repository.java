package dbt.git.dto.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Repository(
        String name,
        @JsonProperty(value = "full_name")
        String fullName,
        String description,
        @JsonProperty(value = "open_issues")
        Integer openIssues
) {}
