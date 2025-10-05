package com.gientech.agentops.mcp.dto;

import java.util.List;

/**
 * 系统依赖分析结果类
 */
public class SystemDependencyResult {
    private boolean success;
    private List<String> criticalPath;
    private List<String> historicalIssues;

    public SystemDependencyResult(boolean success, List<String> criticalPath, List<String> historicalIssues) {
        this.success = success;
        this.criticalPath = criticalPath;
        this.historicalIssues = historicalIssues;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public List<String> getCriticalPath() {
        return criticalPath;
    }

    public List<String> getHistoricalIssues() {
        return historicalIssues;
    }

    // toString for debugging
    @Override
    public String toString() {
        return "SystemDependencyResult{" +
                "success=" + success +
                ", criticalPath=" + criticalPath +
                ", historicalIssues=" + historicalIssues +
                '}';
    }
}