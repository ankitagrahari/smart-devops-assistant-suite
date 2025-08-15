package dbt.git.dto;

public record GitSourceMetaDataDetailsDTO(
        String path,
        String type,
        String sha,
        int size,
        String url
) {
}
