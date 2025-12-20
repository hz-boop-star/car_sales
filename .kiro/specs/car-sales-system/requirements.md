# Requirements Document - 汽车销售管理系统

## Introduction

汽车销售管理系统是一个面向汽车销售企业的综合管理平台，旨在规范销售流程、提高管理效率、实现车辆库存管理、客户关系管理和销售数据统计分析的数字化。系统采用 Spring Boot + Vue 技术栈，使用 openGauss 数据库，支持完整的销售业务流程管理。

## Glossary

- **System**: 汽车销售管理系统
- **User**: 系统用户（包括管理员和销售员）
- **Administrator**: 系统管理员，拥有所有权限
- **Salesperson**: 销售员，负责车辆销售和客户管理
- **Vehicle**: 车辆实体，代表一辆具体的汽车（一车一码）
- **VIN**: 车架号（Vehicle Identification Number），车辆唯一标识
- **Customer**: 客户实体，存储客户档案信息
- **Sales_Order**: 销售订单，记录车辆销售交易
- **Inventory**: 库存状态（在库、锁定、已售）
- **Database**: openGauss 数据库系统
- **Stored_Procedure**: 存储过程，数据库层面的事务处理逻辑
- **View**: 数据库视图，用于复杂查询和报表统计
- **Primary_Key**: 主键，使用雪花算法或 UUID 生成，禁止自增

## Requirements

### Requirement 1: 用户认证与权限管理

**User Story:** 作为系统管理员，我希望能够管理系统用户并控制其权限，以确保系统安全和职责分离。

#### Acceptance Criteria

1. WHEN a user attempts to log in with valid credentials, THE System SHALL authenticate the user and create a session
2. WHEN a user attempts to log in with invalid credentials, THE System SHALL reject the login and return an error message
3. THE System SHALL distinguish between Administrator and Salesperson roles
4. WHEN an Administrator accesses any system function, THE System SHALL grant full access
5. WHEN a Salesperson accesses administrative functions, THE System SHALL deny access and return an authorization error
6. WHEN a user session expires, THE System SHALL require re-authentication

### Requirement 2: 车辆库存管理

**User Story:** 作为销售员，我希望能够管理车辆库存信息，以便准确掌握可售车辆情况。

#### Acceptance Criteria

1. WHEN creating a new Vehicle record, THE System SHALL generate a unique Primary_Key using snowflake algorithm
2. WHEN creating a new Vehicle record, THE System SHALL validate that VIN is unique
3. THE System SHALL store Vehicle attributes including VIN, brand, model, price, and Inventory status
4. WHEN a Vehicle is created, THE System SHALL set its initial Inventory status to "在库" (available)
5. WHEN querying Vehicles, THE System SHALL support filtering by brand (exact match)
6. WHEN querying Vehicles, THE System SHALL support filtering by price range (minimum and maximum values)
7. WHEN querying Vehicles, THE System SHALL support filtering by Inventory status
8. WHEN updating a Vehicle, THE System SHALL validate that VIN remains unique if changed
9. WHEN deleting a Vehicle with status "已售", THE System SHALL prevent deletion and return an error
10. THE System SHALL display Vehicle Inventory status using visual indicators (tags or colors)

### Requirement 3: 客户档案管理

**User Story:** 作为销售员，我希望能够维护客户档案，以便建立长期客户关系并避免重复录入信息。

#### Acceptance Criteria

1. WHEN creating a new Customer record, THE System SHALL generate a unique Primary_Key using snowflake algorithm
2. THE System SHALL store Customer attributes including name, phone, and id_card
3. WHEN creating a Customer, THE System SHALL validate that phone number is unique
4. WHEN creating a Customer, THE System SHALL validate that id_card is unique
5. WHEN querying Customers, THE System SHALL support searching by name (fuzzy match)
6. WHEN querying Customers, THE System SHALL support searching by phone (exact match)
7. WHEN updating a Customer, THE System SHALL validate uniqueness constraints
8. WHEN deleting a Customer with existing Sales_Orders, THE System SHALL prevent deletion and return an error

### Requirement 4: 销售订单管理（核心业务逻辑）

**User Story:** 作为销售员，我希望能够创建销售订单并自动更新车辆状态，以确保销售流程的准确性和一致性。

#### Acceptance Criteria

1. WHEN creating a Sales_Order, THE Database SHALL execute a Stored_Procedure to handle the transaction
2. WHEN the Stored_Procedure executes, THE Database SHALL create a new Sales_Order record with a unique Primary_Key
3. WHEN the Stored_Procedure executes, THE Database SHALL update the Vehicle Inventory status from "在库" to "已售"
4. WHEN the Stored_Procedure executes, THE Database SHALL link the Sales_Order to the User, Customer, and Vehicle
5. IF the Vehicle Inventory status is not "在库", THEN THE Stored_Procedure SHALL rollback the transaction and return an error
6. WHEN a Sales_Order is created, THE System SHALL record the actual_price, order_date, and status
7. WHEN querying Sales_Orders, THE System SHALL display associated User, Customer, and Vehicle information
8. WHEN querying Sales_Orders, THE System SHALL support filtering by date range
9. WHEN querying Sales_Orders, THE System SHALL support filtering by Salesperson
10. WHEN querying Sales_Orders, THE System SHALL support filtering by order status

