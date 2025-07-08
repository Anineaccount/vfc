#!/bin/bash
echo "开始构建和运行智能健身教练应用..."

# 检查设备连接
echo "检查设备连接..."
if ! adb devices | grep -q "device$"; then
    echo "错误：未找到已连接的设备"
    exit 1
fi

# 清理并构建
echo "清理项目..."
./gradlew clean

echo "构建Debug版本..."
./gradlew assembleDebug

# 安装应用
echo "安装应用到设备..."
./gradlew installDebug

# 启动应用
echo "启动应用..."
adb shell am start -n cn.skstudio.fitness/.MainActivity

echo "应用启动完成！" 