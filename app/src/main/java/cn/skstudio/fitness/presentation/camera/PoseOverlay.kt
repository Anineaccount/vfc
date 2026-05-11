package cn.skstudio.fitness.presentation.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import cn.skstudio.fitness.domain.model.PoseLandmarkType
import cn.skstudio.fitness.domain.model.PosePoint

/**
 * 姿态覆盖层组件
 * 在相机预览上绘制检测到的姿态关键点和骨架
 * @param posePoints 检测到的姿态关键点列表
 * @param modifier 修饰符
 */
@Composable
fun PoseOverlay(
    posePoints: List<PosePoint>,
    modifier: Modifier = Modifier,
    mirrorHorizontally: Boolean = true
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (posePoints.isNotEmpty()) {
            drawPose(posePoints, mirrorHorizontally)
        }
    }
}

/**
 * 绘制姿态
 */
private fun DrawScope.drawPose(
    posePoints: List<PosePoint>,
    mirrorHorizontally: Boolean
) {
    val poseMap = posePoints.associateBy { it.type }

    // 绘制骨架连接线
    drawBodyConnections(poseMap, mirrorHorizontally)

    // 绘制关键点
    posePoints.forEach { point ->
        drawPosePoint(point, mirrorHorizontally)
    }
}

/**
 * 绘制单个姿态关键点
 */
private fun DrawScope.drawPosePoint(point: PosePoint, mirrorHorizontally: Boolean) {
    val color = when {
        point.confidence > 0.8f -> Color(0xFF00E676)
        point.confidence > 0.5f -> Color(0xFFFFD54F)
        else -> Color(0xFFFF5252)
    }

    val center = point.toCanvasOffset(size.width, size.height, mirrorHorizontally)

    // 外圈描边，提升复杂背景下可见性
    drawCircle(
        color = Color.Black.copy(alpha = 0.65f),
        radius = 14f,
        center = center,
        style = Stroke(width = 4f)
    )

    // 中间色圈
    drawCircle(
        color = color,
        radius = 10f,
        center = center,
        style = Stroke(width = 3f)
    )

    // 核心点
    drawCircle(
        color = Color.White,
        radius = 5f,
        center = center
    )
}

/**
 * 绘制身体骨架连接线
 */
