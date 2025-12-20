# 数据库启动指南（openGauss）

## 前置条件

需要安装 **openGauss** 数据库。

### Windows 安装 openGauss

1. 下载：https://opengauss.org/zh/download/
2. 选择 Windows 版本（或使用 Docker）
3. 安装时记住设置的密码（默认用户是 `gaussdb` 或 `omm`）
4. 默认端口：5432

### 使用 Docker 快速启动（推荐）

```bash
# 拉取 openGauss 镜像
docker pull opengauss/opengauss:latest

# 启动容器
docker run --name opengauss ^
  -e GS_PASSWORD=Gauss@123 ^
  -p 5432:5432 ^
  -d opengauss/opengauss:latest

# 等待几秒让数据库启动完成
```

## 步骤 1：连接到 openGauss

```bash
# 如果使用 Docker
docker exec -it opengauss gsql -d postgres -U gaussdb -W Gauss@123

# 如果本地安装
gsql -d postgres -U gaussdb -W
```

## 步骤 2：创建数据库

在 gsql 中执行：

```sql
-- 创建数据库
CREATE DATABASE car_sales_db;

-- 切换到新数据库
\c car_sales_db

-- 退出
\q
```

## 步骤 3：执行初始化脚本

```bash
# 如果使用 Docker
docker exec -i opengauss gsql -d car_sales_db -U gaussdb -W Gauss@123 < init.sql

# 如果本地安装（在 database 目录下）
gsql -d car_sales_db -U gaussdb -W -f init.sql
```

**预期输出**：
- 创建表成功
- 创建索引成功
- 创建视图成功
- 创建存储过程成功
- 插入测试数据成功
- 最后显示统计信息

## 步骤 4：验证数据库

连接到数据库验证：

```bash
# Docker
docker exec -it opengauss gsql -d car_sales_db -U gaussdb -W Gauss@123

# 本地安装
gsql -d car_sales_db -U gaussdb -W
```

执行验证查询：

```sql
-- 查看所有表
\dt

-- 查看用户数据
SELECT * FROM sys_user;

-- 查看车辆数据
SELECT * FROM car_info WHERE status = 0 LIMIT 5;

-- 查看视图
SELECT * FROM v_sales_statistics;

-- 测试存储过程（创建一个订单）
SELECT * FROM proc_create_order(1002, 3004, 2001, 295000.00);

-- 验证车辆状态是否更新
SELECT id, vin, brand, status FROM car_info WHERE id = 2001;
```

## 步骤 5：配置后端连接

检查 `backend/src/main/resources/application.yml` 配置：

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/car_sales_db
    username: gaussdb  # openGauss 默认用户
    password: Gauss@123  # 改成你设置的密码
```

**重要**：openGauss 兼容 PostgreSQL 协议，所以使用 PostgreSQL 驱动即可！

## 步骤 6：启动后端

```bash
cd backend
mvn spring-boot:run
```

**预期输出**：
- Spring Boot 启动成功
- 端口 8080
- 看到 "Started CarSalesApplication"

## 步骤 7：测试接口

### 6.1 测试登录

```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"123456\"}"
```

**预期响应**：
```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGc...",
    "userInfo": {
      "id": 1001,
      "username": "admin",
      "realName": "系统管理员",
      "role": "ADMIN"
    }
  }
}
```

**保存 token**，后续请求需要用到！

### 6.2 测试查询车辆

```bash
# 替换 YOUR_TOKEN 为上一步获取的 token
curl -X GET "http://localhost:8080/api/cars?pageNum=1&pageSize=10" ^
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 6.3 测试创建订单（验证存储过程）

```bash
# 创建订单（使用在库车辆 ID: 2004）
curl -X POST http://localhost:8080/api/orders ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer YOUR_TOKEN" ^
  -d "{\"customerId\":3005,\"carId\":2004,\"actualPrice\":275000.00}"
```

**预期**：
- 订单创建成功
- 车辆状态从 0（在库）变为 2（已售）

### 6.4 验证车辆状态更新

```bash
curl -X GET http://localhost:8080/api/cars/2004 ^
  -H "Authorization: Bearer YOUR_TOKEN"
```

**预期**：status 应该是 2（已售）

## 常见问题

### 问题 1：gsql 命令不存在

**解决**：
- **Docker 方式**：使用 `docker exec -it opengauss gsql ...`
- **本地安装**：将 openGauss 的 bin 目录添加到系统 PATH

### 问题 2：密码认证失败

**解决**：
1. 检查密码是否正确（Docker 默认是 `Gauss@123`）
2. 修改 `application.yml` 中的用户名和密码：
   ```yaml
   username: gaussdb
   password: Gauss@123
   ```

### 问题 3：端口 5432 被占用

**解决**：
1. 检查是否已有数据库在运行
2. 或修改 Docker 端口映射：`-p 15432:5432`，同时修改 `application.yml` 中的端口

### 问题 4：后端启动失败 - 数据库连接错误

**解决**：
1. 确认 openGauss 已启动（Docker: `docker ps`）
2. 确认数据库名称、用户名、密码正确
3. 测试连接：
   ```bash
   docker exec -it opengauss gsql -d car_sales_db -U gaussdb -W Gauss@123
   ```

### 问题 5：Docker 容器无法启动

**解决**：
```bash
# 查看日志
docker logs opengauss

# 删除旧容器重新创建
docker rm -f opengauss
docker run --name opengauss -e GS_PASSWORD=Gauss@123 -p 5432:5432 -d opengauss/opengauss:latest
```

### 问题 5：存储过程调用失败

**检查**：
```sql
-- 查看存储过程是否存在
\df proc_create_order

-- 手动测试存储过程
SELECT * FROM proc_create_order(1002, 3004, 2001, 295000.00);
```

## 测试数据说明

### 默认用户（密码都是 123456）

| 用户名 | 角色 | 真实姓名 |
|--------|------|----------|
| admin | ADMIN | 系统管理员 |
| sales001 | SALESPERSON | 张三 |
| sales002 | SALESPERSON | 李四 |
| sales003 | SALESPERSON | 王五 |

### 测试车辆

- 在库车辆：ID 2001-2010（10辆）
- 已售车辆：ID 2011-2013（3辆，用于测试）

### 测试客户

- 客户 ID：3001-3010（10个客户）

## 下一步

数据库和后端都正常后，可以：
1. 继续开发前端页面
2. 或者先写一些集成测试

---

**遇到问题？** 把错误信息发给我，我帮你解决！
