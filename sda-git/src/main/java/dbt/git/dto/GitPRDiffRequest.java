package dbt.git.dto;

public record GitPRDiffRequest(
        String prURL,
        String prNumber
) {
}
