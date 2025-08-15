package dbt.git.controller;

import dbt.git.dto.ai.PRSuggestionResponse;
import dbt.git.dto.webhook.WebhookRequest;
import dbt.git.service.GitWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/git/webhook")
public class GitWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(GitWebhookController.class);

    GitWebhookService service;

    public GitWebhookController(GitWebhookService service) {
        this.service = service;
    }

    @GetMapping("/echo")
    public ResponseEntity<String> allOK(){
        return ResponseEntity.ok("GitWebhook working...!");
    }

    @PostMapping("/analyzepr")
    public ResponseEntity<PRSuggestionResponse> analyzePR(@RequestBody WebhookRequest request){
        logger.info("request:{}", request);
        return service.analyzePR(request);
    }
}
