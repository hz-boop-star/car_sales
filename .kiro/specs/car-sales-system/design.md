# Design Document - 汽车销售管理系统

## Overview

汽车销售管理系统采用前后端分离的架构设计，后端使用 Spring Boot 3.x 提供 RESTful API，前端使用 Vue 3 + TypeScript 构建单页应用。系统核心特点是将关键业务逻辑（订单创建）下沉到数据库层面通过存储过程实现，确保事务一致性；同时使用数据库视图优化复杂统计查询，提升性能和可维护性。

### 技术架构概览

```
┌─────────────────────────────────────────────────────────┐
│                    Frontend Layer                        │
│  Vue 3 + TypeScript + Element Plus + Pinia + Axios      │
└─────────────────────────────────────────────────────────┘
                          ↓ HTTP/REST
┌─────────────────────────────────────────────────────────┐
│                    Backend Layer                         │
│  Spring Boot 3.x + MyBatis-Plus + Spring Security       │
│  - Controller (API Endpoints)                            │
│  - Service (Business Logic)                              │
│  - Mapper (Database Access)                              │
└─────────────────────────────────────────────────────────┘
                          ↓ JDBC
┌─────────────────────────────────────────────────────────┐
│                   Database Layer                         │
│  openGauss (PostgreSQL Compatible)                       │
│  - Tables (sys_user, car_info, customer, sales_order)   │
│  - Views (v_sales_statistics)                            │
│  - Stored Procedures (proc_create_order)                 │
│  - Indexes (idx_car_vin, idx_customer_phone, etc.)      │
└─────────────────────────────────────────────────────────┘
```

### 设计原则

1. **数据库优先原则**：核心事务逻辑（订单创建）必须在数据库层面通过存储过程实现，确保 ACID 特性
2. **一车一码原则**：每条 car_info 记录代表一辆具体的车，通过 VIN 唯一标识，避免"车型+数量"的设计
3. **无自增主键原则**：所有表主键使用雪花算法生成 BIGINT 类型 ID，满足课程设计要求
4. **视图优化原则**：复杂统计查询通过数据库视图实现，减少应用层 JOIN 操作
5. **前后端分离原则**：前端通过 RESTful API 与后端交互，支持独立部署和扩展

## Architecture

### 后端架构设计

#### 分层架构

```
Controller Layer (控制层)
├── UserController - 用户认证与管理
├── CarController - 车辆库存管理
├── CustomerController - 客户档案管理
├── OrderController - 销售订单管理
└── StatisticsController - 统计报表

Service Layer (业务层)
├── UserService - 用户业务逻辑
├── CarService - 车辆业务逻辑（含导入导出）
├── CustomerService - 客户业务逻辑
├── OrderService - 订单业务逻辑（调用存储过程）
└── StatisticsService - 统计业务逻辑（查询视图）

Mapper Layer (数据访问层)
├── UserMapper - 用户数据访问
├── CarMapper - 车辆数据访问
├── CustomerMapper - 客户数据访问
├── OrderMapper - 订单数据访问
└── StatisticsMapper - 统计数据访问（视图查询）

Database Layer (数据库层)
├── Tables - 基础表结构
├── Views - 统计视图
├── Stored Procedures - 存储过程
└── Indexes - 索引优化
```

#### 核心组件职责

**Controller 层**：
- 接收 HTTP 请求，参数验证
- 调用 Service 层处理业务逻辑
- 返回统一格式的响应（Result<T>）
- 处理异常并转换为友好的错误信息

**Service 层**：
- 实现业务逻辑和流程控制
- 事务管理（@Transactional）
- 调用 Mapper 层进行数据操作
- 对于订单创建，调用数据库存储过程

**Mapper 层**：
- 使用 MyBatis-Plus 进行 CRUD 操作
- 自定义 SQL 查询（复杂查询、视图查询）
- 调用存储过程的接口定义

### 前端架构设计

#### 目录结构

```
src/
├── api/ - API 接口定义
│   ├── user.ts
│   ├── car.ts
│   ├── customer.ts
│   ├── order.ts
│   └── statistics.ts
├── components/ - 通用组件
│   ├── Layout/ - 布局组件
│   ├── Table/ - 表格组件
│   └── Chart/ - 图表组件
├── views/ - 页面视图
│   ├── Login.vue - 登录页
│   ├── Dashboard.vue - 仪表盘
│   ├── Car/ - 车辆管理
│   │   ├── CarList.vue
│   │   └── CarImport.vue
│   ├── Customer/ - 客户管理
│   │   └── CustomerList.vue
│   └── Order/ - 订单管理
│       ├── OrderList.vue
│       └── CreateOrder.vue
├── stores/ - Pinia 状态管理
│   ├── user.ts - 用户状态
│   └── app.ts - 应用状态
├── router/ - 路由配置
└── utils/ - 工具函数
    ├── request.ts - Axios 封装
    └── validate.ts - 表单验证
```

