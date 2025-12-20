# 数据库连接配置说明

## 远程 openGauss 数据库信息

- **服务器地址**: 124.70.48.79
- **端口**: 26000
- **数据库名**: car_sales_db
- **数据库类型**: openGauss (使用 PostgreSQL 驱动)

## 配置方式

### 方式一：使用环境变量（推荐）

1. 复制 `.env.example` 为 `.env`：
```bash
cp .env.example .env
```

2. 编辑 `.env` 文件，填入实际的用户名和密码：
```properties
DB_USERNAME=your_actual_username
DB_PASSWORD=your_actual_password
```

3. 启动应用时加载环境变量：

**Windows (CMD)**:
```cmd
set DB_USERNAME=your_username
set DB_PASSWORD=your_password
mvnw.cmd spring-boot:run
```

**Windows (PowerShell)**:
```powershell
$env:DB_USERNAME="your_username"
$env:DB_PASSWORD="your_password"
.\mvnw.cmd spring-boot:run
```

**Linux/Mac**:
```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
./mvnw spring-boot:run
```

### 方式二：IDEA 配置环境变量

1. 打开 Run/Debug Configurations
2. 选择 Spring Boot 应用配置
3. 在 Environment variables 中添加：
   - `DB_USERNAME=your_username`
   - `DB_PASSWORD=your_password`

### 方式三：直接修改配置文件（不推荐，仅用于开发测试）

编辑 `application-dev.yml`，将环境变量替换为实际值：
```yaml
spring:
  datasource:
    username: your_actual_username
    password: your_actual_password
```

**注意**: 不要将包含真实密码的配置文件提交到 Git！

## 配置文件说明

### application-dev.yml (开发环境)
- 直连远程数据库 124.70.48.79
- 使用环境变量 `DB_USERNAME` 和 `DB_PASSWORD`
- 默认值为 `gaussdb` 和 `Gauss@123`（仅用于示例）
- 启用 SQL 日志输出

### application-prod.yml (生产环境)
- 完全使用环境变量配置
- 必须设置 `DB_USERNAME` 和 `DB_PASSWORD`
- 禁用 SQL 日志输出
- 优化连接池配置

## 测试数据库连接

启动应用后，检查日志输出：

```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

如果看到以上信息，说明数据库连接成功。

## 常见问题

### 1. 连接超时
- 检查服务器 124.70.48.79 的防火墙是否开放 26000 端口
- 检查 openGauss 是否允许远程连接
- 检查网络连接是否正常

### 2. 认证失败
- 确认用户名和密码正确
- 检查 openGauss 的 pg_hba.conf 配置
- 确认用户有访问 car_sales_db 数据库的权限

### 3. 驱动兼容性
- openGauss 兼容 PostgreSQL 协议
- 使用 PostgreSQL JDBC 驱动 (org.postgresql.Driver)
- 已在 pom.xml 中配置依赖

## 安全建议

1. ✅ 使用环境变量存储敏感信息
2. ✅ 不要将密码硬编码在配置文件中
3. ✅ 不要将 .env 文件提交到版本控制
4. ✅ 生产环境使用强密码
5. ✅ 定期更换数据库密码
6. ✅ 限制数据库用户权限（最小权限原则）

## 下一步

配置完成后，可以：

1. 启动后端服务：`mvnw.cmd spring-boot:run`
2. 测试 API 接口：访问 http://localhost:8080/api
3. 查看 Swagger 文档（如果已配置）
4. 运行单元测试验证数据库操作
