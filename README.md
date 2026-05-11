# 智能健身教练 (Virtual Fitness Coach)

## 项目简介

智能健身教练是一款基于计算机视觉和深度学习技术的Android应用，通过手机摄像头实时捕捉用户的健身动作，提供专业的动作识别和指导反馈，帮助用户以更科学、安全的方式进行个人健身训练。

## 主要功能

### 1. 实时姿态检测
- 使用Google ML Kit的Pose Detection API
- 支持33个人体关键点的实时检测
- 提供高精度和快速两种检测模式

### 2. 动作识别与分析
- 预设多种标准健身动作（深蹲、俯卧撑、平板支撑等）
- 基于关节角度的动作匹配算法
- 实时计算动作准确度评分

### 3. 即时反馈指导
- 动作标准度百分比显示
- 实时语音和文字提示
- 常见错误动作纠正建议

### 4. 美观的用户界面
- Material Design 3设计风格
- 流畅的动画效果
- 直观的视觉反馈

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

## 快速开始

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- Android SDK 35
- 最低支持 Android 7.0 (API 24)
- 支持前置摄像头的Android设备

### 构建运行
1. 克隆项目到本地
```bash
git clone https://github.com/yourusername/VirtualFitnessCoach.git
```

2. 使用Android Studio打开项目

3. 等待Gradle同步完成

4. 连接Android设备或启动模拟器

5. 运行应用
```bash
./gradlew installDebug
```

### 使用说明
1. 首次启动应用时，授予相机权限
2. 从动作列表中选择要练习的健身动作
3. 查看动作详情和要点说明
4. 点击"开始练习"进入实时检测界面
5. 将手机放置在合适位置，确保全身在画面中
6. 跟随指导进行动作练习，查看实时反馈

### 详细文档
- **[快速开始指南](QUICK_START.md)** - 5分钟上手，包含环境配置和基本操作
- **[高级调试指南](DEBUG_GUIDE.md)** - 调试技巧、问题解决和高级功能
- **[图标替换指南](ICON_REPLACEMENT_GUIDE.md)** - 应用图标替换方法，支持 Android Studio 和命令行两种方式
- **[项目分析与上下文参考](PROJECT_ANALYSIS.md)** - 项目架构、核心流程、风险点与后续会话参考入口

## 核心功能实现

### 姿态检测流程
1. CameraX捕获实时视频帧
2. ML Kit处理图像并检测人体关键点
3. 转换关键点坐标为标准化格式
4. 在预览画面上绘制骨架

### 动作分析算法
1. 计算关键关节的角度
2. 与标准动作的角度范围进行比对
3. 生成匹配分数和反馈建议
4. 检测常见错误动作模式

## 未来计划

- [ ] 添加更多健身动作类型
- [ ] 支持自定义训练计划
- [ ] 添加历史记录和进度追踪
- [ ] 集成语音指导功能
- [ ] 支持多人同时检测
- [ ] 添加社交分享功能

## 贡献指南

欢迎提交Issue和Pull Request来帮助改进项目。在提交PR之前，请确保：
1. 代码符合项目的编码规范
2. 添加必要的注释和文档
3. 通过所有的测试用例
4. 更新相关的README内容

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 联系方式

如有问题或建议，请通过以下方式联系：
- 提交GitHub Issue
- 发送邮件至：your-email@example.com

---

**注意**: 本应用仅供健身参考，不能替代专业健身教练的指导。使用时请注意安全，量力而行。
