package dbt.git.config;


import dbt.git.dto.GitSourceMetaDataDetailsDTO;
import dbt.git.entites.GitSourceMetaData;
import dbt.git.mapper.GitSourceMetaDataMapper;
import dbt.git.repo.GitFileMetaDataRepository;
import dbt.git.service.GitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    GitService gitService;
    GitFileMetaDataRepository gitFileMetaDataRepository;

    @Value("${git.branch.name:master}")
    String GIT_BRANCH_NAME;

    public DataInitializer(GitService gitService,
                           GitFileMetaDataRepository gitFileMetaDataRepository) {
        this.gitService = gitService;
        this.gitFileMetaDataRepository = gitFileMetaDataRepository;    }

    /**
     * It loads all the files of a project from Github to the configured Vector Store.
     * 1. Fetch the SHA for the configured branch (default: master)
     *      API: https://api.github.com/repos/{{owner}}/{{repo}}/branches/{{branch_name}}
     * 2. Fetch all the files of type: BLOB (no folders)
     *      API: https://api.github.com/repos/{{owner}}/{{repo}}/git/trees/{{sha}}?recursive=1
     * 3. Save file path and some metadata for all entities to the database.
     */
    void loadData() {
        log.info("Initial data load started...");
        String sha = gitService.fetchBranchSHA(GIT_BRANCH_NAME);
        List<GitSourceMetaDataDetailsDTO> gitFileMetaDataList = gitService.fetchGitFileMetaData(sha);
        gitFileMetaDataRepository.saveAll(GitSourceMetaDataMapper.toEntities(gitFileMetaDataList));
        log.info("Initial data load completed!");

        log.info("Listing the entities saved in H2 DB");
        List<GitSourceMetaData> list = gitFileMetaDataRepository.findAll();
        list.forEach(dto -> log.info(dto.toString()));
        log.info("-------------------------------------");
    }

    @Override
    public void run(String... args) {
        loadData();
    }
}