#### 状态管理

使用 Pinia 管理全局状态：
- **User Store**：存储用户信息、token、权限
- **App Store**：存储应用配置、主题、侧边栏状态

#### 路由设计

```typescript
routes = [
  { path: '/login', component: Login },
  { 
    path: '/', 
    component: Layout,
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'cars', component: CarList },
      { path: 'customers', component: CustomerList },
      { path: 'orders', component: OrderList },
      { path: 'orders/create', component: CreateOrder }
    ]
  }
]
```

## Components and Interfaces

### 后端 API 接口设计

#### 1. 用户认证接口

```java
// POST /api/auth/login
Request: {
  username: string,
  password: string
}
Response: Result<{
  token: string,
  userInfo: {
    id: long,
    username: string,
    realName: string,
    role: string // "ADMIN" | "SALESPERSON"
  }
}>

// POST /api/auth/logout
Response: Result<void>
```

#### 2. 车辆管理接口

```java
// GET /api/cars - 查询车辆列表（支持多条件筛选）
Query Parameters: {
  brand?: string,        // 品牌（精确匹配）
  minPrice?: number,     // 最低价格
  maxPrice?: number,     // 最高价格
  status?: number,       // 状态：0-在库, 1-锁定, 2-已售
  pageNum: number,
  pageSize: number
}
Response: Result<Page<CarVO>>

// POST /api/cars - 创建车辆
Request: {
  vin: string,
  brand: string,
  model: string,
  price: number,
  color?: string,
  year?: number
}
Response: Result<CarVO>

// PUT /api/cars/{id} - 更新车辆
Request: CarUpdateDTO
Response: Result<CarVO>

// DELETE /api/cars/{id} - 删除车辆
Response: Result<void>

// POST /api/cars/import - 批量导入车辆
Request: MultipartFile (Excel)
Response: Result<{
  successCount: number,
  failCount: number,
  errors: string[]
}>

// GET /api/cars/export - 导出车辆列表
Query Parameters: (same as list query)
Response: Excel File Download
```

#### 3. 客户管理接口

```java
// GET /api/customers - 查询客户列表
Query Parameters: {
  name?: string,         // 姓名（模糊匹配）
  phone?: string,        // 手机号（精确匹配）
  pageNum: number,
  pageSize: number
}
Response: Result<Page<CustomerVO>>

// POST /api/customers - 创建客户
Request: {
  name: string,
  phone: string,
  idCard: string,
  address?: string
}
Response: Result<CustomerVO>

// PUT /api/customers/{id} - 更新客户
Request: CustomerUpdateDTO
Response: Result<CustomerVO>

// DELETE /api/customers/{id} - 删除客户
Response: Result<void>
```

#### 4. 订单管理接口

```java
// GET /api/orders - 查询订单列表
Query Parameters: {
  startDate?: string,    // 开始日期
  endDate?: string,      // 结束日期
  salesUserId?: long,    // 销售员ID
  status?: number,       // 订单状态
  pageNum: number,
  pageSize: number
}
Response: Result<Page<OrderVO>>

// POST /api/orders - 创建订单（调用存储过程）
Request: {
  customerId: long,
  carId: long,
  actualPrice: number
}
Response: Result<OrderVO>

// GET /api/orders/{id} - 查询订单详情
Response: Result<OrderDetailVO>
```

#### 5. 统计报表接口

```java
// GET /api/statistics/sales-by-brand - 品牌销量统计（查询视图）
Query Parameters: {
  startDate?: string,
  endDate?: string
}
Response: Result<List<{
  brand: string,
  salesCount: number,
  totalAmount: number
}>>

// GET /api/statistics/sales-trend - 销售趋势统计（查询视图）
Query Parameters: {
  startDate?: string,
  endDate?: string,
  groupBy: string // "day" | "month"
}
Response: Result<List<{
  date: string,
  salesCount: number,
  totalAmount: number
}>>

// GET /api/statistics/dashboard - 仪表盘数据
Response: Result<{
  totalCars: number,
  availableCars: number,
  soldCars: number,
  totalOrders: number,
  totalRevenue: number,
  brandDistribution: Array<{brand: string, count: number}>,
  monthlyTrend: Array<{month: string, sales: number}>
}>
```

