package cn.skstudio.fitness.domain.usecase

import cn.skstudio.fitness.domain.model.*
import cn.skstudio.fitness.utils.AngleCalculator
import javax.inject.Inject

/**
 * 健身动作分析器
 * 负责分析用户的姿态并识别健身动作
 */
class ExerciseAnalyzer @Inject constructor() {
    
    /**
     * 分析当前姿态是否符合指定的健身动作
     * @param posePoints 当前检测到的姿态关键点
     * @param exercise 要匹配的健身动作
     * @return 分析结果
     */
    fun analyzePose(
        posePoints: List<PosePoint>,
        exercise: Exercise
    ): ExerciseAnalysisResult {
        // 将姿态点转换为Map以便快速查找
        val poseMap = posePoints.associateBy { it.type }
        
        // 检查每个标准姿态
        val matchResults = exercise.standardPoseSequence.map { standardPose ->
            analyzeStandardPose(poseMap, standardPose)
        }
        
        // 找出最匹配的姿态
        val bestMatch = matchResults.maxByOrNull { it.matchScore }
        
        // 检查常见错误
        val detectedMistakes = checkCommonMistakes(poseMap, exercise)
        
        return ExerciseAnalysisResult(
            exercise = exercise,
            currentPoseMatch = bestMatch,
            detectedMistakes = detectedMistakes,
            overallScore = bestMatch?.matchScore ?: 0f,
            feedback = generateFeedback(bestMatch, detectedMistakes)
        )
    }
    
    /**
     * 分析单个标准姿态
     */
    private fun analyzeStandardPose(
        poseMap: Map<PoseLandmarkType, PosePoint>,
        standardPose: StandardPose
    ): PoseMatchResult {
        val angleResults = mutableListOf<AngleMatchResult>()
        var totalScore = 0f
        var validAngles = 0
        
        // 检查每个角度要求
        standardPose.keyAngles.forEach { requirement ->
            val point1 = poseMap[requirement.point1]
            val vertex = poseMap[requirement.vertex]
            val point2 = poseMap[requirement.point2]
            
            if (point1 != null && vertex != null && point2 != null) {
                val angle = AngleCalculator.calculateAngle(point1, vertex, point2)
                val isCorrect = AngleCalculator.isAngleInRange(
                    angle,
                    requirement.minAngle,
                    requirement.maxAngle
                )
                
                val score = calculateAngleScore(
                    angle,
                    requirement.minAngle,
                    requirement.maxAngle
                )
                
                angleResults.add(
                    AngleMatchResult(
                        jointName = requirement.jointName,
                        actualAngle = angle,
                        expectedRange = requirement.minAngle to requirement.maxAngle,
                        isCorrect = isCorrect,
                        score = score
                    )
                )
                
                totalScore += score
                validAngles++
            }
        }
        
        val matchScore = if (validAngles > 0) totalScore / validAngles else 0f
        
        return PoseMatchResult(
            poseName = standardPose.name,
            angleResults = angleResults,
            matchScore = matchScore
        )
    }
    
    /**
     * 计算角度匹配分数
     */
    private fun calculateAngleScore(
        actualAngle: Float,
        minAngle: Float,
        maxAngle: Float
    ): Float {
        return when {
            actualAngle < minAngle -> {
                val deviation = minAngle - actualAngle
                (1f - deviation / 90f).coerceIn(0f, 1f)
            }
            actualAngle > maxAngle -> {
                val deviation = actualAngle - maxAngle
                (1f - deviation / 90f).coerceIn(0f, 1f)
            }
            else -> 1f // 完全匹配
        }
    }
    
    /**
     * 检查常见错误
     */
    private fun checkCommonMistakes(
        poseMap: Map<PoseLandmarkType, PosePoint>,
        exercise: Exercise
    ): List<ExerciseMistake> {
        // 这里应该根据具体的动作实现错误检测逻辑
        // 现在返回空列表作为示例
        return emptyList()
    }
    
    /**
     * 生成反馈信息
     */
    private fun generateFeedback(
        poseMatch: PoseMatchResult?,
        mistakes: List<ExerciseMistake>
    ): String {
        return when {
            poseMatch == null -> "无法检测到有效的姿态，请调整相机位置"
            poseMatch.matchScore >= 0.9f -> "非常好！动作标准"
            poseMatch.matchScore >= 0.7f -> "不错！继续保持"
            poseMatch.matchScore >= 0.5f -> {
                val incorrectAngles = poseMatch.angleResults
                    .filter { !it.isCorrect }
                    .joinToString(", ") { it.jointName }
                "需要调整：$incorrectAngles"
            }
            else -> "动作需要较大调整，请参考示范动作"
        }
    }
}

/**
 * 动作分析结果
 */
data class ExerciseAnalysisResult(
    val exercise: Exercise,
    val currentPoseMatch: PoseMatchResult?,
    val detectedMistakes: List<ExerciseMistake>,
    val overallScore: Float,
    val feedback: String
)

/**
 * 姿态匹配结果
 */
data class PoseMatchResult(
    val poseName: String,
    val angleResults: List<AngleMatchResult>,
    val matchScore: Float
)

/**
 * 角度匹配结果
 */
data class AngleMatchResult(
    val jointName: String,
    val actualAngle: Float,
    val expectedRange: Pair<Float, Float>,
    val isCorrect: Boolean,
    val score: Float
) 