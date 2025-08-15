package dbt.ai.controller;

import dbt.ai.dto.*;
import dbt.ai.service.AIService;
import dbt.ai.service.SDAVectorStoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
public class  AIController {

    private final AIService aiService;
    private final SDAVectorStoreService sdaVectorStoreService;

    public AIController(AIService aiService,
                        SDAVectorStoreService sdaVectorStoreService) {
        this.aiService = aiService;
        this.sdaVectorStoreService = sdaVectorStoreService;
    }

    @GetMapping("/echo")
    public ResponseEntity<String> allOk(){
        return ResponseEntity.ok("AI working...!");
    }

    @PostMapping("/pr/summary")
    public ResponseEntity<PRSummaryResponse> generateSummary(@RequestBody PRSummaryRequest prSummaryRequest){
        return aiService.generateSummary(prSummaryRequest);
    }

    @PostMapping("/pr/analyze")
    public PRSuggestionResponse analyzePR(@RequestBody AnalyzePRRequest request){
        return aiService.analyzePR(request.prDiff(), request.fileNames());
    }

    @PostMapping("/vectorstore")
    public void storeDataToVectorStore(@RequestBody List<GitChangedFile> gitChangedFileList){
        sdaVectorStoreService.populateVectorStore(gitChangedFileList);
    }
}