### 前端组件接口

#### 1. 车辆列表组件 (CarList.vue)

```typescript
interface CarListProps {
  // 无 props，内部管理状态
}

interface CarListState {
  carList: Car[],
  loading: boolean,
  queryParams: {
    brand: string,
    minPrice: number,
    maxPrice: number,
    status: number,
    pageNum: number,
    pageSize: number
  },
  total: number
}

interface CarListMethods {
  fetchCarList(): Promise<void>,
  handleSearch(): void,
  handleReset(): void,
  handleEdit(car: Car): void,
  handleDelete(id: number): void,
  handleImport(): void,
  handleExport(): void
}
```

#### 2. 订单创建组件 (CreateOrder.vue)

```typescript
interface CreateOrderProps {
  // 无 props
}

interface CreateOrderState {
  currentStep: number, // 0: 选择客户, 1: 选择车辆, 2: 确认订单
  selectedCustomer: Customer | null,
  selectedCar: Car | null,
  actualPrice: number,
  customerList: Customer[],
  availableCarList: Car[]
}

interface CreateOrderMethods {
  nextStep(): void,
  prevStep(): void,
  selectCustomer(customer: Customer): void,
  selectCar(car: Car): void,
  submitOrder(): Promise<void>
}
```

#### 3. 仪表盘组件 (Dashboard.vue)

```typescript
interface DashboardProps {
  // 无 props
}

interface DashboardState {
  statistics: {
    totalCars: number,
    availableCars: number,
    soldCars: number,
    totalOrders: number,
    totalRevenue: number
  },
  brandChartData: Array<{name: string, value: number}>,
  trendChartData: Array<{date: string, sales: number}>
}

interface DashboardMethods {
  fetchDashboardData(): Promise<void>,
  initBrandChart(): void,
  initTrendChart(): void
}
```

## Data Models

### 数据库表结构设计

#### 1. sys_user (系统用户表)

```sql
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY,                    -- 主键（雪花算法生成）
  username VARCHAR(50) NOT NULL UNIQUE,     -- 用户名
  password VARCHAR(255) NOT NULL,           -- 密码（加密存储）
  real_name VARCHAR(50) NOT NULL,           -- 真实姓名
  role VARCHAR(20) NOT NULL,                -- 角色：ADMIN, SALESPERSON
  phone VARCHAR(20),                        -- 联系电话
  email VARCHAR(100),                       -- 邮箱
  status SMALLINT DEFAULT 1,                -- 状态：0-禁用, 1-启用
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'SALESPERSON')),
  CONSTRAINT chk_user_status CHECK (status IN (0, 1))
);

-- 索引
CREATE INDEX idx_user_username ON sys_user(username);
```

#### 2. car_info (车辆信息表)

```sql
CREATE TABLE car_info (
  id BIGINT PRIMARY KEY,                    -- 主键（雪花算法生成）
  vin VARCHAR(17) NOT NULL UNIQUE,          -- 车架号（唯一标识）
  brand VARCHAR(50) NOT NULL,               -- 品牌
  model VARCHAR(100) NOT NULL,              -- 型号
  color VARCHAR(30),                        -- 颜色
  year INTEGER,                             -- 年份
  price NUMERIC(12, 2) NOT NULL,            -- 价格
  status SMALLINT DEFAULT 0,                -- 状态：0-在库, 1-锁定, 2-已售
  purchase_date DATE,                       -- 进货日期
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_car_status CHECK (status IN (0, 1, 2)),
  CONSTRAINT chk_car_price CHECK (price > 0)
);

-- 索引
CREATE INDEX idx_car_vin ON car_info(vin);
CREATE INDEX idx_car_brand ON car_info(brand);
CREATE INDEX idx_car_status ON car_info(status);
CREATE INDEX idx_car_price ON car_info(price);
```

#### 3. customer (客户信息表)

