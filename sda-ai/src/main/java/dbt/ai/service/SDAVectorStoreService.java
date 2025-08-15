package dbt.ai.service;

import dbt.ai.dto.GitChangedFile;
import dbt.ai.feignclient.GitClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class SDAVectorStoreService {

    private static final Logger logger = LoggerFactory.getLogger(SDAVectorStoreService.class);

    private static final String GIT_FILE_STATUS_REMOVED = "REMOVED";
    private final GitClient gitClient;
    private final FileService fileService;
    private final VectorStore vectorStore;

    public SDAVectorStoreService(GitClient gitClient,
                                 FileService fileService,
                                 VectorStore vectorStore) {
        this.gitClient = gitClient;
        this.fileService = fileService;
        this.vectorStore = vectorStore;
    }

    public void populateVectorStore(List<GitChangedFile> changedFiles){
        logger.info("populating git files content to vector store!");
        changedFiles.stream()
                .filter(SDAVectorStoreService::filterOnRemovedStatus)
                .filter(SDAVectorStoreService::filterOnFileType)
                .map(gitClient::getGitFileMetaDataDTO)
                .map(gitClient::fetchChangedGitFileContent)
                .forEach(this::addToVectorStore);

        logger.info("Incoming files from git PR {} files", changedFiles.size());
    }

    private static boolean filterOnFileType(GitChangedFile obj) {
        return obj.filename().endsWith(".java");
    }

    private static boolean filterOnRemovedStatus(GitChangedFile gitChangedFile) {
        return !gitChangedFile.status().equals(GIT_FILE_STATUS_REMOVED);
    }


    private void addToVectorStore(Pair<String, String> contentFilePair){

        String path = contentFilePair.getFirst();
        String content = contentFilePair.getSecond();

        Path tempFilePath = null;
        try {
            tempFilePath = fileService.getTempFilePath(content);

            Resource resource = new FileSystemResource(tempFilePath);
            DocumentReader reader = new TikaDocumentReader(resource);
            TextSplitter splitter = new TokenTextSplitter();
            List<Document> documents = splitter.apply(reader.get());

            documents.forEach(document -> {
                Map<String, Object> metaData = document.getMetadata();
                metaData.put("path", path);
            });

            vectorStore.add(documents);
        } catch (IOException e) {
            logger.error("Error creating temporary file {}", e.getMessage());
        } finally {
            if(tempFilePath!=null){
                try {
                    Files.deleteIfExists(tempFilePath);
                } catch (IOException e) {
                    logger.error("Error deleting the temporary file {}", e.getMessage());
                }
            }
        }
    }

}
