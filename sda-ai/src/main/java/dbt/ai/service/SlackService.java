package dbt.ai.service;

import dbt.ai.dto.PRSummaryResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SlackService {

    private final String SLACK_WEBHOOK_INCOMING_URL;

    private final RestClient restClient;

    public SlackService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
        SLACK_WEBHOOK_INCOMING_URL = System.getenv("SLACK_WEBHOOK_INCOMING_URL");
    }

    public void sendPRReviewToSlack(PRSummaryResponse prSummaryResponse) {
        String payload = "{\"text\":\"" + prSummaryResponse.summary() + "\"}";

        restClient.post()
                .uri(SLACK_WEBHOOK_INCOMING_URL)
                .body(payload)
                .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
                .retrieve()
                .toEntity(String.class);
    }

}
