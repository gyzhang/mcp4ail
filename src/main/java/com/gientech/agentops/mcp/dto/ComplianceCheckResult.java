package com.gientech.agentops.mcp.dto;

import java.util.List;

/**
 * 合规检查结果类
 */
public class ComplianceCheckResult {
    private boolean success;
    private List<String> issues;
    private String suggestedFix;

    public ComplianceCheckResult(boolean success, List<String> issues, String suggestedFix) {
        this.success = success;
        this.issues = issues;
        this.suggestedFix = suggestedFix;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public List<String> getIssues() {
        return issues;
    }

    public String getSuggestedFix() {
        return suggestedFix;
    }

    @Override
    public String toString() {
        return "ComplianceCheckResult{" +
                "success=" + success +
                ", issues=" + issues +
                ", suggestedFix='" + suggestedFix + '\'' +
                '}';
    }
}
