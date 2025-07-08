package cn.skstudio.fitness.data.repository

import cn.skstudio.fitness.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 健身动作仓库
 * 提供预定义的健身动作数据和管理功能
 */
@Singleton
class ExerciseRepository @Inject constructor() {
    
    /**
     * 获取所有可用的健身动作
     */
    fun getAllExercises(): List<Exercise> {
        return listOf(
            createSquatExercise(),
            createPushUpExercise(),
            createPlankExercise()
        )
    }
    
    /**
     * 根据ID获取特定的健身动作
     */
    fun getExerciseById(id: String): Exercise? {
        return getAllExercises().find { it.id == id }
    }
    
    /**
     * 创建深蹲动作
     */
    private fun createSquatExercise(): Exercise {
        return Exercise(
            id = "squat",
            name = "深蹲",
            type = ExerciseType.STRENGTH,
            description = "深蹲是一种锻炼下肢肌肉的基础动作，主要锻炼大腿前侧的股四头肌和臀部肌肉。",
            targetMuscles = listOf("股四头肌", "臀大肌", "腘绳肌"),
            difficulty = 2,
            standardPoseSequence = listOf(
                StandardPose(
                    name = "起始位置",
                    keyAngles = listOf(
                        AngleRequirement(
                            jointName = "左膝",
                            point1 = PoseLandmarkType.LEFT_HIP,
                            vertex = PoseLandmarkType.LEFT_KNEE,
                            point2 = PoseLandmarkType.LEFT_ANKLE,
                            minAngle = 170f,
                            maxAngle = 180f
                        ),
                        AngleRequirement(
                            jointName = "右膝",
                            point1 = PoseLandmarkType.RIGHT_HIP,
                            vertex = PoseLandmarkType.RIGHT_KNEE,
                            point2 = PoseLandmarkType.RIGHT_ANKLE,
                            minAngle = 170f,
                            maxAngle = 180f
                        )
                    )
                ),
                StandardPose(
                    name = "下蹲位置",
                    keyAngles = listOf(
                        AngleRequirement(
                            jointName = "左膝",
                            point1 = PoseLandmarkType.LEFT_HIP,
                            vertex = PoseLandmarkType.LEFT_KNEE,
                            point2 = PoseLandmarkType.LEFT_ANKLE,
                            minAngle = 80f,
                            maxAngle = 100f
                        ),
                        AngleRequirement(
                            jointName = "右膝",
                            point1 = PoseLandmarkType.RIGHT_HIP,
                            vertex = PoseLandmarkType.RIGHT_KNEE,
                            point2 = PoseLandmarkType.RIGHT_ANKLE,
                            minAngle = 80f,
                            maxAngle = 100f
                        ),
                        AngleRequirement(
                            jointName = "左髋",
                            point1 = PoseLandmarkType.LEFT_SHOULDER,
                            vertex = PoseLandmarkType.LEFT_HIP,
                            point2 = PoseLandmarkType.LEFT_KNEE,
                            minAngle = 80f,
                            maxAngle = 100f
                        )
                    ),
                    duration = 1000
                )
            ),
            commonMistakes = listOf(
                ExerciseMistake(
                    id = "knee_over_toes",
                    description = "膝盖超过脚尖",
                    detectionCriteria = "膝盖位置超过脚尖垂直线",
                    correctionAdvice = "保持膝盖在脚尖正上方或略后，避免膝盖前倾过多"
                ),
                ExerciseMistake(
                    id = "back_round",
                    description = "背部弯曲",
                    detectionCriteria = "脊柱曲度过大",
                    correctionAdvice = "保持背部挺直，核心收紧，目视前方"
                )
            )
        )
    }
    
