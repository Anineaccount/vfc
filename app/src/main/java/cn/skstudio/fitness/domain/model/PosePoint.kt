package cn.skstudio.fitness.domain.model

/**
 * 姿态关键点模型
 * 表示人体姿态中的一个关键点（如肩膀、肘部、膝盖等）
 */
data class PosePoint(
    /**
     * 关键点类型（如左肩、右肘等）
     */
    val type: PoseLandmarkType,
    
    /**
     * X坐标（相对于图像宽度的比例，范围0-1）
     */
    val x: Float,
    
    /**
     * Y坐标（相对于图像高度的比例，范围0-1）
     */
    val y: Float,
    
    /**
     * Z坐标（深度信息，可选）
     */
    val z: Float? = null,
    
    /**
     * 置信度（0-1之间，表示检测的可信程度）
     */
    val confidence: Float
)

/**
 * 姿态关键点类型枚举
 * 定义人体的各个关键点
 */
enum class PoseLandmarkType {
    // 面部关键点
    NOSE,
    LEFT_EYE_INNER, LEFT_EYE, LEFT_EYE_OUTER,
    RIGHT_EYE_INNER, RIGHT_EYE, RIGHT_EYE_OUTER,
    LEFT_EAR, RIGHT_EAR,
    MOUTH_LEFT, MOUTH_RIGHT,
    
    // 上半身关键点
    LEFT_SHOULDER, RIGHT_SHOULDER,
    LEFT_ELBOW, RIGHT_ELBOW,
    LEFT_WRIST, RIGHT_WRIST,
    LEFT_PINKY, RIGHT_PINKY,
    LEFT_INDEX, RIGHT_INDEX,
    LEFT_THUMB, RIGHT_THUMB,
    
    // 下半身关键点
    LEFT_HIP, RIGHT_HIP,
    LEFT_KNEE, RIGHT_KNEE,
    LEFT_ANKLE, RIGHT_ANKLE,
    LEFT_HEEL, RIGHT_HEEL,
    LEFT_FOOT_INDEX, RIGHT_FOOT_INDEX
} 