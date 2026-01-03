#!/bin/bash
# ============================================================================
# 汽车销售系统后端部署脚本
# ============================================================================

echo "=========================================="
echo "汽车销售系统后端 - Docker 部署"
echo "=========================================="

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ 错误：未检测到 Docker，请先安装 Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "❌ 错误：未检测到 Docker Compose，请先安装"
    exit 1
fi

# 创建必要的目录
echo "📁 创建数据和日志目录..."
mkdir -p data logs

# 检查 .env 文件
if [ ! -f .env ]; then
    echo "📝 创建 .env 配置文件..."
    cp .env.docker .env
    echo "✅ 已创建 .env 文件，请根据需要修改配置"
else
    echo "✅ .env 文件已存在"
fi

# 构建并启动容器
echo ""
echo "🚀 开始构建 Docker 镜像..."
docker-compose build

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 镜像构建成功！"
    echo "🚀 启动容器..."
    docker-compose up -d
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "=========================================="
        echo "✅ 部署成功！"
        echo "=========================================="
        echo "服务地址: http://localhost:8080"
        echo "健康检查: http://localhost:8080/health"
        echo ""
        echo "常用命令："
        echo "  查看日志: docker-compose logs -f"
        echo "  停止服务: docker-compose stop"
        echo "  重启服务: docker-compose restart"
        echo "  删除容器: docker-compose down"
        echo "=========================================="
    else
        echo "❌ 容器启动失败，请检查日志"
        exit 1
    fi
else
    echo "❌ 镜像构建失败，请检查错误信息"
    exit 1
fi
