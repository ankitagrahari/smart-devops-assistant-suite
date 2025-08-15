package dbt.git.dto.ai;

import java.util.List;

public record AnalyzePRRequest(
        String prDiff,
        List<String> fileNames
) {
}
