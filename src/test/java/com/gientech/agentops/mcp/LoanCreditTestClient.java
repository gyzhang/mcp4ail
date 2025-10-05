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
package com.gientech.agentops.mcp;

import java.util.Map;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;

/**
 * 零售信贷系统MCP工具测试客户端
 * 用于测试5个信贷系统相关的MCP工具
 */
public class LoanCreditTestClient {

    private final McpClientTransport transport;

    public LoanCreditTestClient(McpClientTransport transport) {
        this.transport = transport;
    }

    public void run() {
        // 创建MCP客户端
        var client = McpClient.sync(this.transport)
                .loggingConsumer(message -> {
                    System.out.println(">> Client Logging: " + message);
                })
                .build();

        client.initialize();
        client.ping();
        System.out.println("成功连接到MCP服务器");

        // 列出所有可用的工具
        ListToolsResult toolsList = client.listTools();
        System.out.println("\n===== 可用的MCP工具列表 =====");
        toolsList.tools().stream().forEach(tool -> {
            System.out.println("工具名称: " + tool.name());
            System.out.println("描述: " + tool.description());
            System.out.println("输入参数结构: " + tool.inputSchema());
            System.out.println("------------------------");
        });

        // 测试查询客户的授信额度工具
        testQueryCreditLimit(client);
        
        // 测试查询贷款余额（按产品）工具
        testQueryLoanBalanceByProduct(client);
        
        // 测试查询贷款余额（所有产品）工具
        testQueryLoanBalancesByCustomer(client);
        
        // 测试查询还款计划工具
        testQueryRepaymentPlans(client);
        
        // 测试查询逾期记录工具
        testQueryOverdueRecords(client);

        client.closeGracefully();
        System.out.println("\nMCP客户端已优雅关闭");
    }

    // 测试查询客户的授信额度工具
    private void testQueryCreditLimit(McpSyncClient client) {
        System.out.println("\n===== 测试 'queryCreditLimit' 工具 =====");
        try {
            // 构建请求参数 - 使用data.sql中实际存在的产品名称
            Map<String, Object> params = Map.of(
                "name", "张三",
                "idType", "身份证",
                "idNumber", "110101199001011234",
                "productName", "公积金贷"
            );
            
            // 调用工具
            CallToolRequest request = new CallToolRequest("queryCreditLimit", params);
            CallToolResult result = client.callTool(request);
            
            // 打印结果
            System.out.println("查询客户授信额度结果: " + result.toString());
        } catch (Exception e) {
            System.out.println("查询客户授信额度失败: " + e.getMessage());
        }
    }

    // 测试查询贷款余额（按产品）工具
    private void testQueryLoanBalanceByProduct(McpSyncClient client) {
        System.out.println("\n===== 测试 'queryLoanBalanceByProduct' 工具 =====");
        try {
            // 构建请求参数 - 使用data.sql中实际存在的产品名称
            Map<String, Object> params = Map.of(
                "name", "张三",
                "idType", "身份证",
                "idNumber", "110101199001011234",
                "productName", "公积金贷"
            );
            
            // 调用工具
            CallToolRequest request = new CallToolRequest("queryLoanBalanceByProduct", params);
            CallToolResult result = client.callTool(request);
            
            // 打印结果
            System.out.println("查询贷款余额（按产品）结果: " + result.toString());
        } catch (Exception e) {
            System.out.println("查询贷款余额（按产品）失败: " + e.getMessage());
        }
    }

    // 测试查询贷款余额（所有产品）工具
    private void testQueryLoanBalancesByCustomer(McpSyncClient client) {
        System.out.println("\n===== 测试 'queryLoanBalancesByCustomer' 工具 =====");
        try {
            // 构建请求参数 - 张三在系统中有数据
            Map<String, Object> params = Map.of(
                "name", "张三",
                "idType", "身份证",
                "idNumber", "110101199001011234"
            );
            
            // 调用工具
            CallToolRequest request = new CallToolRequest("queryLoanBalancesByCustomer", params);
            CallToolResult result = client.callTool(request);
            
            // 打印结果
            System.out.println("查询贷款余额（所有产品）结果: " + result.toString());
        } catch (Exception e) {
            System.out.println("查询贷款余额（所有产品）失败: " + e.getMessage());
        }
    }

    // 测试查询还款计划工具
    private void testQueryRepaymentPlans(McpSyncClient client) {
        System.out.println("\n===== 测试 'queryRepaymentPlans' 工具 =====");
        try {
            // 构建请求参数 - 使用data.sql中实际存在的产品名称
            Map<String, Object> params = Map.of(
                "name", "张三",
                "idType", "身份证",
                "idNumber", "110101199001011234",
                "productName", "公积金贷"
            );
            
            // 调用工具
            CallToolRequest request = new CallToolRequest("queryRepaymentPlans", params);
            CallToolResult result = client.callTool(request);
            
            // 打印结果
            System.out.println("查询还款计划结果: " + result.toString());
        } catch (Exception e) {
            System.out.println("查询还款计划失败: " + e.getMessage());
        }
    }

    // 测试查询逾期记录工具
    private void testQueryOverdueRecords(McpSyncClient client) {
        System.out.println("\n===== 测试 'queryOverdueRecords' 工具 =====");
        try {
            // 构建请求参数 - 李四有逾期记录，使用消费贷产品
            Map<String, Object> params = Map.of(
                "name", "李四",
                "idType", "身份证",
                "idNumber", "110101199102022345",
                "productName", "消费贷"
            );
            
            // 调用工具
            CallToolRequest request = new CallToolRequest("queryOverdueRecords", params);
            CallToolResult result = client.callTool(request);
            
            // 打印结果
            System.out.println("查询逾期记录结果: " + result.toString());
        } catch (Exception e) {
            System.out.println("查询逾期记录失败: " + e.getMessage());
        }
    }
}
