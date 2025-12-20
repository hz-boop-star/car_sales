# 汽车销售管理系统 - 后端

Car Sales Management System - Backend

## 技术栈

- **Java**: 17
- **Spring Boot**: 3.2.0
- **MyBatis-Plus**: 3.5.5
- **Database**: openGauss (via PostgreSQL driver)
- **Build Tool**: Maven

## 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/carsales/
│   │   │       ├── config/          # 配置类
│   │   │       ├── controller/      # 控制器
│   │   │       ├── service/         # 服务层
│   │   │       ├── mapper/          # 数据访问层
│   │   │       ├── entity/          # 实体类
│   │   │       ├── dto/             # 数据传输对象
│   │   │       ├── vo/              # 视图对象
│   │   │       ├── common/          # 公共类
│   │   │       ├── exception/       # 异常处理
│   │   │       ├── handler/         # 处理器
│   │   │       └── CarSalesApplication.java
│   │   └── resources/
│   │       ├── mapper/              # MyBatis XML映射文件
│   │       ├── application.yml      # 主配置文件
│   │       ├── application-dev.yml  # 开发环境配置
│   │       └── application-prod.yml # 生产环境配置
│   └── test/
│       └── java/
│           └── com/carsales/        # 测试类
└── pom.xml
```

## 快速开始

### 前置要求

- JDK 17+
- Maven 3.6+
- openGauss 或 PostgreSQL 数据库

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE car_sales_db;
```

2. 执行初始化脚本：
```bash
psql -U postgres -d car_sales_db -f ../database/init.sql
```

3. 修改配置文件 `src/main/resources/application-dev.yml` 中的数据库连接信息

### 运行应用

开发环境：
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

生产环境：
```bash
mvn clean package
java -jar target/car-sales-system-1.0.0.jar --spring.profiles.active=prod
```

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行单元测试
mvn test -Dtest=*Test

# 运行属性测试
mvn test -Dtest=*PropertyTest
```

## API文档

应用启动后，访问：
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- API Docs: http://localhost:8080/api/v3/api-docs

## 配置说明

### 环境配置

- `application.yml`: 主配置文件，包含通用配置
- `application-dev.yml`: 开发环境配置
- `application-prod.yml`: 生产环境配置

### 主要配置项

- **数据库连接**: `spring.datasource.*`
- **MyBatis-Plus**: `mybatis-plus.*`
- **JWT**: `jwt.*`
- **CORS**: `cors.*`
- **日志**: `logging.*`

## 核心功能

- ✅ 统一响应结果封装 (Result<T>)
- ✅ 全局异常处理
- ✅ CORS跨域支持
- ✅ 雪花算法ID生成
- ✅ 字段自动填充 (create_time, update_time)
- ⏳ JWT认证授权
- ⏳ 用户管理
- ⏳ 车辆管理
- ⏳ 客户管理
- ⏳ 订单管理
- ⏳ 统计报表

## 开发规范

### 代码规范

- 使用Lombok简化代码
- 遵循阿里巴巴Java开发手册
- 使用MyBatis-Plus进行数据库操作
- 所有API返回统一的Result<T>格式

### 测试规范

- 单元测试覆盖率 > 80%
- 每个属性测试至少100次迭代
- 使用Jqwik进行属性测试
- 使用Testcontainers进行集成测试

## 许可证

本项目仅用于课程设计学习目的。
