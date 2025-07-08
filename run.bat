@echo off
echo 开始构建和运行智能健身教练应用...

REM 检查设备连接
echo 检查设备连接...
adb devices | findstr "device$" >nul
if errorlevel 1 (
    echo 错误：未找到已连接的设备
    pause
    exit /b 1
)

REM 清理并构建
echo 清理项目...
call gradlew clean

echo 构建Debug版本...
call gradlew assembleDebug

REM 安装应用
echo 安装应用到设备...
call gradlew installDebug

REM 启动应用
echo 启动应用...
adb shell am start -n cn.skstudio.fitness/.MainActivity

echo 应用启动完成！
pause 