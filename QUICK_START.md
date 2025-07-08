# 智能健身教练 - 快速开始指南

## 环境要求

### 开发环境
- **Android Studio**: Hedgehog (2023.1.1) 或更高版本
- **JDK**: 版本 11 或更高
- **Android SDK**: API 35 (Android 15)
- **Gradle**: 8.0 或更高版本

### 硬件要求
- **测试设备**: 支持前置摄像头的Android设备（推荐真机测试）
- **最低系统**: Android 7.0 (API 24)
- **推荐系统**: Android 10.0 或更高版本

## 快速开始（5分钟上手）

### 1. 克隆项目
```bash
git clone <项目地址>
cd VirtualFitnessCoach
```

### 2. 检查设备连接
```bash
adb devices
```
确保显示 `device` 状态（不是 `unauthorized` 或 `offline`）

### 3. 一键运行

#### Windows用户
1. 连接Android设备并启用USB调试
2. 双击 `run.bat` 文件
3. 等待构建完成，应用自动启动

#### Linux/macOS用户
```bash
chmod +x run.sh
./run.sh
```

#### 命令行用户（所有平台）
```bash
# 一键构建并运行
./gradlew clean assembleDebug installDebug && adb shell am start -n cn.skstudio.fitness/.MainActivity
```

## 项目配置

### 1. 打开项目
1. 启动Android Studio
2. 选择 "Open an existing Android Studio project"
3. 选择项目根目录
4. 等待Gradle同步完成

### 2. 检查配置
确保以下文件配置正确：

#### build.gradle.kts (项目级)
```kotlin
plugins {
    id("com.google.dagger.hilt.android") version "2.48" apply false
}
```

#### app/build.gradle.kts
```kotlin
plugins {
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 35
    minSdk = 24
    targetSdk = 35
}
```

## 基本操作

### 编译检查
```bash
# 清理并编译（推荐，检查编译问题）
./gradlew clean assembleDebug

# 仅编译检查
./gradlew assembleDebug

# 编译并显示详细错误信息
./gradlew assembleDebug --stacktrace

# 编译并显示调试信息
./gradlew assembleDebug --info
```

### 构建项目
```bash
# Debug版本
./gradlew assembleDebug

# Release版本
./gradlew assembleRelease
```

### 安装应用
```bash
# 安装Debug版本
./gradlew installDebug

# 安装Release版本
./gradlew installRelease
```

### 启动应用
```bash
# 启动应用
adb shell am start -n cn.skstudio.fitness/.MainActivity

# 重启应用
adb shell am force-stop cn.skstudio.fitness && adb shell am start -n cn.skstudio.fitness/.MainActivity
```

### 查看日志
```bash
# 查看应用日志
adb logcat | grep cn.skstudio.fitness

# 清除日志
adb logcat -c
```

## 编译问题排查

### 常见编译问题检查
```bash
# 检查依赖冲突
./gradlew app:dependencies

# 检查Kotlin编译
./gradlew compileDebugKotlin

# 检查资源文件
./gradlew processDebugResources

# 重新同步项目
./gradlew build
```

### 编译成功标志
如果编译成功，您会看到：
```
BUILD SUCCESSFUL in 2m 30s
33 actionable tasks: 33 executed
```

### 推荐编译检查流程
1. **首次检查**：
```bash
./gradlew clean assembleDebug --stacktrace
```

2. **如果失败，查看具体错误**：
```bash
./gradlew assembleDebug --info
```

3. **检查依赖**：
```bash
./gradlew app:dependencies
```

4. **重新同步项目**：
```bash
./gradlew build
```

## 常见快速操作

### 检查编译问题
```bash
# 快速编译检查
./gradlew clean assembleDebug

# 仅重新安装（跳过构建）
./gradlew installDebug
```

### 仅启动应用
```bash
adb shell am start -n cn.skstudio.fitness/.MainActivity
```

### 重启应用
```bash
adb shell am force-stop cn.skstudio.fitness && adb shell am start -n cn.skstudio.fitness/.MainActivity
```

### 清除应用数据
```bash
adb shell pm clear cn.skstudio.fitness
```

### 卸载应用
```bash
adb uninstall cn.skstudio.fitness
```

## 使用说明

### 1. 首次启动
1. 应用启动后会自动请求相机权限
2. 点击"授予权限"按钮
3. 进入动作列表页面

### 2. 选择动作
1. 从列表中选择要练习的健身动作
2. 查看动作详情和要点说明
3. 点击"开始练习"按钮

### 3. 开始练习
1. 将手机放置在合适位置
2. 确保全身在画面中
3. 跟随指导进行动作练习
4. 查看实时反馈和评分

## 项目结构

```
app/src/main/java/cn/skstudio/fitness/
├── data/                      # 数据层
│   ├── model/                # 数据模型
│   └── repository/           # 数据仓库实现
├── domain/                    # 领域层
│   ├── model/                # 业务模型
│   └── usecase/              # 业务用例
├── presentation/              # 表现层
│   ├── camera/               # 相机相关组件
│   ├── exercise/             # 健身动作页面
│   └── navigation/           # 导航配置
├── utils/                     # 工具类
└── ui/                       # UI主题配置
```

## 技术栈

### 核心技术
- **编程语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构模式**: Clean Architecture + MVVM
- **依赖注入**: Hilt
- **导航**: Navigation Compose

### 视觉识别
- **姿态检测**: Google ML Kit Pose Detection
- **相机处理**: CameraX
- **图像分析**: TensorFlow Lite (预留接口)

### 其他依赖
- **异步处理**: Kotlin Coroutines + Flow
- **权限管理**: Accompanist Permissions
- **生命周期**: Lifecycle Components

## 预设健身动作

1. **深蹲** - 检测膝关节和髋关节角度
2. **俯卧撑** - 检测肘关节角度和身体直线度
3. **平板支撑** - 检测身体保持直线的姿态

## 下一步

如果遇到问题，请查看 [高级调试指南](DEBUG_GUIDE.md)

---

**注意**: 本应用仅供健身参考，不能替代专业健身教练的指导。使用时请注意安全，量力而行。 