```sql
CREATE TABLE customer (
  id BIGINT PRIMARY KEY,                    -- 主键（雪花算法生成）
  name VARCHAR(50) NOT NULL,                -- 姓名
  phone VARCHAR(20) NOT NULL UNIQUE,        -- 手机号（唯一）
  id_card VARCHAR(18) NOT NULL UNIQUE,      -- 身份证号（唯一）
  gender CHAR(1),                           -- 性别：M-男, F-女
  address VARCHAR(200),                     -- 地址
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_customer_gender CHECK (gender IN ('M', 'F'))
);

-- 索引
CREATE INDEX idx_customer_phone ON customer(phone);
CREATE INDEX idx_customer_name ON customer(name);
```

#### 4. sales_order (销售订单表)

```sql
CREATE TABLE sales_order (
  id BIGINT PRIMARY KEY,                    -- 主键（雪花算法生成）
  order_no VARCHAR(32) NOT NULL UNIQUE,     -- 订单编号
  sales_user_id BIGINT NOT NULL,            -- 销售员ID
  customer_id BIGINT NOT NULL,              -- 客户ID
  car_id BIGINT NOT NULL,                   -- 车辆ID
  original_price NUMERIC(12, 2) NOT NULL,   -- 原价
  actual_price NUMERIC(12, 2) NOT NULL,     -- 实际成交价
  discount_amount NUMERIC(12, 2) DEFAULT 0, -- 优惠金额
  order_date DATE NOT NULL,                 -- 订单日期
  status SMALLINT DEFAULT 1,                -- 订单状态：1-已完成, 2-已取消
  remark TEXT,                              -- 备注
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_order_sales_user FOREIGN KEY (sales_user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
  CONSTRAINT fk_order_car FOREIGN KEY (car_id) REFERENCES car_info(id),
  CONSTRAINT chk_order_status CHECK (status IN (1, 2)),
  CONSTRAINT chk_order_price CHECK (actual_price > 0)
);

-- 索引
CREATE INDEX idx_order_sales_user ON sales_order(sales_user_id);
CREATE INDEX idx_order_customer ON sales_order(customer_id);
CREATE INDEX idx_order_car ON sales_order(car_id);
CREATE INDEX idx_order_date ON sales_order(order_date);
CREATE INDEX idx_order_no ON sales_order(order_no);
```

### 数据库视图设计

#### v_sales_statistics (销售统计视图)

```sql
CREATE OR REPLACE VIEW v_sales_statistics AS
SELECT 
  c.brand,
  c.model,
  COUNT(o.id) AS sales_count,
  SUM(o.actual_price) AS total_amount,
  AVG(o.actual_price) AS avg_price,
  MIN(o.order_date) AS first_sale_date,
  MAX(o.order_date) AS last_sale_date,
  u.real_name AS salesperson_name
FROM sales_order o
INNER JOIN car_info c ON o.car_id = c.id
INNER JOIN sys_user u ON o.sales_user_id = u.id
WHERE o.status = 1  -- 只统计已完成订单
GROUP BY c.brand, c.model, u.real_name;
```

#### v_monthly_sales_trend (月度销售趋势视图)

```sql
CREATE OR REPLACE VIEW v_monthly_sales_trend AS
SELECT 
  TO_CHAR(o.order_date, 'YYYY-MM') AS month,
  COUNT(o.id) AS sales_count,
  SUM(o.actual_price) AS total_amount,
  AVG(o.actual_price) AS avg_price
FROM sales_order o
WHERE o.status = 1
GROUP BY TO_CHAR(o.order_date, 'YYYY-MM')
ORDER BY month DESC;
```

### 存储过程设计

#### proc_create_order (创建订单存储过程)

