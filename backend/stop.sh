#!/bin/bash
# ============================================================================
# 停止应用脚本
# ============================================================================

if [ -f app.pid ]; then
    PID=$(cat app.pid)
    echo "停止应用 (PID: $PID)..."
    kill $PID
    rm app.pid
    echo "✅ 应用已停止"
else
    echo "⚠️  未找到 PID 文件，尝试查找进程..."
    PID=$(ps aux | grep 'car-sales-system' | grep -v grep | awk '{print $2}')
    if [ -n "$PID" ]; then
        echo "找到进程 PID: $PID"
        kill $PID
        echo "✅ 应用已停止"
    else
        echo "❌ 未找到运行中的应用"
    fi
fi
