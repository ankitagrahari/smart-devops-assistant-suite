package dbt.git.dto.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WebhookRequest (
    String action,
    Integer number,
    @JsonProperty(value="pull_request")
    PullRequest pullRequest,
    Repository repository
){}
