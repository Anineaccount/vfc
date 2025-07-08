@echo off
echo === 智能健身教练应用测试 ===

REM 测试1：构建
echo 1. 测试构建...
call gradlew assembleDebug
if errorlevel 1 (
    echo ✗ 构建失败
    pause
    exit /b 1
) else (
    echo ✓ 构建成功
)

REM 测试2：安装
echo 2. 测试安装...
call gradlew installDebug
if errorlevel 1 (
    echo ✗ 安装失败
    pause
    exit /b 1
) else (
    echo ✓ 安装成功
)

REM 测试3：启动
echo 3. 测试启动...
adb shell am start -n cn.skstudio.fitness/.MainActivity
timeout /t 3 /nobreak >nul

REM 测试4：检查进程
adb shell ps | findstr cn.skstudio.fitness >nul
if errorlevel 1 (
    echo ✗ 应用启动失败
) else (
    echo ✓ 应用运行正常
)

echo === 测试完成 ===
pause 