### Requirement 5: 数据导入导出

**User Story:** 作为管理员，我希望能够批量导入车辆数据和导出销售报表，以提高数据管理效率。

#### Acceptance Criteria

1. WHEN uploading an Excel file for Vehicle import, THE System SHALL parse the file and validate data format
2. WHEN importing Vehicles, THE System SHALL generate unique Primary_Keys for each record
3. WHEN importing Vehicles, THE System SHALL validate that all VINs are unique
4. IF any validation fails during import, THEN THE System SHALL reject the entire batch and return detailed error messages
5. WHEN exporting sales reports, THE System SHALL generate an Excel file containing Sales_Order data
6. WHEN exporting sales reports, THE System SHALL include related Customer and Vehicle information
7. WHEN exporting sales reports, THE System SHALL support filtering by date range before export
8. THE System SHALL use EasyExcel library for import and export operations

### Requirement 6: 销售统计与报表（数据库视图）

**User Story:** 作为管理员，我希望能够查看销售统计数据和可视化报表，以便分析销售趋势和业务表现。

#### Acceptance Criteria

1. THE Database SHALL provide a View for sales statistics aggregation
2. WHEN querying the sales statistics View, THE Database SHALL return aggregated data by brand, time period, or Salesperson
3. WHEN the frontend requests dashboard data, THE System SHALL query the Database View
4. WHEN displaying the dashboard, THE System SHALL render charts using ECharts library
5. THE System SHALL display brand sales distribution as a pie chart
6. THE System SHALL display monthly sales trends as a line or bar chart
7. WHEN the View data changes, THE System SHALL reflect updates in real-time queries
8. THE Database View SHALL join Sales_Order, Vehicle, and User tables for comprehensive statistics

### Requirement 7: 数据库设计约束（课程设计要求）

**User Story:** 作为系统架构师，我希望数据库设计符合课程设计规范，以满足评分要求和展示数据库能力。

#### Acceptance Criteria

1. THE Database SHALL NOT use AUTO_INCREMENT for any Primary_Key
2. THE System SHALL generate all Primary_Keys using snowflake algorithm (BIGINT type)
3. THE Database SHALL define foreign key constraints to ensure referential integrity
4. THE Database SHALL create at least one View for complex query operations
5. THE Database SHALL create at least one Stored_Procedure for transactional business logic
6. THE Database SHALL create indexes on high-frequency query fields (VIN, phone, order_date)
7. THE Database design SHALL conform to Third Normal Form (3NF)
8. THE Database SHALL enforce data consistency through constraints and triggers if needed

### Requirement 8: 系统界面与用户体验

**User Story:** 作为用户，我希望系统界面美观易用，以便快速完成日常工作任务。

#### Acceptance Criteria

1. THE System SHALL provide a classic admin layout with sidebar navigation, header, and content area
2. WHEN displaying Vehicle lists, THE System SHALL use tags to indicate Inventory status with different colors
3. WHEN creating a Sales_Order, THE System SHALL provide a wizard or dialog for step-by-step operation
4. WHEN selecting a Customer in the order wizard, THE System SHALL display a searchable dropdown list
5. WHEN selecting a Vehicle in the order wizard, THE System SHALL only show Vehicles with "在库" status
6. THE System SHALL provide visual feedback for loading states and operation results
7. THE System SHALL display error messages in a user-friendly format
8. THE System SHALL support responsive design for different screen sizes

### Requirement 9: 数据一致性与完整性

**User Story:** 作为系统架构师，我希望确保数据的一致性和完整性，以防止数据错误和业务逻辑漏洞。

#### Acceptance Criteria

1. WHEN any database operation fails, THE System SHALL rollback the entire transaction
2. THE Database SHALL enforce NOT NULL constraints on required fields
3. THE Database SHALL enforce UNIQUE constraints on VIN, phone, and id_card fields
4. THE Database SHALL enforce CHECK constraints on Inventory status values
5. THE Database SHALL enforce foreign key constraints between Sales_Order and related tables
6. WHEN concurrent users attempt to purchase the same Vehicle, THE Database SHALL prevent duplicate sales through locking mechanisms
7. THE System SHALL validate all input data before database operations
8. THE System SHALL log all critical operations for audit purposes

### Requirement 10: 系统配置与部署

**User Story:** 作为开发者，我希望系统易于配置和部署，以便快速搭建开发和演示环境。

#### Acceptance Criteria

1. THE System SHALL provide configuration files for database connection parameters
2. THE System SHALL support CORS configuration for frontend-backend communication
3. THE System SHALL provide global exception handling for consistent error responses
4. THE System SHALL provide unified response format (Result wrapper) for all API endpoints
5. THE System SHALL use PostgreSQL JDBC driver to connect to openGauss database
6. THE System SHALL provide SQL scripts for database initialization (DDL, sample data, views, stored procedures)
7. THE System SHALL provide clear documentation for installation and deployment
8. THE System SHALL support environment-specific configurations (development, production)
