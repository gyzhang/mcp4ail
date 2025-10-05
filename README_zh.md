# MCP4AIL - 演示MCP Server如何开发的系统

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)![Java Version](https://img.shields.io/badge/Java-17-blue)![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen)![H2 Database](https://img.shields.io/badge/Database-H2-orange)

一个基于Spring AI MCP（模型上下文协议）服务器构建的贷款信用管理系统。该系统为金融机构提供智能化的信贷管理解决方案，用于贷款处理和客户数据管理。

## 概述

MCP4AIL是一款基于前沿Spring技术构建的金融贷款信用管理系统。它既展示了Spring AI MCP的功能，又是一个功能完整的信用管理平台。该应用特性包括：

- **MCP服务器实现**：利用Spring AI的模型上下文协议服务器实现AI增强的金融操作
- **金融数据管理**：全面管理客户信息、贷款产品、信用额度、贷款合同、还款计划和逾期记录
- **现代技术栈**：基于Spring Boot、MyBatis Plus和Spring AI构建
- **数据库集成**：使用H2内存数据库，兼容MySQL模式
- **Web界面**：采用Thymeleaf模板引擎提供动态网页内容

## 项目结构

```
src/main/java/com/gientech/agentops/mcp/
├── controller/     # REST API控制器
├── entity/         # 实体类
├── mapper/         # MyBatis Plus映射接口
├── providers/      # MCP提供者
├── service/        # 业务逻辑服务
└── Mcp4ailApplication.java
```

## 功能特性

- **客户管理**：完整的客户信息跟踪，包括唯一身份识别
- **贷款产品目录**：管理各种贷款产品（公积金贷、税务贷、消费贷）
- **信用额度管理**：客户特定的信用额度和可用余额
- **合同管理**：详细的贷款合同跟踪，包括本金和利息计算
- **还款计划**：自动生成的还款计划，包含月度明细
- **逾期跟踪**：全面的逾期记录保存及罚金计算
- **H2控制台**：内置数据库管理界面

## 技术栈

- **Java 17** - 核心编程语言
- **Spring Boot 3.5.6** - 应用程序框架
- **Spring AI** - AI集成框架，包含MCP服务器
- **MyBatis Plus** - 高级ORM框架
- **H2数据库** - 内存数据库，支持持久化模式
- **Thymeleaf** - 服务器端Java模板引擎
- **Lombok** - 代码简化库
- **Maven** - 构建和依赖管理

## 配置

应用程序通过`application.properties`进行配置，包括：

- 服务器运行在9081端口
- MCP服务器名称为'loan-credit-server'
- H2数据库控制台启用在`/h2-console`
- 数据库模式和示例数据初始化

## 数据库模式

系统包含以下核心表：

- **customer**: 客户信息（姓名、证件类型、证件号码、联系方式）
- **loan_product**: 贷款产品定义，包含代码和描述
- **customer_credit**: 客户特定的信用额度和可用性
- **loan_contract**: 贷款协议，包含金额、日期和状态
- **repayment_plan**: 计划的月度还款，包含利息/本金明细
- **overdue_record**: 支付历史记录和罚金计算

## 示例数据

应用程序包含全面的示例数据，可立即用于测试：

- 3个客户，包含各种身份识别和联系信息
- 3种不同的贷款产品，涵盖消费贷、税务贷和公积金贷
- 多个信用额度，限额不同
- 5个贷款合同，条款和余额各异
- 2024年和2025年的还款计划
- 历史和预测的逾期记录

## 快速开始

### 系统要求

- Java 17 或更高版本
- Maven 3.6.0 或更高版本

### 运行应用程序

1. 克隆仓库：
   ```bash
   git clone [repository-url]
   cd mcp4ail
   ```

2. 构建项目：
   ```bash
   mvn clean install
   ```

3. 运行应用程序：
   ```bash
   mvn spring-boot:run
   ```

4. 访问应用程序：
   - 主应用程序: `http://localhost:9081`
   - H2控制台: `http://localhost:9081/h2-console`

5. H2控制台访问，使用以下连接设置：
   - 驱动类: `org.h2.Driver`
   - JDBC URL: `jdbc:h2:mem:testdb;MODE=MySQL`
   - 用户名: `sa`
   - 密码: （空）

## MCP服务器配置

应用程序实现了Spring AI的模型上下文协议服务器，配置为：
- 服务器名称: `loan-credit-server`
- 版本: `1.7.0`
- 协议: `STREAMABLE`
- MCP端点已配置用于AI集成

## 许可证

本项目采用MIT许可证 - 详情请查看LICENSE文件。

## 贡献

欢迎贡献！请随时提交Pull Request。