    /**
     * 创建俯卧撑动作
     */
    private fun createPushUpExercise(): Exercise {
        return Exercise(
            id = "pushup",
            name = "俯卧撑",
            type = ExerciseType.STRENGTH,
            description = "俯卧撑是锻炼上肢和核心肌群的经典动作，主要锻炼胸肌、三角肌和肱三头肌。",
            targetMuscles = listOf("胸大肌", "三角肌前束", "肱三头肌"),
            difficulty = 3,
            standardPoseSequence = listOf(
                StandardPose(
                    name = "起始位置",
                    keyAngles = listOf(
                        AngleRequirement(
                            jointName = "左肘",
                            point1 = PoseLandmarkType.LEFT_SHOULDER,
                            vertex = PoseLandmarkType.LEFT_ELBOW,
                            point2 = PoseLandmarkType.LEFT_WRIST,
                            minAngle = 170f,
                            maxAngle = 180f
                        ),
                        AngleRequirement(
                            jointName = "右肘",
                            point1 = PoseLandmarkType.RIGHT_SHOULDER,
                            vertex = PoseLandmarkType.RIGHT_ELBOW,
                            point2 = PoseLandmarkType.RIGHT_WRIST,
                            minAngle = 170f,
                            maxAngle = 180f
                        )
                    )
                ),
                StandardPose(
                    name = "下降位置",
                    keyAngles = listOf(
                        AngleRequirement(
                            jointName = "左肘",
                            point1 = PoseLandmarkType.LEFT_SHOULDER,
                            vertex = PoseLandmarkType.LEFT_ELBOW,
                            point2 = PoseLandmarkType.LEFT_WRIST,
                            minAngle = 80f,
                            maxAngle = 100f
                        ),
                        AngleRequirement(
                            jointName = "右肘",
                            point1 = PoseLandmarkType.RIGHT_SHOULDER,
                            vertex = PoseLandmarkType.RIGHT_ELBOW,
                            point2 = PoseLandmarkType.RIGHT_WRIST,
                            minAngle = 80f,
                            maxAngle = 100f
                        )
                    ),
                    duration = 500
                )
            ),
            commonMistakes = listOf(
                ExerciseMistake(
                    id = "hip_sag",
                    description = "臀部下沉",
                    detectionCriteria = "臀部低于肩膀和脚踝连线",
                    correctionAdvice = "保持身体呈一条直线，收紧核心肌群"
                ),
                ExerciseMistake(
                    id = "elbow_flare",
                    description = "手肘外展过大",
                    detectionCriteria = "手肘与身体夹角大于45度",
                    correctionAdvice = "手肘保持靠近身体，与躯干夹角约30-45度"
                )
            )
        )
    }
    
    /**
     * 创建平板支撑动作
     */
    private fun createPlankExercise(): Exercise {
        return Exercise(
            id = "plank",
            name = "平板支撑",
            type = ExerciseType.STRENGTH,
            description = "平板支撑是一种静态核心训练动作，可以有效锻炼腹肌、背肌和肩部稳定性。",
            targetMuscles = listOf("腹直肌", "腹横肌", "竖脊肌"),
            difficulty = 2,
            standardPoseSequence = listOf(
                StandardPose(
                    name = "标准姿势",
                    keyAngles = listOf(
                        AngleRequirement(
                            jointName = "左肘",
                            point1 = PoseLandmarkType.LEFT_SHOULDER,
                            vertex = PoseLandmarkType.LEFT_ELBOW,
                            point2 = PoseLandmarkType.LEFT_WRIST,
                            minAngle = 85f,
                            maxAngle = 95f
                        ),
                        AngleRequirement(
                            jointName = "右肘",
                            point1 = PoseLandmarkType.RIGHT_SHOULDER,
                            vertex = PoseLandmarkType.RIGHT_ELBOW,
                            point2 = PoseLandmarkType.RIGHT_WRIST,
                            minAngle = 85f,
                            maxAngle = 95f
                        ),
                        AngleRequirement(
                            jointName = "身体直线",
                            point1 = PoseLandmarkType.LEFT_SHOULDER,
                            vertex = PoseLandmarkType.LEFT_HIP,
                            point2 = PoseLandmarkType.LEFT_ANKLE,
                            minAngle = 170f,
                            maxAngle = 180f
                        )
                    ),
                    duration = 30000 // 30秒
                )
            ),
            commonMistakes = listOf(
                ExerciseMistake(
                    id = "hip_high",
                    description = "臀部抬得过高",
                    detectionCriteria = "臀部高于肩膀和脚踝连线",
                    correctionAdvice = "降低臀部位置，保持身体呈一条直线"
                ),
                ExerciseMistake(
                    id = "head_drop",
                    description = "头部下垂",
                    detectionCriteria = "头部低于肩膀水平线",
                    correctionAdvice = "保持头部与脊柱在同一直线上，目视地面"
                )
            )
        )
    }
} 