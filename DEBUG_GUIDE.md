# 智能健身教练 - 高级调试指南

## 调试技巧

### 1. 日志调试
在关键位置添加日志输出：

```kotlin
import android.util.Log

class MLKitPoseDetector {
    private fun processImageProxy(imageProxy: ImageProxy) {
        Log.d("PoseDetection", "开始处理图像帧")
        // 处理逻辑
        Log.d("PoseDetection", "检测到 ${posePoints.size} 个关键点")
    }
}
```

### 2. 相机权限调试
检查权限是否正确授予：

```kotlin
// 在ExerciseScreen中添加权限状态日志
val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
LaunchedEffect(cameraPermissionState.status) {
    Log.d("CameraPermission", "权限状态: ${cameraPermissionState.status}")
}
```

### 3. 姿态检测调试
在PoseOverlay中添加调试信息：

```kotlin
@Composable
fun PoseOverlay(posePoints: List<PosePoint>) {
    // 添加调试信息
    Text(
        text = "检测到 ${posePoints.size} 个关键点",
        modifier = Modifier.padding(16.dp),
        color = Color.White
    )
}
```

### 4. 调试模式运行
```bash
# 启用调试日志
adb shell setprop log.tag.PoseDetection DEBUG

# 运行应用并查看日志
./gradlew installDebug && \
adb logcat -c && \
adb shell am start -n cn.skstudio.fitness/.MainActivity && \
adb logcat | grep -E "(PoseDetection|CameraPermission|FitnessApp)"
```

### 5. 性能监控运行
```bash
# 安装并启动应用
./gradlew installDebug && \
adb shell am start -n cn.skstudio.fitness/.MainActivity

# 监控CPU使用率
adb shell top -p $(adb shell pidof cn.skstudio.fitness)

# 监控内存使用
adb shell dumpsys meminfo cn.skstudio.fitness
```

## 常见问题解决

### 1. Gradle同步失败
**问题**: 依赖下载失败或版本冲突
**解决方案**:
```bash
# 清理Gradle缓存
./gradlew clean
rm -rf ~/.gradle/caches/

# 重新同步
./gradlew build
```

### 2. Hilt编译错误
**问题**: 找不到生成的Hilt代码
**解决方案**:
1. 确保在Application类上添加了`@HiltAndroidApp`
2. 确保在Activity上添加了`@AndroidEntryPoint`
3. 重新构建项目

### 3. 相机权限问题
**问题**: 应用无法访问相机
**解决方案**:
1. 检查AndroidManifest.xml中的权限声明
2. 在设备设置中手动授予相机权限
3. 重启应用

### 4. ML Kit初始化失败
**问题**: 姿态检测无法正常工作
**解决方案**:
1. 检查网络连接（首次使用需要下载模型）
2. 确保设备支持ML Kit
3. 查看Logcat中的错误信息

### 5. 性能问题
**问题**: 应用运行缓慢或卡顿
**解决方案**:
1. 降低图像分析分辨率
2. 使用快速模式而非高精度模式
3. 优化UI更新频率

### 6. 编译错误
**问题**: Kotlin编译失败
**解决方案**:
```bash
# 清理项目
./gradlew clean

# 重新构建
./gradlew assembleDebug --stacktrace

# 检查Kotlin版本兼容性
./gradlew dependencies
```

### 7. 依赖冲突
**问题**: 库版本冲突
**解决方案**:
```bash
# 查看依赖树
./gradlew app:dependencies

# 强制使用特定版本
implementation("com.google.mlkit:pose-detection:18.0.0-beta4") {
    force = true
}
```

## 高级调试命令

### 1. 应用信息查看
```bash
# 查看应用信息
adb shell dumpsys package cn.skstudio.fitness

# 查看应用权限
adb shell dumpsys package cn.skstudio.fitness | grep permission

# 查看应用进程
adb shell ps | grep cn.skstudio.fitness
```

### 2. 性能分析
```bash
# CPU使用率
adb shell top -p $(adb shell pidof cn.skstudio.fitness)

# 内存使用
adb shell dumpsys meminfo cn.skstudio.fitness

# 电池使用
adb shell dumpsys batterystats cn.skstudio.fitness
```

### 3. 网络调试
```bash
# 查看网络连接
adb shell netstat

# 查看DNS解析
adb shell nslookup google.com

# 测试网络连通性
adb shell ping 8.8.8.8
```