```sql
CREATE OR REPLACE FUNCTION proc_create_order(
  p_sales_user_id BIGINT,
  p_customer_id BIGINT,
  p_car_id BIGINT,
  p_actual_price NUMERIC,
  OUT p_order_id BIGINT,
  OUT p_result_code INTEGER,
  OUT p_result_msg VARCHAR
)
RETURNS RECORD AS $$
DECLARE
  v_car_status SMALLINT;
  v_original_price NUMERIC;
  v_order_no VARCHAR(32);
BEGIN
  -- 初始化返回值
  p_result_code := 0;
  p_result_msg := 'Success';
  
  -- 检查车辆状态
  SELECT status, price INTO v_car_status, v_original_price
  FROM car_info
  WHERE id = p_car_id
  FOR UPDATE;  -- 行锁，防止并发问题
  
  IF NOT FOUND THEN
    p_result_code := 1;
    p_result_msg := '车辆不存在';
    RETURN;
  END IF;
  
  IF v_car_status != 0 THEN
    p_result_code := 2;
    p_result_msg := '车辆状态不可售（已锁定或已售出）';
    RETURN;
  END IF;
  
  -- 生成订单编号（时间戳 + 随机数）
  v_order_no := 'ORD' || TO_CHAR(CURRENT_TIMESTAMP, 'YYYYMMDDHH24MISS') || LPAD(FLOOR(RANDOM() * 1000)::TEXT, 3, '0');
  
  -- 生成订单ID（使用雪花算法，这里简化为序列）
  -- 注意：实际项目中应在应用层使用雪花算法生成
  SELECT NEXTVAL('seq_order_id') INTO p_order_id;
  
  -- 创建订单
  INSERT INTO sales_order (
    id, order_no, sales_user_id, customer_id, car_id,
    original_price, actual_price, discount_amount, order_date, status
  ) VALUES (
    p_order_id, v_order_no, p_sales_user_id, p_customer_id, p_car_id,
    v_original_price, p_actual_price, v_original_price - p_actual_price, CURRENT_DATE, 1
  );
  
  -- 更新车辆状态为已售
  UPDATE car_info
  SET status = 2, update_time = CURRENT_TIMESTAMP
  WHERE id = p_car_id;
  
  p_result_msg := '订单创建成功，订单号：' || v_order_no;
  
EXCEPTION
  WHEN OTHERS THEN
    p_result_code := -1;
    p_result_msg := '系统错误：' || SQLERRM;
END;
$$ LANGUAGE plpgsql;
```

### Java 实体类设计

#### CarInfo.java

