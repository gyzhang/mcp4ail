package com.gientech.agentops.mcp.providers;

import com.gientech.agentops.mcp.dto.ComplianceCheckResult;
import com.gientech.agentops.mcp.dto.ResourceSchedulerResult;
import com.gientech.agentops.mcp.dto.RiskAssessmentResult;
import com.gientech.agentops.mcp.dto.SystemDependencyResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LoanProductPlanProvider {
    private static final Logger logger = LoggerFactory.getLogger(LoanProductPlanProvider.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    private static final Date CONFLICT_END_DATE;
    // 依赖关系图 - 核心系统改造依赖项
    private static final Map<String, List<String>> dependencyGraph = new HashMap<>();


    static {
        dependencyGraph.put("核心系统改造", Arrays.asList("风控模型集成", "渠道接口开发"));
        dependencyGraph.put("征信接口对接", Arrays.asList("反欺诈系统对接"));
        dependencyGraph.put("风控模型集成", Arrays.asList("数据中台对接"));

        try {
            CONFLICT_END_DATE = dateFormat.parse("2023-11-10");
        } catch (ParseException e) {
            throw new RuntimeException("日期格式初始化失败", e);
        }
    }

    /**
     * 合规检查方法
     *
     * @param planStep   需要检查的规划步骤描述
     * @param regulation 适用的监管规定
     * @return 合规检查结果
     */
    @McpTool(description = "检查信贷产品规划是否符合监管要求")
    public ComplianceCheckResult complianceCheck(@McpToolParam(description = "需要检查的规划步骤描述") String planStep,
                                                 @McpToolParam(description = "适用的监管规定") String regulation) {
        // 默认使用小微企业贷款管理办法
        if (regulation == null || regulation.isEmpty()) {
            regulation = "小微企业贷款管理办法";
        }

        // 检查审批时限
        if (planStep != null && planStep.contains("审批时限") &&
                (planStep == null || !planStep.contains("5个工作日"))) {
            List<String> issues = new ArrayList<>();
            issues.add("审批时限不符合《小微企业贷款管理办法》第15条规定");
            return new ComplianceCheckResult(false, issues, "将审批时限调整为不超过5个工作日");
        }

        // 检查利率设置
        if (planStep != null && planStep.contains("利率") &&
                (planStep == null || !planStep.contains("LPR+150BP"))) {
            List<String> issues = new ArrayList<>();
            issues.add("利率设置不符合监管上限要求");
            return new ComplianceCheckResult(false, issues, "确保利率不超过LPR+150BP");
        }

        // 通过合规检查
        return new ComplianceCheckResult(true, new ArrayList<>(), "");
    }

    /**
     * 重载方法 - 使用默认监管规定
     *
     * @param planStep 需要检查的规划步骤描述
     * @return 合规检查结果
     */
    @McpTool(description = "使用检查信贷产品规划是否符合默认监管要求")
    public ComplianceCheckResult defaultComplianceCheck(@McpToolParam(description = "需要检查的规划步骤描述") String planStep) {
        return complianceCheck(planStep, "小微企业贷款管理办法");
    }

    /**
     * 风险评估方法
     *
     * @param productType   产品类型
     * @param targetAmount  目标放款额
     * @return 风险评估结果
     */
    @McpTool(description = "评估信贷产品风险")
    public RiskAssessmentResult riskAssessment(@McpToolParam(description = "产品类型") String productType,
                                               @McpToolParam(description = "目标放款额") double targetAmount) {
        // 检查产品类型是否为"闪电贷"且目标放款额超过3000万
        if ("闪电贷".equals(productType) && targetAmount > 30000000.0) {
            List<String> issues = new ArrayList<>();
            issues.add("目标放款额超过风控模型验证范围");

            List<String> suggestions = new ArrayList<>();
            suggestions.add("建议分阶段实施，首期目标设为3000万");

            return new RiskAssessmentResult(false, issues, suggestions);
        }

        // 通过风险评估
        return new RiskAssessmentResult(true, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * 资源调度检查方法
     *
     * @param team           需要协调的团队
     * @param requiredTasks  需要完成的任务
     * @param startDate      计划开始日期（格式：yyyy-MM-dd）
     * @return 资源调度检查结果
     */
    @McpTool(description = "检查技术资源可用性")
    public ResourceSchedulerResult resourceScheduler(@McpToolParam(description = "需要协调的团队") String team,
                                                     @McpToolParam(description = "需要完成的任务") String requiredTasks,
                                                     @McpToolParam(description = "计划开始日期（格式：yyyy-MM-dd）") String startDate) {
        // 参数验证
        if (team == null || team.isEmpty()) {
            throw new IllegalArgumentException("团队参数不能为空");
        }
        if (requiredTasks == null || requiredTasks.isEmpty()) {
            throw new IllegalArgumentException("任务参数不能为空");
        }
        if (startDate == null || startDate.isEmpty()) {
            throw new IllegalArgumentException("开始日期参数不能为空");
        }

        try {
            // 解析日期
            Date start = dateFormat.parse(startDate);

            // 检查资源冲突
            if ("技术团队".equals(team) &&
                    requiredTasks.contains("系统改造") &&
                    start.before(CONFLICT_END_DATE)) {

                List<String> conflicts = new ArrayList<>();
                conflicts.add("核心系统升级项目占用资源");

                return new ResourceSchedulerResult(false, "2023-11-10", conflicts);
            }

            // 资源可用
            return new ResourceSchedulerResult(true, null, new ArrayList<>());

        } catch (ParseException e) {
            throw new IllegalArgumentException("日期格式错误，应为yyyy-MM-dd格式", e);
        }
    }

    /**
     * 系统依赖分析方法
     *
     * @param systemChanges 计划进行的系统变更列表
     * @return 系统依赖分析结果
     */
    @McpTool(description = "分析系统改造的依赖关系")
    public SystemDependencyResult systemDependency(@McpToolParam(description = "计划进行的系统变更列表") List<String> systemChanges) {
        // 参数验证
        if (systemChanges == null) {
            throw new IllegalArgumentException("系统变更参数不能为空");
        }

        // 计算关键路径
        Set<String> criticalPathSet = new HashSet<>();
        for (String change : systemChanges) {
            if (dependencyGraph.containsKey(change)) {
                criticalPathSet.addAll(dependencyGraph.get(change));
            }
        }

        // 转换为列表
        List<String> criticalPath = new ArrayList<>(criticalPathSet);

        // 检查历史问题
        List<String> historicalIssues = new ArrayList<>();
        if (systemChanges.contains("核心系统改造")) {
            historicalIssues.add("核心系统改造常因表结构调整影响其他模块");
        }

        // 返回结果
        return new SystemDependencyResult(true, criticalPath, historicalIssues);
    }
}
