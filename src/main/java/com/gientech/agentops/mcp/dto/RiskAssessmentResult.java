package com.gientech.agentops.mcp.dto;

import java.util.List;

/**
 * 风险评估结果类
 */
public class RiskAssessmentResult {
    private boolean success;
    private List<String> issues;
    private List<String> suggestions;

    public RiskAssessmentResult(boolean success, List<String> issues, List<String> suggestions) {
        this.success = success;
        this.issues = issues;
        this.suggestions = suggestions;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public List<String> getIssues() {
        return issues;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public String toString() {
        return "RiskAssessmentResult{" +
                "success=" + success +
                ", issues=" + issues +
                ", suggestions=" + suggestions +
                '}';
    }
}
