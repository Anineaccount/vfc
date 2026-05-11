# 项目分析与上下文参考（Virtual Fitness Coach Android）

> 状态：上下文参考文件（后续会话建议优先读取）
> 
> 更新时间：2026-04-08

## 1. 目标与定位
- 项目类型：单模块 Android App（`app`）
- 核心目标：通过 CameraX + ML Kit Pose Detection 实时识别用户姿态，并基于角度规则给出动作评分与反馈
- 主要场景：深蹲、俯卧撑、平板支撑的实时动作引导

## 2. 技术栈与构建信息
- 语言与UI：Kotlin + Jetpack Compose + Material 3
- 架构风格：分层目录（`data/domain/presentation/utils`），接近 Clean + MVVM 思路
- DI：Hilt（已接入 `@HiltAndroidApp` / `@AndroidEntryPoint`）
- 视觉链路：CameraX + Google ML Kit Pose Detection
- 构建：AGP `8.13.0`，Kotlin `2.2.20`，`compileSdk/targetSdk=36`，`minSdk=24`

关键配置文件：
- `settings.gradle.kts`
- `build.gradle.kts`
- `app/build.gradle.kts`
- `gradle/libs.versions.toml`
- `app/src/main/AndroidManifest.xml`

## 3. 模块与目录地图
- 应用入口
  - `app/src/main/java/cn/skstudio/fitness/FitnessApplication.kt`
  - `app/src/main/java/cn/skstudio/fitness/MainActivity.kt`
- 导航与页面
  - `app/src/main/java/cn/skstudio/fitness/presentation/navigation/FitnessNavigation.kt`
  - `app/src/main/java/cn/skstudio/fitness/presentation/exercise/ExerciseListScreen.kt`
  - `app/src/main/java/cn/skstudio/fitness/presentation/exercise/ExerciseDetailScreen.kt`
  - `app/src/main/java/cn/skstudio/fitness/presentation/exercise/ExerciseScreen.kt`
- 相机与姿态绘制
  - `app/src/main/java/cn/skstudio/fitness/presentation/camera/CameraPreview.kt`
  - `app/src/main/java/cn/skstudio/fitness/presentation/camera/PoseOverlay.kt`
- 业务与数据
  - `app/src/main/java/cn/skstudio/fitness/domain/model/Exercise.kt`
  - `app/src/main/java/cn/skstudio/fitness/domain/model/PosePoint.kt`
  - `app/src/main/java/cn/skstudio/fitness/domain/usecase/ExerciseAnalyzer.kt`
  - `app/src/main/java/cn/skstudio/fitness/domain/usecase/PoseDetector.kt`
  - `app/src/main/java/cn/skstudio/fitness/data/repository/ExerciseRepository.kt`
  - `app/src/main/java/cn/skstudio/fitness/data/repository/MLKitPoseDetector.kt`
- 工具类
  - `app/src/main/java/cn/skstudio/fitness/utils/AngleCalculator.kt`

## 4. 核心运行链路（重要）
1. `MainActivity` 载入 Compose，并挂载 `FitnessNavigation`
2. 用户在动作列表选择动作 -> 进入详情 -> 进入练习页 `ExerciseScreen`
3. `ExerciseScreen` 请求相机权限，创建 `MLKitPoseDetector` 与 `ExerciseAnalyzer`
4. `CameraPreview` 通过 CameraX `ImageAnalysis` 持续提供帧
5. `MLKitPoseDetector.processImageProxy()` 调用 ML Kit 检测关键点并输出 `PosePoint` 列表（`Flow`）
6. `ExerciseAnalyzer.analyzePose()` 对照 `Exercise.standardPoseSequence` 进行角度匹配打分
7. UI 显示评分与文案反馈，同时 `PoseOverlay` 绘制骨架和关键点

## 5. 关键模型与算法摘要
- `PosePoint`：关键点类型、坐标、置信度
- `Exercise`：动作定义（类型、描述、目标肌群、标准姿态序列、常见错误）
- `AngleRequirement`：每个关节的目标角度区间
- `ExerciseAnalyzer`：
  - 将实时关键点映射为 `Map<PoseLandmarkType, PosePoint>`
  - 对每个标准姿态计算各关节角度分数
  - 取最佳匹配姿态作为当前分值来源
  - 反馈阈值：>=0.9 优秀，>=0.7 良好，>=0.5 提示调整，否则大幅偏差

## 6. 当前实现状态（基于代码现状）
- 已完成
  - 相机预览 + 实时姿态检测 + 骨架绘制
  - 3个预置动作及规则化角度匹配
  - 列表/详情/练习完整导航流程
- 待完善
  - Hilt 已接入但多数对象仍在 Composable 内 `remember { ... }` 手动创建
  - `checkCommonMistakes()` 当前返回空列表（错误动作检测尚未落地）
  - 测试仍为模板用例（无核心算法和流程自动化测试）

## 7. 风险与注意事项
- 坐标归一化风险：`MLKitPoseDetector` 当前直接写入 `landmark.position.x/y`，而 `PoseOverlay` 按 0-1 比例绘制；若坐标为像素值会导致覆盖层定位偏差
- 性能与资源：`ExerciseScreen` 中 Flow 收集 + Camera 分析并行，需关注中低端机发热与帧率
- 线程模型：`CameraPreview.update` 中重复触发 `startCamera()` 的频率需关注，避免重复绑定风险
- 数据与备份：`backup_rules.xml` / `data_extraction_rules.xml` 仍为模板内容

## 8. 测试与质量现状
- 单测：`app/src/test/java/cn/skstudio/fitness/ExampleUnitTest.kt`（模板）
- 仪器测试：`app/src/androidTest/java/cn/skstudio/fitness/ExampleInstrumentedTest.kt`（模板）
- 结论：当前自动化测试覆盖几乎为空，后续改动建议先补核心分析逻辑单测

## 9. 多语言与资源
- `app_name` 已提供：默认英文 + `values-zh` + `values-zh-rCN`
- 其余业务文案主要直接写在 Kotlin 代码中，尚未统一抽取到 `strings.xml`

## 10. 后续会话建议使用方式
- 若是功能开发：先看“第4节核心运行链路”+“第5节关键模型”
- 若是修Bug：优先核对“第7节风险与注意事项”
- 若是重构：先处理依赖注入一致性（第6节）再补测试（第8节）

---

## 快速索引（符号级）
- 入口：`MainActivity`、`FitnessApplication`
- 导航：`FitnessNavigation`、`FitnessRoutes`
- 检测实现：`MLKitPoseDetector`
- 分析实现：`ExerciseAnalyzer`
- 相机管线：`CameraPreview.startCamera`
- 绘制：`PoseOverlay.drawBodyConnections`
- 数据源：`ExerciseRepository`
- 角度计算：`AngleCalculator.calculateAngle`

