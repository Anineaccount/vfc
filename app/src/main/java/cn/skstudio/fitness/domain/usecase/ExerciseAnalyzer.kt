package cn.skstudio.fitness.domain.usecase

import cn.skstudio.fitness.domain.model.*
import cn.skstudio.fitness.utils.AngleCalculator
import javax.inject.Inject

/**
 * 健身动作分析器
 * 负责分析用户的姿态并识别健身动作
 */
class ExerciseAnalyzer @Inject constructor() {

    private var currentExerciseId: String? = null
    private var sessionStartMs: Long = 0L

    private var totalCount: Int = 0
    private var validCount: Int = 0
    private var invalidCount: Int = 0

    private var repInProgress: Boolean = false
    private var repReachedDown: Boolean = false
    private var repFormValid: Boolean = true
    private var repStartMs: Long = 0L
    private var lastRepEndMs: Long = 0L

    private enum class RepType {
        SQUAT,
        PUSHUP
    }
    
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
        if (exercise.id != currentExerciseId) {
            resetRepState(exercise.id)
        }

        if (sessionStartMs == 0L) {
            sessionStartMs = System.currentTimeMillis()
        }

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
        
        val repMetrics = updateRepetitionMetrics(
            poseMap = poseMap,
            exercise = exercise
        )

