#!/bin/bash
# ============================================================================
# 本地构建 JAR 包脚本
# 在本地 Windows 上运行，然后把 jar 传到服务器
# ============================================================================

echo "开始构建 JAR 包..."

# Windows 使用 mvnw.cmd
./mvnw.cmd clean package -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 构建成功！"
    echo "JAR 包位置: target/car-sales-system-1.0.0.jar"
    echo ""
    echo "上传到服务器："
    echo "scp target/car-sales-system-1.0.0.jar user@server:/home/user/car-sales/backend/"
else
    echo "❌ 构建失败"
    exit 1
fi
