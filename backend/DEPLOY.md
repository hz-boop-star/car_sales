# 后端部署指南

## 📦 服务器目录建议

推荐的服务器目录结构：

```
/opt/car-sales/          # 或 /home/your-user/apps/car-sales/
├── backend/             # 后端应用（你上传的目录）
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── .env            # 环境配置
│   ├── data/           # SQLite 数据库文件
│   ├── logs/           # 应用日志
│   └── ...
└── frontend/           # 前端应用（可选，如果也部署在同一台服务器）
```

常见目录选择：
- `/opt/car-sales/backend` - 系统级应用（需要 root 权限）
- `/home/your-user/car-sales/backend` - 用户级应用（推荐）
- `/var/www/car-sales/backend` - Web 应用目录

## 🚀 部署步骤

### 1. 上传文件到服务器

```bash
# 方式一：使用 scp
scp -r backend/ user@your-server:/home/user/car-sales/

# 方式二：使用 rsync（推荐，支持断点续传）
rsync -avz --progress backend/ user@your-server:/home/user/car-sales/backend/

# 方式三：使用 FTP/SFTP 工具（如 FileZilla）
```

### 2. 连接到服务器

```bash
ssh user@your-server
cd /home/user/car-sales/backend
```

### 3. 配置环境变量

```bash
# 复制环境变量模板
cp .env.docker .env

# 编辑配置（使用 vim 或 nano）
vim .env
```

**SQLite 模式（开发/测试）：**
```env
SPRING_PROFILE=dev
DB_FILE=./data/car_sales.db
JWT_SECRET=car-sales-system-secret-key-2024
CORS_ORIGINS=*
```

**openGauss 模式（生产）：**
```env
SPRING_PROFILE=prod
DB_HOST=124.70.48.79
DB_PORT=26000
DB_NAME=car_sales_db
DB_USERNAME=admin_navicat
DB_PASSWORD=BigData@123
JWT_SECRET=your-super-secure-random-key-here
CORS_ORIGINS=http://your-domain.com
```

### 4. 部署应用

**方式一：使用部署脚本（推荐）**
```bash
chmod +x deploy.sh
./deploy.sh
```

**方式二：手动部署**
```bash
# 创建必要目录
mkdir -p data logs

# 构建镜像
docker-compose build

# 启动容器
docker-compose up -d

# 查看日志
docker-compose logs -f
```

### 5. 验证部署

```bash
# 检查容器状态
docker-compose ps

# 测试健康检查接口
curl http://localhost:8080/health

# 查看实时日志
docker-compose logs -f backend
```

## 🔧 常用管理命令

```bash
# 查看容器状态
docker-compose ps

# 查看日志（实时）
docker-compose logs -f

# 查看最近 100 行日志
docker-compose logs --tail=100

# 重启服务
docker-compose restart

# 停止服务
docker-compose stop

# 启动服务
docker-compose start

# 停止并删除容器
docker-compose down

# 重新构建并启动
docker-compose up -d --build

# 进入容器内部
docker-compose exec backend sh
```

## 🔄 更新应用

```bash
# 1. 上传新代码到服务器

# 2. 重新构建并启动
docker-compose up -d --build

# 3. 查看日志确认启动成功
docker-compose logs -f
```

## 🌐 配置外网访问

### 开放防火墙端口

**CentOS/RHEL (firewalld):**
```bash
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

**Ubuntu/Debian (ufw):**
```bash
sudo ufw allow 8080/tcp
sudo ufw reload
```

**云服务器：**
- 阿里云/腾讯云：在控制台的"安全组"中添加 8080 端口规则
- AWS：在 Security Group 中添加 Inbound Rule

### 使用 Nginx 反向代理（推荐）

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 🐛 故障排查

### 容器无法启动
```bash
# 查看详细日志
docker-compose logs backend

# 检查端口占用
netstat -tlnp | grep 8080

# 检查磁盘空间
df -h
```

### 数据库连接失败
```bash
# 检查环境变量
docker-compose exec backend env | grep DB

# SQLite：检查 data 目录权限
ls -la data/

# openGauss：测试网络连接
telnet 124.70.48.79 26000
```

### 内存不足
```bash
# 调整 JVM 内存（编辑 docker-compose.yml）
JAVA_OPTS=-Xms128m -Xmx256m -XX:+UseG1GC
```

## 📊 监控和维护

### 查看资源占用
```bash
# 容器资源使用情况
docker stats car-sales-backend

# 磁盘使用
du -sh data/ logs/
```

### 日志清理
```bash
# 清理旧日志（保留最近 7 天）
find logs/ -name "*.log" -mtime +7 -delete

# 或者在 application-prod.yml 中配置日志滚动
```

### 数据备份
```bash
# SQLite 数据库备份
cp data/car_sales.db data/car_sales_backup_$(date +%Y%m%d).db

# 定时备份（添加到 crontab）
0 2 * * * cd /home/user/car-sales/backend && cp data/car_sales.db data/backup_$(date +\%Y\%m\%d).db
```

## 🔐 安全建议

1. **修改默认密钥**：生产环境务必修改 `JWT_SECRET`
2. **限制 CORS**：不要使用 `*`，指定具体的前端域名
3. **使用 HTTPS**：配置 SSL 证书（通过 Nginx）
4. **定期更新**：及时更新 Docker 镜像和依赖
5. **备份数据**：定期备份数据库文件

## 📞 技术支持

如遇问题，请检查：
1. Docker 和 Docker Compose 版本
2. 服务器防火墙配置
3. 容器日志：`docker-compose logs -f`
4. 应用日志：`logs/car-sales-system.log`
