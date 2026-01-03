#!/bin/bash
# ============================================================================
# 服务器上直接运行 JAR 包脚本
# ============================================================================

JAR_FILE="car-sales-system-1.0.0.jar"
APP_NAME="car-sales-backend"

# 检查 JAR 文件
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ 错误：找不到 $JAR_FILE"
    echo "请先上传 JAR 包到当前目录"
    exit 1
fi

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "❌ 错误：未检测到 Java，请先安装 JDK 17"
    exit 1
fi

# 检查 .env 文件
if [ ! -f .env ]; then
    echo "📝 创建 .env 配置文件..."
    cp .env.docker .env
    echo "✅ 已创建 .env 文件"
fi

# 加载环境变量
export $(cat .env | grep -v '^#' | xargs)

# 创建必要目录
mkdir -p data logs

echo "=========================================="
echo "启动 $APP_NAME"
echo "=========================================="
echo "环境: $SPRING_PROFILE"
echo "端口: 8080"
echo "=========================================="

# 启动应用
nohup java -Xms256m -Xmx512m -XX:+UseG1GC \
    -Dspring.profiles.active=${SPRING_PROFILE:-dev} \
    -DDB_FILE=${DB_FILE} \
    -DDB_HOST=${DB_HOST} \
    -DDB_PORT=${DB_PORT} \
    -DDB_NAME=${DB_NAME} \
    -DDB_USERNAME=${DB_USERNAME} \
    -DDB_PASSWORD=${DB_PASSWORD} \
    -DJWT_SECRET=${JWT_SECRET} \
    -DJWT_EXPIRATION=${JWT_EXPIRATION} \
    -DCORS_ORIGINS=${CORS_ORIGINS} \
    -jar $JAR_FILE > logs/app.log 2>&1 &

PID=$!
echo $PID > app.pid

echo ""
echo "✅ 应用已启动！"
echo "PID: $PID"
echo "日志: tail -f logs/app.log"
echo ""
echo "停止应用: ./stop.sh"
