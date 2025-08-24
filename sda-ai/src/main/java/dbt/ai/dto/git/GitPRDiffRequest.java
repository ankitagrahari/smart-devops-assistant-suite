package dbt.ai.dto.git;

public record GitPRDiffRequest(
        String prURL,
        String prNumber
) {
}