### 4. 文件操作
```bash
# 查看应用数据目录
adb shell ls -la /data/data/cn.skstudio.fitness/

# 导出应用数据
adb pull /data/data/cn.skstudio.fitness/ ./app_data/

# 查看应用缓存
adb shell ls -la /data/data/cn.skstudio.fitness/cache/
```

### 5. 截图和录制
```bash
# 截图
adb shell screencap /sdcard/screenshot.png
adb pull /sdcard/screenshot.png

# 录制屏幕
adb shell screenrecord /sdcard/record.mp4
# 按 Ctrl+C 停止录制
adb pull /sdcard/record.mp4
```

## 测试建议

### 1. 功能测试
- [ ] 应用启动正常
- [ ] 权限请求正常工作
- [ ] 相机预览显示正常
- [ ] 姿态检测准确
- [ ] 动作分析正确
- [ ] 反馈信息显示正常

### 2. 性能测试
- [ ] 应用启动时间 < 3秒
- [ ] 相机预览延迟 < 100ms
- [ ] 姿态检测延迟 < 200ms
- [ ] 内存使用 < 200MB
- [ ] CPU使用率 < 30%

### 3. 兼容性测试
- [ ] 不同Android版本
- [ ] 不同屏幕尺寸
- [ ] 不同设备性能
- [ ] 横竖屏切换

### 4. 压力测试
```bash
# 创建压力测试脚本 stress_test.sh
#!/bin/bash
echo "开始压力测试..."

for i in {1..10}; do
    echo "测试轮次 $i"
    
    # 启动应用
    adb shell am start -n cn.skstudio.fitness/.MainActivity
    
    # 等待5秒
    sleep 5
    
    # 停止应用
    adb shell am force-stop cn.skstudio.fitness
    
    # 等待2秒
    sleep 2
done

echo "压力测试完成"
```

## 开发工具推荐

### 1. Android Studio插件
- **Layout Inspector**: 检查UI布局
- **Profiler**: 性能分析
- **Logcat**: 日志查看
- **Device File Explorer**: 文件管理

### 2. 调试工具
- **Chrome DevTools**: 调试WebView
- **adb**: 命令行调试
- **Android Studio Profiler**: 性能监控

### 3. 第三方工具
- **Stetho**: Facebook的网络调试工具
- **LeakCanary**: 内存泄漏检测
- **BlockCanary**: 主线程阻塞检测

## 自动化测试脚本

### 1. 完整测试脚本
```bash
#!/bin/bash
# complete_test.sh

set -e

echo "=== 完整测试流程 ==="

# 环境检查
echo "1. 环境检查..."
java -version
./gradlew --version

# 代码检查
echo "2. 代码检查..."
./gradlew ktlintCheck
./gradlew detekt

# 单元测试
echo "3. 单元测试..."
./gradlew test

# 构建测试
echo "4. 构建测试..."
./gradlew assembleDebug

# 安装测试
echo "5. 安装测试..."
./gradlew installDebug

# 启动测试
echo "6. 启动测试..."
adb shell am start -n cn.skstudio.fitness/.MainActivity
sleep 5

# 功能测试
echo "7. 功能测试..."
# 这里可以添加更多的自动化测试

echo "=== 测试完成 ==="
```

### 2. 性能测试脚本
```bash
#!/bin/bash
# performance_test.sh

echo "=== 性能测试 ==="

# 启动应用
adb shell am start -n cn.skstudio.fitness/.MainActivity

# 监控性能指标
echo "监控性能指标..."
adb shell top -p $(adb shell pidof cn.skstudio.fitness) -n 10

# 内存使用
echo "内存使用情况..."
adb shell dumpsys meminfo cn.skstudio.fitness

echo "=== 性能测试完成 ==="
```

## 发布准备

### 1. 代码优化
- 移除调试日志
- 优化性能瓶颈
- 检查内存泄漏
- 完善错误处理

### 2. 测试验证
- 完整功能测试
- 性能压力测试
- 兼容性测试
- 用户体验测试

### 3. 打包发布
```bash
# 生成Release APK
./gradlew assembleRelease

# 生成AAB (推荐)
./gradlew bundleRelease
```

## 联系支持

如果在调试过程中遇到问题，请：
1. 查看Logcat日志
2. 检查错误堆栈
3. 搜索GitHub Issues
4. 提交新的Issue并附上详细错误信息

---

**注意**: 调试时请确保在安全的环境中进行，避免在公共场合测试相机功能。

## 快速开始

如果这是您第一次使用，请先查看 [快速开始指南](QUICK_START.md) 