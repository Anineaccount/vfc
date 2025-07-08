package cn.skstudio.fitness.domain.model

/**
 * 健身动作模型
 * 表示一个具体的健身动作及其相关信息
 */
data class Exercise(
    /**
     * 动作ID
     */
    val id: String,
    
    /**
     * 动作名称（如深蹲、俯卧撑等）
     */
    val name: String,
    
    /**
     * 动作类型
     */
    val type: ExerciseType,
    
    /**
     * 动作描述
     */
    val description: String,
    
    /**
     * 目标肌群列表
     */
    val targetMuscles: List<String>,
    
    /**
     * 难度等级（1-5）
     */
    val difficulty: Int,
    
    /**
     * 标准动作的关键姿态序列
     * 用于与用户动作进行比对
     */
    val standardPoseSequence: List<StandardPose>,
    
    /**
     * 常见错误列表
     */
    val commonMistakes: List<ExerciseMistake>
)

/**
 * 健身动作类型枚举
 */
enum class ExerciseType {
    STRENGTH,      // 力量训练
    CARDIO,        // 有氧运动
    FLEXIBILITY,   // 柔韧性训练
    BALANCE,       // 平衡训练
    COMPOUND       // 复合动作
}

/**
 * 标准姿态模型
 * 表示动作中的一个标准姿态
 */
data class StandardPose(
    /**
     * 姿态名称（如"起始位置"、"下蹲位置"等）
     */
    val name: String,
    
    /**
     * 该姿态的关键角度要求
     */
    val keyAngles: List<AngleRequirement>,
    
    /**
     * 持续时间（毫秒）
     */
    val duration: Long? = null
)

/**
 * 角度要求模型
 * 定义特定关节的角度范围要求
 */
data class AngleRequirement(
    /**
     * 关节名称（如"左膝"、"右肘"等）
     */
    val jointName: String,
    
    /**
     * 构成角度的三个关键点
     */
    val point1: PoseLandmarkType,
    val vertex: PoseLandmarkType,
    val point2: PoseLandmarkType,
    
    /**
     * 最小角度（度）
     */
    val minAngle: Float,
    
    /**
     * 最大角度（度）
     */
    val maxAngle: Float
)

/**
 * 动作错误模型
 * 表示常见的动作错误
 */
data class ExerciseMistake(
    /**
     * 错误ID
     */
    val id: String,
    
    /**
     * 错误描述
     */
    val description: String,
    
    /**
     * 检测条件
     */
    val detectionCriteria: String,
    
    /**
     * 纠正建议
     */
    val correctionAdvice: String
) 