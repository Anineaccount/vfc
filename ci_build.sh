#!/bin/bash
# ci_build.sh - 持续集成构建脚本

set -e  # 遇到错误立即退出

echo "开始CI构建流程..."

# 环境检查
echo "检查环境..."
java -version
./gradlew --version

# 代码检查
echo "运行代码检查..."
./gradlew ktlintCheck
./gradlew detekt

# 单元测试
echo "运行单元测试..."
./gradlew test

# 构建
echo "构建应用..."
./gradlew assembleDebug

# 安装测试
echo "安装到测试设备..."
./gradlew installDebug

# 启动测试
echo "启动应用测试..."
adb shell am start -n cn.skstudio.fitness/.MainActivity

echo "CI构建完成！" 