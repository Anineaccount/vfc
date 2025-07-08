#!/bin/bash
echo "=== 智能健身教练应用测试 ==="

# 测试1：构建
echo "1. 测试构建..."
./gradlew assembleDebug
if [ $? -eq 0 ]; then
    echo "✓ 构建成功"
else
    echo "✗ 构建失败"
    exit 1
fi

# 测试2：安装
echo "2. 测试安装..."
./gradlew installDebug
if [ $? -eq 0 ]; then
    echo "✓ 安装成功"
else
    echo "✗ 安装失败"
    exit 1
fi

# 测试3：启动
echo "3. 测试启动..."
adb shell am start -n cn.skstudio.fitness/.MainActivity
sleep 3

# 测试4：检查进程
if adb shell ps | grep -q cn.skstudio.fitness; then
    echo "✓ 应用运行正常"
else
    echo "✗ 应用启动失败"
fi

echo "=== 测试完成 ===" 