# MCP4AIL - System demonstrating MCP Server development

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)![Java Version](https://img.shields.io/badge/Java-17-blue)![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen)![H2 Database](https://img.shields.io/badge/Database-H2-orange)

A Spring Boot application that implements a Loan Credit Management System using Spring AI MCP (Model Context Protocol) server. This system provides financial institutions with an intelligent credit management solution for loan processing and customer data management.

## Overview

MCP4AIL is a financial loan credit management system built with cutting-edge Spring technologies. It serves as both a demonstration of Spring AI MCP capabilities and a functional credit management platform. The application features:

- **MCP Server Implementation**: Leverages Spring AI's Model Context Protocol server for AI-enhanced financial operations
- **Financial Data Management**: Comprehensive management of customer information, loan products, credit lines, loan contracts, repayment schedules, and overdue records
- **Modern Tech Stack**: Built with Spring Boot, MyBatis Plus, and Spring AI
- **Database Integration**: Uses H2 in-memory database with MySQL compatibility mode
- **Web Interface**: Thymeleaf templating for dynamic web content

## Project Structure

```
src/main/java/com/gientech/agentops/mcp/
├── controller/     # REST API controllers
├── entity/         # Domain entities
├── mapper/         # MyBatis Plus mapper interfaces
├── providers/      # MCP providers
├── service/        # Business logic services
└── Mcp4ailApplication.java
```

## Features

- **Customer Management**: Complete customer information tracking with unique identification
- **Loan Product Catalog**: Management of various loan products (Housing Fund Loan, Tax Loan, Consumer Loan)
- **Credit Line Management**: Customer-specific credit limits and available balances
- **Contract Management**: Detailed loan contract tracking with principal and interest calculations
- **Repayment Planning**: Automated repayment schedule generation with monthly breakdowns
- **Overdue Tracking**: Comprehensive overdue record keeping with penalty calculations
- **H2 Console**: Built-in database administration interface

## Technology Stack

- **Java 17** - Core programming language
- **Spring Boot 3.5.6** - Application framework
- **Spring AI** - AI integration framework with MCP server
- **MyBatis Plus** - Advanced ORM framework
- **H2 Database** - In-memory database with persistent schema
- **Thymeleaf** - Server-side Java template engine
- **Lombok** - Code reduction library
- **Maven** - Build and dependency management

## Configuration

The application is configured via `application.properties` with:

- Server running on port 9081
- MCP server named 'loan-credit-server'
- H2 database with console enabled at `/h2-console`
- Database schema and sample data initialization

## Database Schema

The system includes the following core tables:

- **customer**: Customer information (name, ID type, ID number, contact details)
- **loan_product**: Loan product definitions with codes and descriptions
- **customer_credit**: Customer-specific credit limits and availability
- **loan_contract**: Loan agreements with amounts, dates, and statuses
- **repayment_plan**: Scheduled monthly repayments with interest/principal breakdowns
- **overdue_record**: Historical records of payments and penalties

## Sample Data

The application ships with comprehensive sample data for immediate testing:

- 3 customers with various identification and contact information
- 3 different loan products covering consumer, tax, and housing fund loans
- Multiple credit lines with varying limits
- 5 loan contracts with different terms and balances
- Repayment schedules for 2024 and 2025
- Historical and projected overdue records

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher

### Running the Application

1. Clone the repository:
   ```bash
   git clone [repository-url]
   cd mcp4ail
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access the application:
   - Main application: `http://localhost:9081`
   - H2 Console: `http://localhost:9081/h2-console`

5. For H2 Console access, use the following connection settings:
   - Driver Class: `org.h2.Driver`
   - JDBC URL: `jdbc:h2:mem:testdb;MODE=MySQL`
   - Username: `sa`
   - Password: (empty)

## MCP Server Configuration

The application implements Spring AI's Model Context Protocol server with:
- Server name: `loan-credit-server`
- Version: `1.7.0`
- Protocol: `STREAMABLE`
- MCP endpoint is configured for AI integration

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.