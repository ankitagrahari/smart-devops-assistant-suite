package dbt.ai.service;

import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChromaVSService {

    @Value("${spring.ai.vectorstore.chroma.collection-name}")
    private String CHROMA_COLLECTION;
    ChromaVectorStore chromaVectorStore;

    public ChromaVSService(ChromaVectorStore chromaVectorStore) {
        this.chromaVectorStore = chromaVectorStore;
    }

    public List<Document> fetchDocumentsByQuery(String query, String filterExpression){
        return chromaVectorStore.doSimilaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .filterExpression(filterExpression)
                        .similarityThreshold(0.8)
                        .build());
    }

    public void deleteCollection(){
        Optional<ChromaApi> chromaApiOp = chromaVectorStore.getNativeClient();
        chromaApiOp.ifPresent(
                chromaApi -> chromaApi
                        .deleteCollection("SpringAiTenant", "SpringAiDatabase", CHROMA_COLLECTION));
    }
}
