package dbt.git.feignclient;

import dbt.git.dto.GitChangedFile;
import dbt.git.dto.ai.AnalyzePRRequest;
import dbt.git.dto.ai.PRSuggestionResponse;
import dbt.git.dto.ai.PRSummaryRequest;
import dbt.git.dto.ai.PRSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("ai-service")
public interface AIClient {

    @PostMapping("/ai/prsummary")
    ResponseEntity<PRSummaryResponse> prSummary(@RequestBody PRSummaryRequest request);

    @PostMapping("/ai/pranalyze")
    PRSuggestionResponse analyzePR(@RequestBody AnalyzePRRequest request);

    @PostMapping("/ai/vectorstore")
    void addToVectorStore(@RequestBody List<GitChangedFile> gitChangedFiles);
}