private fun DrawScope.drawBodyConnections(
    poseMap: Map<PoseLandmarkType, PosePoint>,
    mirrorHorizontally: Boolean
) {
    // 定义身体连接关系
    val connections = listOf(
        // 面部连接
        PoseLandmarkType.LEFT_EAR to PoseLandmarkType.LEFT_EYE_OUTER,
        PoseLandmarkType.LEFT_EYE_OUTER to PoseLandmarkType.LEFT_EYE,
        PoseLandmarkType.LEFT_EYE to PoseLandmarkType.LEFT_EYE_INNER,
        PoseLandmarkType.LEFT_EYE_INNER to PoseLandmarkType.NOSE,
        PoseLandmarkType.NOSE to PoseLandmarkType.RIGHT_EYE_INNER,
        PoseLandmarkType.RIGHT_EYE_INNER to PoseLandmarkType.RIGHT_EYE,
        PoseLandmarkType.RIGHT_EYE to PoseLandmarkType.RIGHT_EYE_OUTER,
        PoseLandmarkType.RIGHT_EYE_OUTER to PoseLandmarkType.RIGHT_EAR,
        PoseLandmarkType.MOUTH_LEFT to PoseLandmarkType.MOUTH_RIGHT,

        // 上半身连接
        PoseLandmarkType.LEFT_SHOULDER to PoseLandmarkType.RIGHT_SHOULDER,
        PoseLandmarkType.LEFT_SHOULDER to PoseLandmarkType.LEFT_ELBOW,
        PoseLandmarkType.LEFT_ELBOW to PoseLandmarkType.LEFT_WRIST,
        PoseLandmarkType.LEFT_WRIST to PoseLandmarkType.LEFT_THUMB,
        PoseLandmarkType.LEFT_WRIST to PoseLandmarkType.LEFT_PINKY,
        PoseLandmarkType.LEFT_WRIST to PoseLandmarkType.LEFT_INDEX,
        PoseLandmarkType.LEFT_PINKY to PoseLandmarkType.LEFT_INDEX,

        PoseLandmarkType.RIGHT_SHOULDER to PoseLandmarkType.RIGHT_ELBOW,
        PoseLandmarkType.RIGHT_ELBOW to PoseLandmarkType.RIGHT_WRIST,
        PoseLandmarkType.RIGHT_WRIST to PoseLandmarkType.RIGHT_THUMB,
        PoseLandmarkType.RIGHT_WRIST to PoseLandmarkType.RIGHT_PINKY,
        PoseLandmarkType.RIGHT_WRIST to PoseLandmarkType.RIGHT_INDEX,
        PoseLandmarkType.RIGHT_PINKY to PoseLandmarkType.RIGHT_INDEX,

        // 躯干连接
        PoseLandmarkType.LEFT_SHOULDER to PoseLandmarkType.LEFT_HIP,
        PoseLandmarkType.RIGHT_SHOULDER to PoseLandmarkType.RIGHT_HIP,
        PoseLandmarkType.LEFT_HIP to PoseLandmarkType.RIGHT_HIP,

        // 下半身连接
        PoseLandmarkType.LEFT_HIP to PoseLandmarkType.LEFT_KNEE,
        PoseLandmarkType.LEFT_KNEE to PoseLandmarkType.LEFT_ANKLE,
        PoseLandmarkType.LEFT_ANKLE to PoseLandmarkType.LEFT_HEEL,
        PoseLandmarkType.LEFT_ANKLE to PoseLandmarkType.LEFT_FOOT_INDEX,
        PoseLandmarkType.LEFT_HEEL to PoseLandmarkType.LEFT_FOOT_INDEX,

        PoseLandmarkType.RIGHT_HIP to PoseLandmarkType.RIGHT_KNEE,
        PoseLandmarkType.RIGHT_KNEE to PoseLandmarkType.RIGHT_ANKLE,
        PoseLandmarkType.RIGHT_ANKLE to PoseLandmarkType.RIGHT_HEEL,
        PoseLandmarkType.RIGHT_ANKLE to PoseLandmarkType.RIGHT_FOOT_INDEX,
        PoseLandmarkType.RIGHT_HEEL to PoseLandmarkType.RIGHT_FOOT_INDEX
    )

    // 绘制每条连接线
    connections.forEach { (start, end) ->
        val startPoint = poseMap[start]
        val endPoint = poseMap[end]

        if (startPoint != null && endPoint != null) {
            val minConfidence = minOf(startPoint.confidence, endPoint.confidence)
            val lineColor = when {
                minConfidence > 0.8f -> Color(0xFF00E676).copy(alpha = 0.9f)
                minConfidence > 0.5f -> Color(0xFFFFD54F).copy(alpha = 0.85f)
                else -> Color(0xFFFF5252).copy(alpha = 0.6f)
            }

            val startOffset = startPoint.toCanvasOffset(size.width, size.height, mirrorHorizontally)
            val endOffset = endPoint.toCanvasOffset(size.width, size.height, mirrorHorizontally)

            // 先画深色底线，再画亮色骨架线，增强可见性
            drawLine(
                color = Color.Black.copy(alpha = 0.5f),
                start = startOffset,
                end = endOffset,
                strokeWidth = 7f
            )
            drawLine(
                color = lineColor,
                start = startOffset,
                end = endOffset,
                strokeWidth = 4f
            )
        }
    }
}

private fun PosePoint.toCanvasOffset(
    canvasWidth: Float,
    canvasHeight: Float,
    mirrorHorizontally: Boolean
): Offset {
    val mappedX = if (mirrorHorizontally) 1f - x else x
    return Offset(mappedX * canvasWidth, y * canvasHeight)
}
