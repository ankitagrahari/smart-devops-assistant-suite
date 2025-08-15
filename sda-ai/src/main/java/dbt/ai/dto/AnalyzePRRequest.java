package dbt.ai.dto;

import java.util.List;

public record AnalyzePRRequest(
        String prDiff,
        List<String> fileNames
) {
}