```java
@Data
@TableName("car_info")
public class CarInfo {
    @TableId(type = IdType.ASSIGN_ID)  // 雪花算法生成ID
    private Long id;
    
    private String vin;
    private String brand;
    private String model;
    private String color;
    private Integer year;
    private BigDecimal price;
    private Integer status;  // 0-在库, 1-锁定, 2-已售
    private LocalDate purchaseDate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

#### Customer.java

```java
@Data
@TableName("customer")
public class Customer {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String name;
    private String phone;
    private String idCard;
    private String gender;
    private String address;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

#### SalesOrder.java

```java
@Data
@TableName("sales_order")
public class SalesOrder {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String orderNo;
    private Long salesUserId;
    private Long customerId;
    private Long carId;
    private BigDecimal originalPrice;
    private BigDecimal actualPrice;
    private BigDecimal discountAmount;
    private LocalDate orderDate;
    private Integer status;
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

### 前端数据模型

#### TypeScript 接口定义

```typescript
// types/car.ts
export interface Car {
  id: number;
  vin: string;
  brand: string;
  model: string;
  color?: string;
  year?: number;
  price: number;
  status: 0 | 1 | 2;  // 0-在库, 1-锁定, 2-已售
  purchaseDate?: string;
  createTime: string;
  updateTime: string;
}

export interface CarQueryParams {
  brand?: string;
  minPrice?: number;
  maxPrice?: number;
  status?: number;
  pageNum: number;
  pageSize: number;
}

// types/customer.ts
export interface Customer {
  id: number;
  name: string;
  phone: string;
  idCard: string;
  gender?: 'M' | 'F';
  address?: string;
  createTime: string;
  updateTime: string;
}

// types/order.ts
export interface Order {
  id: number;
  orderNo: string;
  salesUserId: number;
  customerId: number;
  carId: number;
  originalPrice: number;
  actualPrice: number;
  discountAmount: number;
  orderDate: string;
  status: 1 | 2;  // 1-已完成, 2-已取消
  remark?: string;
  createTime: string;
  updateTime: string;
}

export interface OrderDetail extends Order {
  salesUserName: string;
  customerName: string;
  customerPhone: string;
  carBrand: string;
  carModel: string;
  carVin: string;
}

export interface CreateOrderRequest {
  customerId: number;
  carId: number;
  actualPrice: number;
}

// types/statistics.ts
export interface BrandSalesStatistics {
  brand: string;
  salesCount: number;
  totalAmount: number;
}

export interface SalesTrend {
  date: string;
  salesCount: number;
  totalAmount: number;
}

export interface DashboardData {
  totalCars: number;
  availableCars: number;
  soldCars: number;
  totalOrders: number;
  totalRevenue: number;
  brandDistribution: Array<{brand: string; count: number}>;
  monthlyTrend: Array<{month: string; sales: number}>;
}
```

## Correctness Properties

A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees. In this system, we use property-based testing to validate that our implementation conforms to these correctness properties across a wide range of inputs.

### Property 1: Snowflake ID Uniqueness

*For any* sequence of entity creations (users, vehicles, customers, or orders), all generated primary keys should be unique across the entire system.

**Validates: Requirements 2.1, 3.1, 4.2, 5.2, 7.2**

### Property 2: Uniqueness Constraint Enforcement

*For any* attempt to create or update an entity with a duplicate unique field (VIN, phone, id_card), the system should reject the operation and return an appropriate error.

**Validates: Requirements 2.2, 2.8, 3.3, 3.4, 3.7, 9.3**

### Property 3: Data Persistence Round Trip

*For any* entity (vehicle, customer, or order) created with valid attributes, retrieving that entity by its ID should return all originally stored attributes unchanged.

**Validates: Requirements 2.3, 3.2, 4.6**

### Property 4: Initial Vehicle Status

*For any* newly created vehicle, its initial inventory status should be 0 (在库/available).

**Validates: Requirements 2.4**

### Property 5: Brand Filter Correctness

*For any* brand filter value, all vehicles returned by the query should have that exact brand value.

**Validates: Requirements 2.5**

### Property 6: Price Range Filter Correctness

*For any* price range (minPrice, maxPrice), all vehicles returned by the query should have prices within that range (inclusive).

**Validates: Requirements 2.6**

### Property 7: Status Filter Correctness

*For any* status filter value, all vehicles returned by the query should have that exact status value.

**Validates: Requirements 2.7, 8.5**

### Property 8: Sold Vehicle Deletion Prevention

*For any* vehicle with status 2 (已售/sold), attempting to delete it should fail and return an error.

**Validates: Requirements 2.9**

### Property 9: Customer Name Fuzzy Search

*For any* name search query, all customers returned should have names containing that query string (case-insensitive substring match).

**Validates: Requirements 3.5**

### Property 10: Customer Phone Exact Search

*For any* phone search query, all customers returned should have that exact phone number.

**Validates: Requirements 3.6**

### Property 11: Customer with Orders Deletion Prevention

*For any* customer who has at least one sales order, attempting to delete that customer should fail and return an error.

**Validates: Requirements 3.8**

### Property 12: Authentication Success with Valid Credentials

*For any* user with valid credentials (username and password), attempting to log in should succeed and return a session token.

**Validates: Requirements 1.1**

### Property 13: Authentication Failure with Invalid Credentials

*For any* invalid credential combination (non-existent username or incorrect password), attempting to log in should fail and return an authentication error.

**Validates: Requirements 1.2**

### Property 14: Role-Based Access Control

*For any* user, their role (ADMIN or SALESPERSON) should be correctly stored and retrievable, and access to functions should be granted or denied based on that role.

**Validates: Requirements 1.3, 1.4, 1.5**

### Property 15: Session Expiration Enforcement

*For any* expired session token, attempting to access protected resources should fail and require re-authentication.

**Validates: Requirements 1.6**


### Property 16: Order Creation State Transition

*For any* successful order creation, the associated vehicle's status should transition from 0 (在库) to 2 (已售).

**Validates: Requirements 4.3**

### Property 17: Order Entity Relationships

*For any* created order, it should correctly reference valid user, customer, and vehicle entities (foreign key integrity).

**Validates: Requirements 4.4**

### Property 18: Non-Available Vehicle Order Prevention

*For any* vehicle with status not equal to 0 (在库), attempting to create an order for that vehicle should fail and return an error.

**Validates: Requirements 4.5**

### Property 19: Order Query Date Range Filter

*For any* date range filter (startDate, endDate), all orders returned should have order_date within that range (inclusive).

**Validates: Requirements 4.8**

### Property 20: Order Query Salesperson Filter

*For any* salesperson ID filter, all orders returned should have that sales_user_id.

**Validates: Requirements 4.9**

### Property 21: Order Query Status Filter

*For any* order status filter, all orders returned should have that exact status value.

**Validates: Requirements 4.10**

### Property 22: Order Query with Joined Data

*For any* order query result, each order should include associated user, customer, and vehicle information (proper JOIN execution).

**Validates: Requirements 4.7**

### Property 23: Excel Import ID Uniqueness

*For any* batch of vehicles imported from Excel, all generated IDs should be unique within the batch and not conflict with existing IDs.

**Validates: Requirements 5.2**

### Property 24: Excel Import VIN Uniqueness Validation

*For any* batch import containing duplicate VINs (within batch or with existing data), the entire import should fail and return detailed error messages.

**Validates: Requirements 5.3**

### Property 25: Import Transaction Atomicity

*For any* import operation that fails validation, no partial data should be inserted into the database (all-or-nothing).

**Validates: Requirements 5.4, 9.1**

### Property 26: Excel Export Completeness

*For any* export request, the generated Excel file should contain all order data matching the filter criteria, including joined customer and vehicle information.

**Validates: Requirements 5.5, 5.6, 5.7**

### Property 27: Statistics View Aggregation Correctness

*For any* query to the sales statistics view, the aggregated values (count, sum, average) should match manual calculations from the raw sales_order data.

**Validates: Requirements 6.2**

### Property 28: View Data Consistency

*For any* change to underlying sales_order, car_info, or sys_user tables, subsequent queries to the statistics view should reflect the updated data.

**Validates: Requirements 6.7**

### Property 29: NOT NULL Constraint Enforcement

*For any* attempt to insert or update an entity with NULL values in required fields, the database should reject the operation.

**Validates: Requirements 9.2**

### Property 30: CHECK Constraint Enforcement

*For any* attempt to insert or update an entity with invalid values (e.g., status outside allowed range, negative price), the database should reject the operation.

**Validates: Requirements 9.4**

### Property 31: Foreign Key Constraint Enforcement

*For any* attempt to create an order with non-existent user_id, customer_id, or car_id, the database should reject the operation.

**Validates: Requirements 9.5**

### Property 32: Concurrent Purchase Prevention

*For any* two concurrent attempts to purchase the same vehicle, only one should succeed and the other should fail with an appropriate error.

**Validates: Requirements 9.6**

### Property 33: Input Validation Before Database Operations

*For any* API request with invalid input data (e.g., malformed VIN, invalid phone format), the system should reject it before attempting database operations.

**Validates: Requirements 9.7**

### Property 34: Critical Operation Logging

*For any* critical operation (order creation, vehicle status change, user authentication), the system should generate a log entry with timestamp and operation details.

**Validates: Requirements 9.8**

### Property 35: CORS Configuration

*For any* cross-origin HTTP request from the configured frontend domain, the backend should include appropriate CORS headers in the response.

**Validates: Requirements 10.2**

### Property 36: Unified Error Response Format

*For any* exception or error condition, the API should return a response following the Result<T> wrapper format with consistent structure.

**Validates: Requirements 10.3**

### Property 37: Unified Success Response Format

*For any* successful API operation, the response should follow the Result<T> wrapper format with consistent structure.

**Validates: Requirements 10.4**

### Property 38: Environment-Specific Configuration Loading

*For any* deployment environment (development, production), the system should load the appropriate configuration file and use environment-specific settings.

**Validates: Requirements 10.8**

## 错误处理（Error Handling）

### 错误响应结构

所有 API 错误都遵循统一的响应格式：

```java
public class Result<T> {
    private Integer code;      // 错误码：0-成功, 非0-失败
    private String message;    // 错误消息
    private T data;            // 响应数据
    private Long timestamp;    // 时间戳
}
```

### 错误码分类

- **1xxx**: 认证授权错误
  - 1001: 用户名或密码错误
  - 1002: Token 无效或过期
  - 1003: 权限不足

- **2xxx**: 参数验证错误
  - 2001: 必填参数缺失
  - 2002: 参数格式错误
  - 2003: 参数值超出范围

- **3xxx**: 业务逻辑错误
  - 3001: 车辆不存在
  - 3002: 车辆状态不可售
  - 3003: 客户已存在（手机号或身份证重复）
  - 3004: VIN 已存在
  - 3005: 客户有关联订单，无法删除
  - 3006: 已售车辆无法删除

- **4xxx**: 数据库错误
  - 4001: 唯一约束冲突
  - 4002: 外键约束冲突
  - 4003: 检查约束冲突
  - 4004: 事务回滚

- **5xxx**: 系统错误
  - 5001: 文件上传失败
  - 5002: 文件解析失败
  - 5003: 数据库连接失败
  - 5004: 未知系统错误

### 异常处理策略

**全局异常处理器**：
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolation(ConstraintViolationException e) {
        return Result.error(4001, "数据约束冲突：" + e.getMessage());
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<?> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return Result.error(4002, "数据完整性错误：" + e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(5004, "系统错误，请联系管理员");
    }
}
```

### 事务回滚规则

- 所有涉及多个数据库操作的 Service 方法都使用 `@Transactional` 注解
- 任何未检查异常（Unchecked Exception）都会触发自动回滚
- 已检查异常（Checked Exception）需要显式配置回滚
- 通过存储过程创建订单时，回滚在存储过程内部处理

### 验证策略

**输入验证层次**：
1. **前端验证**：使用 Element Plus 表单验证进行基本格式和必填字段检查
2. **控制器验证**：使用 JSR-303 Bean Validation 注解（`@Valid`、`@NotNull`、`@Pattern`）
3. **服务层验证**：业务规则验证（例如，车辆可用性、客户唯一性）
4. **数据库验证**：约束强制执行（UNIQUE、CHECK、FOREIGN KEY）

## 测试策略（Testing Strategy）

### 双重测试方法

本系统采用单元测试和基于属性的测试相结合的方式，以确保全面的正确性验证：

- **单元测试（Unit Tests）**：验证特定示例、边界情况和错误条件
- **属性测试（Property Tests）**：通过随机化测试验证所有输入的通用属性

这两种测试方法是互补且必要的。单元测试捕获特定场景中的具体错误，而属性测试验证广泛输入空间中的一般正确性。

### 基于属性的测试配置

**测试库**：我们将使用 **Jqwik** 进行 Java 属性测试，使用 **fast-check** 进行 TypeScript 属性测试。

**配置要求**：
- 每个属性测试必须运行至少 100 次迭代（由于随机化）
- 每个属性测试必须包含引用设计文档属性的注释标签
- 标签格式：`// Feature: car-sales-system, Property N: [属性描述]`
- 设计文档中的每个正确性属性都必须由单个基于属性的测试实现

**属性测试结构示例**：
```java
@Property
@Label("Feature: car-sales-system, Property 1: Snowflake ID Uniqueness")
void allGeneratedIdsShouldBeUnique(@ForAll("vehicles") List<CarInfo> vehicles) {
    Set<Long> ids = vehicles.stream()
        .map(CarInfo::getId)
        .collect(Collectors.toSet());
    
    assertThat(ids).hasSize(vehicles.size());
}
```

### 单元测试策略

**单元测试重点领域**：
- 演示正确行为的特定示例
- 边界情况（空输入、边界值、null 处理）
- 错误条件（无效凭证、约束违规）
- 组件之间的集成点（controller-service-mapper）

**测试覆盖率目标**：
- Service 层：80%+ 覆盖率
- Controller 层：70%+ 覆盖率
- Mapper 层：通过集成测试覆盖
- 工具类：90%+ 覆盖率

### 集成测试

**数据库集成测试**：
- 测试存储过程执行和回滚行为
- 测试视图查询与原始数据的正确性
- 测试并发事务处理
- 使用 testcontainers 和 PostgreSQL 创建隔离的测试数据库

**API 集成测试**：
- 测试完整的请求-响应周期
- 测试认证和授权流程
- 测试文件上传/下载操作
- 使用 MockMvc 进行 Spring Boot 控制器测试

### 前端测试

**组件测试**：
- 测试组件在各种 props 下的渲染
- 测试用户交互（按钮点击、表单提交）
- 测试状态管理（Pinia stores）
- 使用 Vitest + Vue Test Utils

**端到端测试（E2E）**（课程项目可选）：
- 测试完整的用户工作流（登录 → 创建订单 → 查看仪表盘）
- 使用 Playwright 或 Cypress

### 测试数据生成

**属性测试生成器**：
- **车辆生成器**：随机 VIN（17 字符）、品牌、型号、价格（正数）、状态（0-2）
- **客户生成器**：随机姓名、手机号（11 位数字）、身份证号（18 字符）、地址
- **订单生成器**：随机 user_id、customer_id、car_id（引用现有实体）、actual_price
- **用户生成器**：随机用户名、密码（加密）、角色（ADMIN/SALESPERSON）

**生成器约束**：
- VIN 必须是 17 个字母数字字符
- 手机号必须是 11 位数字
- 身份证号必须是 18 个字符（17 位数字 + 1 位校验位或 X）
- 价格必须是正小数
- 日期必须有效且在合理范围内

### 持续测试

- 每次提交时运行单元测试
- 每次 Pull Request 时运行属性测试
- 部署前运行集成测试
- 监控测试执行时间并优化慢速测试
