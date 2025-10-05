package com.gientech.agentops.mcp.dto;

import java.util.List;

/**
 * 资源调度结果类
 */
public class ResourceSchedulerResult {
    private boolean success;
    private String availableDate;
    private List<String> conflicts;

    public ResourceSchedulerResult(boolean success, String availableDate, List<String> conflicts) {
        this.success = success;
        this.availableDate = availableDate;
        this.conflicts = conflicts;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public List<String> getConflicts() {
        return conflicts;
    }

    @Override
    public String toString() {
        return "ResourceSchedulerResult{" +
                "success=" + success +
                ", availableDate='" + availableDate + '\'' +
                ", conflicts=" + conflicts +
                '}';
    }
}
