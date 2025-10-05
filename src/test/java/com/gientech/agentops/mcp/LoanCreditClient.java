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

import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;

/**
 * 零售信贷系统MCP客户端主类
 * 用于启动LoanCreditTestClient测试5个信贷系统相关的MCP工具
 */
public class LoanCreditClient {

    public static void main(String[] args) {
        // 创建HTTP客户端传输对象，连接到本地运行的MCP服务器
        HttpClientStreamableHttpTransport transport = HttpClientStreamableHttpTransport.builder("http://localhost:9081")
                .build();

        // 创建并运行LoanCreditTestClient，测试所有5个信贷系统工具
        new LoanCreditTestClient(transport).run();                
    }

}
