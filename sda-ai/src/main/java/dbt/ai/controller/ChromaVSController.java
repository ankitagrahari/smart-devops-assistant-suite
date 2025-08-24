package dbt.ai.controller;

import dbt.ai.service.ChromaVSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai/chroma")
public class ChromaVSController {

    private static final Logger log = LoggerFactory.getLogger(ChromaVSController.class);
    ChromaVSService chromaService;

    public ChromaVSController(ChromaVSService chromaService) {
        this.chromaService = chromaService;
    }

    @GetMapping
    public String allOk(){
        return "Chroma API working...";
    }

    @GetMapping("/data")
    public List<Document> fetchDataByQuery(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "expression", required = false) String expression){
        log.info("Query {}, expression: {}", query, expression);

        return chromaService.fetchDocumentsByQuery(query, expression);
    }

    @DeleteMapping("/deleteCollection")
    public void deleteCollection(){
        chromaService.deleteCollection();
    }
}
