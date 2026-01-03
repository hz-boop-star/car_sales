-- ============================================================================
-- 汽车销售管理系统 - SQLite 数据库初始化脚本
-- 自动执行：Spring Boot 启动时会自动执行此脚本
-- ============================================================================

-- 删除已存在的表（按依赖关系逆序删除）
DROP TABLE IF EXISTS sales_order;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS car_info;
DROP TABLE IF EXISTS sys_user;

-- ----------------------------------------------------------------------------
-- 1. sys_user 表（系统用户表）
-- ----------------------------------------------------------------------------
CREATE TABLE sys_user (
    id INTEGER PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'SALESPERSON')),
    phone VARCHAR(20),
    email VARCHAR(100),
    status INTEGER DEFAULT 1 CHECK (status IN (0, 1)),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 2. car_info 表（车辆信息表）
-- ----------------------------------------------------------------------------
CREATE TABLE car_info (
    id INTEGER PRIMARY KEY,
    vin VARCHAR(17) NOT NULL UNIQUE,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    color VARCHAR(30),
    year INTEGER CHECK (year IS NULL OR (year >= 1900 AND year <= 2100)),
    price DECIMAL(12, 2) NOT NULL CHECK (price > 0),
    status INTEGER DEFAULT 0 CHECK (status IN (0, 1, 2)),
    purchase_date DATE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 3. customer 表（客户信息表）
-- ----------------------------------------------------------------------------
CREATE TABLE customer (
    id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    id_card VARCHAR(18) NOT NULL UNIQUE,
    gender CHAR(1) CHECK (gender IS NULL OR gender IN ('M', 'F')),
    address VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 4. sales_order 表（销售订单表）
-- ----------------------------------------------------------------------------
CREATE TABLE sales_order (
    id INTEGER PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL UNIQUE,
    sales_user_id INTEGER NOT NULL,
    customer_id INTEGER NOT NULL,
    car_id INTEGER NOT NULL,
    original_price DECIMAL(12, 2) NOT NULL CHECK (original_price > 0),
    actual_price DECIMAL(12, 2) NOT NULL CHECK (actual_price > 0),
    discount_amount DECIMAL(12, 2) DEFAULT 0 CHECK (discount_amount >= 0),
    order_date DATE NOT NULL,
    status INTEGER DEFAULT 1 CHECK (status IN (1, 2)),
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (sales_user_id) REFERENCES sys_user(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (car_id) REFERENCES car_info(id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ============================================================================
-- 创建索引
-- ============================================================================

-- sys_user 表索引
CREATE INDEX idx_user_username ON sys_user(username);

-- car_info 表索引
CREATE INDEX idx_car_vin ON car_info(vin);
CREATE INDEX idx_car_brand ON car_info(brand);
CREATE INDEX idx_car_status ON car_info(status);
CREATE INDEX idx_car_price ON car_info(price);

-- customer 表索引
CREATE INDEX idx_customer_phone ON customer(phone);
CREATE INDEX idx_customer_name ON customer(name);

-- sales_order 表索引
CREATE INDEX idx_order_sales_user ON sales_order(sales_user_id);
CREATE INDEX idx_order_customer ON sales_order(customer_id);
CREATE INDEX idx_order_car ON sales_order(car_id);
CREATE INDEX idx_order_date ON sales_order(order_date);
CREATE INDEX idx_order_no ON sales_order(order_no);

-- ============================================================================
-- 创建视图
-- ============================================================================

-- ----------------------------------------------------------------------------
-- v_sales_statistics 视图（销售统计视图）
-- 功能：按品牌、型号、销售员统计销售数据
-- ----------------------------------------------------------------------------
DROP VIEW IF EXISTS v_sales_statistics;
CREATE VIEW v_sales_statistics AS
SELECT 
    c.brand,
    c.model,
    COUNT(o.id) AS sales_count,
    SUM(o.actual_price) AS total_amount,
    AVG(o.actual_price) AS avg_price,
    MIN(o.order_date) AS first_sale_date,
    MAX(o.order_date) AS last_sale_date,
    u.id AS salesperson_id,
    u.real_name AS salesperson_name
FROM sales_order o
INNER JOIN car_info c ON o.car_id = c.id
INNER JOIN sys_user u ON o.sales_user_id = u.id
WHERE o.status = 1
GROUP BY c.brand, c.model, u.id, u.real_name;

-- ----------------------------------------------------------------------------
-- v_monthly_sales_trend 视图（月度销售趋势视图）
-- 功能：按月统计销售趋势
-- ----------------------------------------------------------------------------
DROP VIEW IF EXISTS v_monthly_sales_trend;
CREATE VIEW v_monthly_sales_trend AS
SELECT 
    strftime('%Y-%m', o.order_date) AS month,
    COUNT(o.id) AS sales_count,
    SUM(o.actual_price) AS total_amount,
    AVG(o.actual_price) AS avg_price,
    COUNT(DISTINCT o.sales_user_id) AS active_salesperson_count
FROM sales_order o
WHERE o.status = 1
GROUP BY strftime('%Y-%m', o.order_date)
ORDER BY month DESC;
