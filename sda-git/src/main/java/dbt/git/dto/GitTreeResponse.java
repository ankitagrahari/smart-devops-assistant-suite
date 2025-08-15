package dbt.git.dto;

import java.util.List;

public record GitTreeResponse(
        String sha,
        String url,
        List<GitSourceMetaDataDetailsDTO> tree,
        boolean truncated
) {
}
