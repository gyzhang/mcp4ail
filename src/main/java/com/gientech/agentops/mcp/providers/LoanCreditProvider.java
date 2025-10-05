/*
 * MIT License
 *
 * Copyright (c) 2025 com.gientech.agentops
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.gientech.agentops.mcp.providers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;

import com.gientech.agentops.mcp.entity.Customer;
import com.gientech.agentops.mcp.entity.CustomerCredit;
import com.gientech.agentops.mcp.entity.LoanContract;
import com.gientech.agentops.mcp.entity.LoanProduct;
import com.gientech.agentops.mcp.entity.OverdueRecord;
import com.gientech.agentops.mcp.entity.RepaymentPlan;
import com.gientech.agentops.mcp.service.CustomerCreditService;
import com.gientech.agentops.mcp.service.CustomerService;
import com.gientech.agentops.mcp.service.LoanContractService;
import com.gientech.agentops.mcp.service.LoanProductService;
import com.gientech.agentops.mcp.service.OverdueRecordService;
import com.gientech.agentops.mcp.service.RepaymentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 零售信贷系统的MCP工具提供者
 * 提供客户授信、贷款余额、还款计划和逾期记录等查询功能
 */
@Service
public class LoanCreditProvider {

    private static final Logger logger = LoggerFactory.getLogger(LoanCreditProvider.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LoanProductService loanProductService;

    @Autowired
    private CustomerCreditService customerCreditService;

    @Autowired
    private LoanContractService loanContractService;

    @Autowired
    private RepaymentPlanService repaymentPlanService;

    @Autowired
    private OverdueRecordService overdueRecordService;

    /**
     * 查询客户的授信额度
     * @param name 客户姓名
     * @param idType 证件类型
     * @param idNumber 证件号码
     * @param productName 贷款品种名称
     * @return 授信额度，单位元，精确到小数点后2位
     */
    @McpTool(description = "查询客户的授信额度")
    public Map<String, Object> queryCreditLimit(
            @McpToolParam(description = "客户姓名") String name,
            @McpToolParam(description = "证件类型") String idType,
            @McpToolParam(description = "证件号码") String idNumber,
            @McpToolParam(description = "贷款品种名称") String productName) {
        
        try {
            // 查询客户
            Customer customer = customerService.lambdaQuery()
                    .eq(Customer::getName, name)
                    .eq(Customer::getIdType, idType)
                    .eq(Customer::getIdNumber, idNumber)
                    .one();
            
            if (customer == null) {
                return createErrorResponse("客户不存在");
            }
            
            // 查询贷款产品
            LoanProduct product = loanProductService.lambdaQuery()
                    .eq(LoanProduct::getProductName, productName)
                    .one();
            
            if (product == null) {
                return createErrorResponse("贷款产品不存在");
            }
            
            // 查询客户授信
            CustomerCredit credit = customerCreditService.lambdaQuery()
                    .eq(CustomerCredit::getCustomerId, customer.getId())
                    .eq(CustomerCredit::getProductId, product.getId())
                    .one();
            
            if (credit == null) {
                return createErrorResponse("客户未获得该产品的授信");
            }
            
            // 格式化返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("customerName", name);
            result.put("productName", productName);
            result.put("creditLimit", formatAmount(credit.getCreditLimit()));
            result.put("availableLimit", formatAmount(credit.getAvailableLimit()));
            
            logger.info("查询客户[{}]的[{}]授信额度成功", name, productName);
            return result;
            
        } catch (Exception e) {
            logger.error("查询客户授信额度失败", e);
            return createErrorResponse("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询客户的贷款余额（按产品）
     * @param name 客户姓名
     * @param idType 证件类型
     * @param idNumber 证件号码
     * @param productName 贷款品种名称
     * @return 贷款余额，单位元，精确到小数点后2位
     */
    @McpTool(description = "查询客户的贷款余额（按产品）")
    public Map<String, Object> queryLoanBalanceByProduct(
            @McpToolParam(description = "客户姓名") String name,
            @McpToolParam(description = "证件类型") String idType,
            @McpToolParam(description = "证件号码") String idNumber,
            @McpToolParam(description = "贷款品种名称") String productName) {
        
        try {
            // 查询客户
            Customer customer = customerService.lambdaQuery()
                    .eq(Customer::getName, name)
                    .eq(Customer::getIdType, idType)
                    .eq(Customer::getIdNumber, idNumber)
                    .one();
            
            if (customer == null) {
                return createErrorResponse("客户不存在");
            }
            
            // 查询贷款产品
            LoanProduct product = loanProductService.lambdaQuery()
                    .eq(LoanProduct::getProductName, productName)
                    .one();
            
            if (product == null) {
                return createErrorResponse("贷款产品不存在");
            }
            
            // 查询贷款合同
            List<LoanContract> contracts = loanContractService.lambdaQuery()
                    .eq(LoanContract::getCustomerId, customer.getId())
                    .eq(LoanContract::getProductId, product.getId())
                    .eq(LoanContract::getStatus, "ACTIVE")
                    .list();
            
            if (contracts.isEmpty()) {
                return createErrorResponse("客户在该产品下没有贷款合同");
            }
            
            // 计算总贷款余额
            BigDecimal totalBalance = BigDecimal.ZERO;
            for (LoanContract contract : contracts) {
                totalBalance = totalBalance.add(contract.getLoanBalance());
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("customerName", name);
            result.put("productName", productName);
            result.put("loanBalance", formatAmount(totalBalance));
            result.put("contractCount", contracts.size());
            
            logger.info("查询客户[{}]的[{}]贷款余额成功", name, productName);
            return result;
            
        } catch (Exception e) {
            logger.error("查询贷款余额失败", e);
            return createErrorResponse("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询客户的贷款余额（所有产品）
     * @param name 客户姓名
     * @param idType 证件类型
     * @param idNumber 证件号码
     * @return 各贷款品种的贷款余额列表
     */
    @McpTool(description = "查询客户的贷款余额（所有产品）")
    public Map<String, Object> queryLoanBalancesByCustomer(
            @McpToolParam(description = "客户姓名") String name,
            @McpToolParam(description = "证件类型") String idType,
            @McpToolParam(description = "证件号码") String idNumber) {
        
        try {
            // 查询客户
            Customer customer = customerService.lambdaQuery()
                    .eq(Customer::getName, name)
                    .eq(Customer::getIdType, idType)
                    .eq(Customer::getIdNumber, idNumber)
                    .one();
            
            if (customer == null) {
                return createErrorResponse("客户不存在");
            }
            
            // 查询所有贷款合同
            List<LoanContract> contracts = loanContractService.lambdaQuery()
                    .eq(LoanContract::getCustomerId, customer.getId())
                    .eq(LoanContract::getStatus, "ACTIVE")
                    .list();
            
            if (contracts.isEmpty()) {
                return createErrorResponse("客户没有任何贷款合同");
            }
            
            // 按产品分组计算余额
            Map<String, BigDecimal> productBalances = new HashMap<>();
            for (LoanContract contract : contracts) {
                LoanProduct product = loanProductService.getById(contract.getProductId());
                if (product != null) {
                    String productName = product.getProductName();
                    BigDecimal balance = contract.getLoanBalance();
                    productBalances.put(productName, productBalances.getOrDefault(productName, BigDecimal.ZERO).add(balance));
                }
            }
            
            // 格式化返回结果
            List<Map<String, Object>> balanceList = new ArrayList<>();
            BigDecimal totalBalance = BigDecimal.ZERO;
            for (Map.Entry<String, BigDecimal> entry : productBalances.entrySet()) {
                Map<String, Object> balanceInfo = new HashMap<>();
                balanceInfo.put("productName", entry.getKey());
                balanceInfo.put("loanBalance", formatAmount(entry.getValue()));
                balanceList.add(balanceInfo);
                totalBalance = totalBalance.add(entry.getValue());
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("customerName", name);
            result.put("totalLoanBalance", formatAmount(totalBalance));
            result.put("productBalances", balanceList);
            
            logger.info("查询客户[{}]的所有贷款余额成功", name);
            return result;
            
        } catch (Exception e) {
            logger.error("查询贷款余额列表失败", e);
            return createErrorResponse("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询客户的还款计划
     * @param name 客户姓名
     * @param idType 证件类型
     * @param idNumber 证件号码
     * @param productName 贷款品种名称
     * @return 今年的还款计划列表
     */
    @McpTool(description = "查询客户的还款计划")
    public Map<String, Object> queryRepaymentPlans(
            @McpToolParam(description = "客户姓名") String name,
            @McpToolParam(description = "证件类型") String idType,
            @McpToolParam(description = "证件号码") String idNumber,
            @McpToolParam(description = "贷款品种名称") String productName) {
        
        try {
            // 查询客户
            Customer customer = customerService.lambdaQuery()
                    .eq(Customer::getName, name)
                    .eq(Customer::getIdType, idType)
                    .eq(Customer::getIdNumber, idNumber)
                    .one();
            
            if (customer == null) {
                return createErrorResponse("客户不存在");
            }
            
            // 查询贷款产品
            LoanProduct product = loanProductService.lambdaQuery()
                    .eq(LoanProduct::getProductName, productName)
                    .one();
            
            if (product == null) {
                return createErrorResponse("贷款产品不存在");
            }
            
            // 查询贷款合同
            List<LoanContract> contracts = loanContractService.lambdaQuery()
                    .eq(LoanContract::getCustomerId, customer.getId())
                    .eq(LoanContract::getProductId, product.getId())
                    .eq(LoanContract::getStatus, "ACTIVE")
                    .list();
            
            if (contracts.isEmpty()) {
                return createErrorResponse("客户在该产品下没有贷款合同");
            }
            
            // 获取当前年份
            String currentYear = String.valueOf(Year.now().getValue());
            
            // 查询今年的还款计划
            List<Map<String, Object>> repaymentPlans = new ArrayList<>();
            for (LoanContract contract : contracts) {
                List<RepaymentPlan> plans = repaymentPlanService.lambdaQuery()
                        .eq(RepaymentPlan::getContractId, contract.getId())
                        .like(RepaymentPlan::getRepaymentDate, currentYear)
                        .list();
                
                for (RepaymentPlan plan : plans) {
                    Map<String, Object> planInfo = new HashMap<>();
                    planInfo.put("repaymentDate", plan.getRepaymentDate());
                    planInfo.put("repaymentAmount", formatAmount(plan.getRepaymentAmount()));
                    planInfo.put("interestAmount", formatAmount(plan.getInterestAmount()));
                    planInfo.put("principalAmount", formatAmount(plan.getPrincipalAmount()));
                    planInfo.put("remainingBalance", formatAmount(plan.getRemainingBalance()));
                    planInfo.put("status", plan.getStatus());
                    repaymentPlans.add(planInfo);
                }
            }
            
            if (repaymentPlans.isEmpty()) {
                return createErrorResponse("今年没有还款计划");
            }
            
            // 按日期排序
            repaymentPlans.sort((a, b) -> a.get("repaymentDate").toString().compareTo(b.get("repaymentDate").toString()));
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("customerName", name);
            result.put("productName", productName);
            result.put("year", currentYear);
            result.put("repaymentPlans", repaymentPlans);
            
            logger.info("查询客户[{}]的[{}]还款计划成功", name, productName);
            return result;
            
        } catch (Exception e) {
            logger.error("查询还款计划失败", e);
            return createErrorResponse("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询客户的逾期记录
     * @param name 客户姓名
     * @param idType 证件类型
     * @param idNumber 证件号码
     * @param productName 贷款品种名称
     * @return 该贷款品种的逾期记录
     */
    @McpTool(description = "查询客户的逾期记录")
    public Map<String, Object> queryOverdueRecords(
            @McpToolParam(description = "客户姓名") String name,
            @McpToolParam(description = "证件类型") String idType,
            @McpToolParam(description = "证件号码") String idNumber,
            @McpToolParam(description = "贷款品种名称") String productName) {
        
        try {
            // 查询客户
            Customer customer = customerService.lambdaQuery()
                    .eq(Customer::getName, name)
                    .eq(Customer::getIdType, idType)
                    .eq(Customer::getIdNumber, idNumber)
                    .one();
            
            if (customer == null) {
                return createErrorResponse("客户不存在");
            }
            
            // 查询贷款产品
            LoanProduct product = loanProductService.lambdaQuery()
                    .eq(LoanProduct::getProductName, productName)
                    .one();
            
            if (product == null) {
                return createErrorResponse("贷款产品不存在");
            }
            
            // 查询贷款合同
            List<LoanContract> contracts = loanContractService.lambdaQuery()
                    .eq(LoanContract::getCustomerId, customer.getId())
                    .eq(LoanContract::getProductId, product.getId())
                    .list();
            
            if (contracts.isEmpty()) {
                return createErrorResponse("客户在该产品下没有贷款合同");
            }
            
            // 查询逾期记录
            List<Map<String, Object>> overdueRecords = new ArrayList<>();
            for (LoanContract contract : contracts) {
                List<OverdueRecord> records = overdueRecordService.lambdaQuery()
                        .eq(OverdueRecord::getContractId, contract.getId())
                        .list();
                
                for (OverdueRecord record : records) {
                    // 只返回有逾期金额的记录
                    if (record.getOverdueAmount().compareTo(BigDecimal.ZERO) > 0) {
                        Map<String, Object> recordInfo = new HashMap<>();
                        recordInfo.put("overdueDate", record.getOverdueDate());
                        recordInfo.put("dueAmount", formatAmount(record.getDueAmount()));
                        recordInfo.put("paidAmount", formatAmount(record.getPaidAmount()));
                        recordInfo.put("overdueAmount", formatAmount(record.getOverdueAmount()));
                        recordInfo.put("penaltyAmount", formatAmount(record.getPenaltyAmount()));
                        overdueRecords.add(recordInfo);
                    }
                }
            }
            
            // 按日期排序
            overdueRecords.sort((a, b) -> a.get("overdueDate").toString().compareTo(b.get("overdueDate").toString()));
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("customerName", name);
            result.put("productName", productName);
            result.put("overdueRecords", overdueRecords);
            result.put("overdueCount", overdueRecords.size());
            
            logger.info("查询客户[{}]的[{}]逾期记录成功", name, productName);
            return result;
            
        } catch (Exception e) {
            logger.error("查询逾期记录失败", e);
            return createErrorResponse("查询失败：" + e.getMessage());
        }
    }

    // 辅助方法：格式化金额为保留2位小数的字符串
    private String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP)
                .toString();
    }

    // 辅助方法：创建错误响应
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        return result;
    }
}
