# ============================================================================
# 汽车销售管理系统 - 后端 Dockerfile
# 多阶段构建：构建阶段 + 运行阶段
# ============================================================================

# ===== 第一阶段：构建 =====
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# 先复制 pom.xml，利用 Docker 缓存加速依赖下载
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并构建
COPY src ./src
RUN mvn clean package -DskipTests -B

# ===== 第二阶段：运行 =====
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Car Sales System"
LABEL description="汽车销售管理系统后端服务"

WORKDIR /app

# 安装必要工具（时区设置等）
RUN apk add --no-cache tzdata su-exec \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

# 创建非 root 用户运行应用
RUN addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -D appuser

# 创建日志和数据目录
RUN mkdir -p /app/logs /app/data && chown -R appuser:appgroup /app

# 从构建阶段复制 jar 包
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 注意：不在这里切换用户，而是在启动脚本中处理权限后再切换

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# JVM 优化参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError"

# 启动命令（先确保目录权限，再以 appuser 运行）
ENTRYPOINT ["sh", "-c", "chown -R appuser:appgroup /app/logs /app/data 2>/dev/null || true && su-exec appuser java $JAVA_OPTS -jar app.jar"]
