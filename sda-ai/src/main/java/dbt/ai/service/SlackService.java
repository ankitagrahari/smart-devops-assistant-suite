package dbt.ai.service;

import dbt.ai.dto.PRSummaryResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SlackService {

    private final String SLACK_WEBHOOK_INCOMING_URL;

    private final RestTemplate restTemplate;

    public SlackService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        SLACK_WEBHOOK_INCOMING_URL = System.getenv("SLACK_WEBHOOK_INCOMING_URL");
    }

    public void sendPRReviewToSlack(PRSummaryResponse prSummaryResponse){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String payload = "{\"text\":\""+prSummaryResponse.summary()+"\"}";
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(SLACK_WEBHOOK_INCOMING_URL, entity, String.class);
    }
}
