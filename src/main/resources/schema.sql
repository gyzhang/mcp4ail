-- 客户信息表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    id_type VARCHAR(20) NOT NULL,
    id_number VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_id_type_number (id_type, id_number)
);

-- 贷款产品表
CREATE TABLE IF NOT EXISTS loan_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(20) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_product_code (product_code)
);

-- 客户授信表
CREATE TABLE IF NOT EXISTS customer_credit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    credit_limit DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    available_limit DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (product_id) REFERENCES loan_product(id),
    UNIQUE KEY uk_customer_product (customer_id, product_id)
);

-- 贷款合同表
CREATE TABLE IF NOT EXISTS loan_contract (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_no VARCHAR(50) NOT NULL,
    customer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    loan_amount DECIMAL(18,2) NOT NULL,
    loan_balance DECIMAL(18,2) NOT NULL,
    loan_date DATE NOT NULL,
    maturity_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (product_id) REFERENCES loan_product(id),
    UNIQUE KEY uk_contract_no (contract_no)
);

-- 还款计划表
CREATE TABLE IF NOT EXISTS repayment_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    repayment_date VARCHAR(7) NOT NULL, -- 格式：YYYY-MM
    repayment_amount DECIMAL(18,2) NOT NULL,
    interest_amount DECIMAL(18,2) NOT NULL,
    principal_amount DECIMAL(18,2) NOT NULL,
    remaining_balance DECIMAL(18,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (contract_id) REFERENCES loan_contract(id),
    UNIQUE KEY uk_contract_date (contract_id, repayment_date)
);

-- 逾期记录表
CREATE TABLE IF NOT EXISTS overdue_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_id BIGINT NOT NULL,
    overdue_date VARCHAR(7) NOT NULL, -- 格式：YYYY-MM
    due_amount DECIMAL(18,2) NOT NULL,
    paid_amount DECIMAL(18,2) NOT NULL,
    overdue_amount DECIMAL(18,2) NOT NULL,
    penalty_amount DECIMAL(18,2) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (contract_id) REFERENCES loan_contract(id),
    UNIQUE KEY uk_contract_overdue_date (contract_id, overdue_date)
);