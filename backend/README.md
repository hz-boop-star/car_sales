# 汽车销售管理系统 - 后端

Car Sales Management System - Backend

## 技术栈

- **Java**: 17
- **Spring Boot**: 3.2.0
- **MyBatis-Plus**: 3.5.10.1
- **Database**: SQLite (开发) / openGauss (生产) - 支持多环境切换
- **Build Tool**: Maven

## 多环境支持

项目支持两种数据库环境，可随时切换：

| 环境 | 数据库 | 用途 | 启动方式 |
|------|--------|------|----------|
| **dev** | SQLite | 开发/测试 | `start-dev.cmd` |
| **prod** | openGauss/PostgreSQL | 生产部署 | `start-prod.cmd` |

详细配置说明见 [SQLITE_MIGRATION.md](SQLITE_MIGRATION.md)

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
- **数据库**: 
  - 开发环境：无需安装（使用 SQLite）
  - 生产环境：openGauss 或 PostgreSQL

### 环境配置

**开发环境（SQLite，推荐）**

无需配置，直接启动即可！首次运行自动创建数据库和测试数据。

**生产环境（openGauss）**

1. 复制配置模板：
```bash
copy .env.prod .env
```

2. 修改 `.env` 中的数据库连接信息：
```bash
DB_HOST=your_host
DB_PORT=26000
DB_NAME=car_sales_db
DB_USERNAME=your_username
DB_PASSWORD=your_password
SPRING_PROFILE=prod
```

3. 手动执行数据库初始化脚本：
```bash
psql -U username -d car_sales_db -f ../database/init.sql
```

### 运行应用

**开发环境（SQLite）**
```bash
# 方式一：使用脚本（推荐）
start-dev.cmd

# 方式二：Maven 命令
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

**生产环境（openGauss）**
```bash
# 方式一：使用脚本
start-prod.cmd

# 方式二：Maven 命令
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod

# 方式三：打包运行
mvnw.cmd clean package
java -jar target/car-sales-system-1.0.0.jar --spring.profiles.active=prod
```

应用启动后访问：http://localhost:8080

**环境切换**

只需修改 `.env` 文件中的 `SPRING_PROFILE`：
- `SPRING_PROFILE=dev` - 使用 SQLite
- `SPRING_PROFILE=prod` - 使用 openGauss

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行单元测试
mvn test -Dtest=*Test

# 运行属性测试
mvn test -Dtest=*PropertyTest
```

## 测试账号

应用启动后可使用以下账号登录：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| sales001 | 123456 | 销售员 |
| sales002 | 123456 | 销售员 |
| sales003 | 123456 | 销售员 |

## API文档

应用启动后，访问：
- API 接口: http://localhost:8080/api/*
- 健康检查: http://localhost:8080/actuator/health

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
