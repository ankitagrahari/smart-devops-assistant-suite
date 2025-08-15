package dbt.git.repo;

import dbt.git.entites.GitSourceMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitFileMetaDataRepository extends JpaRepository<GitSourceMetaData, Long> {

    GitSourceMetaData findByPathAndType(String path, String type);
}
