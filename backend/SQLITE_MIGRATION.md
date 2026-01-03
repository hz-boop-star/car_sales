# SQLite 多环境配置说明

## 配置完成 ✅

现在支持两种数据库环境，可以随时切换：

### 📦 开发环境 - SQLite（默认）
- **优势**: 无需外部数据库，开箱即用
- **适用**: 本地开发、测试、演示

### 🚀 生产环境 - openGauss/PostgreSQL
- **优势**: 高性能、支持并发
- **适用**: 生产部署、多用户访问

---

## 快速使用

### 方式一：使用 SQLite（推荐开发）

```bash
# 1. 确保 .env 配置为 dev
SPRING_PROFILE=dev

# 2. 启动
start-dev.cmd
```

首次运行自动创建 `./data/car_sales.db` 并初始化数据。

### 方式二：使用 openGauss

```bash
# 1. 复制生产环境配置
copy .env.prod .env

# 2. 修改 .env 中的数据库连接信息
DB_HOST=your_host
DB_PORT=26000
DB_NAME=car_sales_db
DB_USERNAME=your_username
DB_PASSWORD=your_password
SPRING_PROFILE=prod

# 3. 启动
start-prod.cmd
```

---

## 环境切换

只需修改 `.env` 文件中的 `SPRING_PROFILE`：

```bash
# 使用 SQLite
SPRING_PROFILE=dev

# 使用 openGauss
SPRING_PROFILE=prod
```

或者使用命令行参数：
```bash
# SQLite
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

# openGauss
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 配置文件说明

| 文件 | 说明 |
|------|------|
| `application.yml` | 主配置，定义默认 profile |
| `application-dev.yml` | 开发环境（SQLite） |
| `application-prod.yml` | 生产环境（openGauss） |
| `.env` | 当前使用的环境变量 |
| `.env.example` | 配置模板 |
| `.env.prod` | 生产环境配置模板 |

---

## 数据初始化

### SQLite (dev)
- ✅ 自动执行 `schema.sql` 创建表
- ✅ 自动执行 `data.sql` 插入测试数据
- ✅ 每次启动都会重新初始化（开发方便）

### openGauss (prod)
- ❌ 不自动初始化（避免误删数据）
- 📝 需手动执行 `database/init.sql`

---

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| sales001 | 123456 | 销售员 |
| sales002 | 123456 | 销售员 |
| sales003 | 123456 | 销售员 |

---

## 常见问题

**Q: 如何重置 SQLite 数据库？**  
A: 删除 `./data/car_sales.db` 文件，重启应用即可。

**Q: 两个环境可以同时运行吗？**  
A: 可以，只要端口不冲突（修改 `SERVER_PORT`）。

**Q: 如何查看当前使用的环境？**  
A: 启动日志会显示 `The following profiles are active: dev` 或 `prod`。

**Q: SQLite 数据会丢失吗？**  
A: 不会，数据保存在 `./data/car_sales.db` 文件中。但因为配置了 `mode: always`，每次启动会重新执行初始化脚本。如需保留数据，改为 `mode: never`。