        return ExerciseAnalysisResult(
            exercise = exercise,
            currentPoseMatch = bestMatch,
            detectedMistakes = detectedMistakes,
            overallScore = bestMatch?.matchScore ?: 0f,
            feedback = generateFeedback(bestMatch, detectedMistakes),
            repMetrics = repMetrics
        )
    }

    private fun resetRepState(exerciseId: String) {
        currentExerciseId = exerciseId
        sessionStartMs = 0L
        totalCount = 0
        validCount = 0
        invalidCount = 0
        repInProgress = false
        repReachedDown = false
        repFormValid = true
        repStartMs = 0L
        lastRepEndMs = 0L
    }

    private fun updateRepetitionMetrics(
        poseMap: Map<PoseLandmarkType, PosePoint>,
        exercise: Exercise
    ): RepetitionMetrics? {
        val repType = when (exercise.id) {
            "squat" -> RepType.SQUAT
            "pushup" -> RepType.PUSHUP
            else -> return null
        }

        val now = System.currentTimeMillis()
        val profile = getRepProfile(repType)
        val posture = buildPostureSnapshot(repType, poseMap)
        val elapsedMs = (now - sessionStartMs).coerceAtLeast(0L)

        if (!posture.hasRequiredPoints) {
            return RepetitionMetrics(
                exerciseId = exercise.id,
                totalCount = totalCount,
                validCount = validCount,
                invalidCount = invalidCount,
                elapsedMs = elapsedMs
            )
        }

        val readyUp = posture.mainAngle >= profile.upThreshold
        val deepDown = posture.mainAngle <= profile.downThreshold
        val formValidFrame = posture.formValid

        if (!repInProgress && readyUp && now - lastRepEndMs >= profile.cooldownMs) {
            repInProgress = false
        }

        if (!repInProgress && posture.mainAngle < profile.startThreshold && now - lastRepEndMs >= profile.cooldownMs) {
            repInProgress = true
            repReachedDown = deepDown
            repFormValid = formValidFrame
            repStartMs = now
        }

        if (repInProgress) {
            if (deepDown) {
                repReachedDown = true
            }
            repFormValid = repFormValid && formValidFrame

            if (readyUp) {
                val durationMs = now - repStartMs
                totalCount += 1

                val isValidRep = repReachedDown && repFormValid && durationMs >= profile.minRepDurationMs
                if (isValidRep) {
                    validCount += 1
                } else {
                    invalidCount += 1
                }

                repInProgress = false
                repReachedDown = false
                repFormValid = true
                repStartMs = 0L
                lastRepEndMs = now
            }
        }

        return RepetitionMetrics(
            exerciseId = exercise.id,
            totalCount = totalCount,
            validCount = validCount,
            invalidCount = invalidCount,
            elapsedMs = elapsedMs
        )
    }

    private data class RepProfile(
        val upThreshold: Float,
        val startThreshold: Float,
        val downThreshold: Float,
        val minRepDurationMs: Long,
        val cooldownMs: Long
    )

    private data class PostureSnapshot(
        val hasRequiredPoints: Boolean,
        val mainAngle: Float,
        val formValid: Boolean
    )

    private fun getRepProfile(repType: RepType): RepProfile {
        return when (repType) {
            RepType.SQUAT -> RepProfile(
                upThreshold = 162f,
                startThreshold = 150f,
                downThreshold = 108f,
                minRepDurationMs = 700L,
                cooldownMs = 220L
            )
            RepType.PUSHUP -> RepProfile(
                upThreshold = 160f,
                startThreshold = 148f,
                downThreshold = 102f,
                minRepDurationMs = 450L,
                cooldownMs = 180L
            )
        }
    }

    private fun buildPostureSnapshot(
        repType: RepType,
        poseMap: Map<PoseLandmarkType, PosePoint>
    ): PostureSnapshot {
        return when (repType) {
            RepType.SQUAT -> {
                val kneeAngle = averageBilateralAngle(
                    poseMap,
                    PoseLandmarkType.LEFT_HIP,
                    PoseLandmarkType.LEFT_KNEE,
                    PoseLandmarkType.LEFT_ANKLE,
                    PoseLandmarkType.RIGHT_HIP,
                    PoseLandmarkType.RIGHT_KNEE,
                    PoseLandmarkType.RIGHT_ANKLE
                )
                val hipAngle = averageBilateralAngle(
                    poseMap,
                    PoseLandmarkType.LEFT_SHOULDER,
                    PoseLandmarkType.LEFT_HIP,
                    PoseLandmarkType.LEFT_KNEE,
                    PoseLandmarkType.RIGHT_SHOULDER,
                    PoseLandmarkType.RIGHT_HIP,
                    PoseLandmarkType.RIGHT_KNEE
                )

                if (kneeAngle == null || hipAngle == null) {
                    PostureSnapshot(false, 180f, false)
                } else {
                    val formValid = kneeAngle in 55f..185f && hipAngle in 55f..190f
                    PostureSnapshot(true, kneeAngle, formValid)
                }
            }
            RepType.PUSHUP -> {
                val elbowAngle = averageBilateralAngle(
                    poseMap,
                    PoseLandmarkType.LEFT_SHOULDER,
                    PoseLandmarkType.LEFT_ELBOW,
                    PoseLandmarkType.LEFT_WRIST,
                    PoseLandmarkType.RIGHT_SHOULDER,
                    PoseLandmarkType.RIGHT_ELBOW,
                    PoseLandmarkType.RIGHT_WRIST
                )
                val bodyLine = averageBilateralAngle(
                    poseMap,
                    PoseLandmarkType.LEFT_SHOULDER,
                    PoseLandmarkType.LEFT_HIP,
                    PoseLandmarkType.LEFT_ANKLE,
                    PoseLandmarkType.RIGHT_SHOULDER,
                    PoseLandmarkType.RIGHT_HIP,
                    PoseLandmarkType.RIGHT_ANKLE
                )

                if (elbowAngle == null || bodyLine == null) {
                    PostureSnapshot(false, 180f, false)
                } else {
                    val formValid = bodyLine in 148f..195f
                    PostureSnapshot(true, elbowAngle, formValid)
                }
            }
        }
    }

    private fun averageBilateralAngle(
        poseMap: Map<PoseLandmarkType, PosePoint>,
        leftPoint1: PoseLandmarkType,
        leftVertex: PoseLandmarkType,
        leftPoint2: PoseLandmarkType,
        rightPoint1: PoseLandmarkType,
        rightVertex: PoseLandmarkType,
        rightPoint2: PoseLandmarkType,
        minConfidence: Float = 0.45f
    ): Float? {
        val leftAngle = angleIfVisible(
            poseMap,
            leftPoint1,
            leftVertex,
            leftPoint2,
            minConfidence
        )
        val rightAngle = angleIfVisible(
            poseMap,
            rightPoint1,
            rightVertex,
            rightPoint2,
            minConfidence
        )

        return when {
            leftAngle != null && rightAngle != null -> (leftAngle + rightAngle) / 2f
            leftAngle != null -> leftAngle
            rightAngle != null -> rightAngle
            else -> null
        }
    }

    private fun angleIfVisible(
        poseMap: Map<PoseLandmarkType, PosePoint>,
        point1Type: PoseLandmarkType,
        vertexType: PoseLandmarkType,
        point2Type: PoseLandmarkType,
        minConfidence: Float
    ): Float? {
        val point1 = poseMap[point1Type]
        val vertex = poseMap[vertexType]
        val point2 = poseMap[point2Type]

        if (point1 == null || vertex == null || point2 == null) {
            return null
        }
        if (point1.confidence < minConfidence || vertex.confidence < minConfidence || point2.confidence < minConfidence) {
            return null
        }

        return AngleCalculator.calculateAngle(point1, vertex, point2)
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
    val feedback: String,
    val repMetrics: RepetitionMetrics?
)

data class RepetitionMetrics(
    val exerciseId: String,
    val totalCount: Int,
    val validCount: Int,
    val invalidCount: Int,
    val elapsedMs: Long
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