package cn.skstudio.fitness.utils

import cn.skstudio.fitness.domain.model.PosePoint
import kotlin.math.*

/**
 * 角度计算工具类
 * 提供各种角度计算相关的工具方法
 */
object AngleCalculator {
    
    /**
     * 计算三个点形成的角度
     * @param point1 第一个点
     * @param vertex 顶点（角的顶点）
     * @param point2 第二个点
     * @return 角度值（度），范围0-180
     */
    fun calculateAngle(
        point1: PosePoint,
        vertex: PosePoint,
        point2: PosePoint
    ): Float {
        // 计算两个向量
        val vector1X = point1.x - vertex.x
        val vector1Y = point1.y - vertex.y
        val vector2X = point2.x - vertex.x
        val vector2Y = point2.y - vertex.y
        
        // 计算点积和向量长度
        val dotProduct = vector1X * vector2X + vector1Y * vector2Y
        val magnitude1 = sqrt(vector1X * vector1X + vector1Y * vector1Y)
        val magnitude2 = sqrt(vector2X * vector2X + vector2Y * vector2Y)
        
        // 避免除零错误
        if (magnitude1 == 0f || magnitude2 == 0f) {
            return 0f
        }
        
        // 计算余弦值并限制在[-1, 1]范围内
        val cosineAngle = (dotProduct / (magnitude1 * magnitude2)).coerceIn(-1f, 1f)
        
        // 计算角度（弧度转度）
        val angleRadians = acos(cosineAngle)
        return Math.toDegrees(angleRadians.toDouble()).toFloat()
    }
    
    /**
     * 计算两点之间的距离
     * @param point1 第一个点
     * @param point2 第二个点
     * @return 欧几里得距离
     */
    fun calculateDistance(point1: PosePoint, point2: PosePoint): Float {
        val dx = point1.x - point2.x
        val dy = point1.y - point2.y
        return sqrt(dx * dx + dy * dy)
    }
    
    /**
     * 计算两点连线与水平线的角度
     * @param point1 起点
     * @param point2 终点
     * @return 角度值（度），范围-180到180
     */
    fun calculateAngleToHorizontal(point1: PosePoint, point2: PosePoint): Float {
        val dx = point2.x - point1.x
        val dy = point2.y - point1.y
        val angleRadians = atan2(dy, dx)
        return Math.toDegrees(angleRadians.toDouble()).toFloat()
    }
    
    /**
     * 检查角度是否在指定范围内
     * @param angle 要检查的角度
     * @param minAngle 最小角度
     * @param maxAngle 最大角度
     * @param tolerance 容差值
     * @return 是否在范围内
     */
    fun isAngleInRange(
        angle: Float,
        minAngle: Float,
        maxAngle: Float,
        tolerance: Float = 5f
    ): Boolean {
        return angle >= (minAngle - tolerance) && angle <= (maxAngle + tolerance)
    }
    
    /**
     * 计算身体倾斜角度
     * 基于肩膀和臀部的连线
     * @param leftShoulder 左肩关键点
     * @param rightShoulder 右肩关键点
     * @param leftHip 左臀关键点
     * @param rightHip 右臀关键点
     * @return 身体倾斜角度（度）
     */
    fun calculateBodyTilt(
        leftShoulder: PosePoint,
        rightShoulder: PosePoint,
        leftHip: PosePoint,
        rightHip: PosePoint
    ): Float {
        // 计算肩膀中心点
        val shoulderCenterX = (leftShoulder.x + rightShoulder.x) / 2
        val shoulderCenterY = (leftShoulder.y + rightShoulder.y) / 2
        
        // 计算臀部中心点
        val hipCenterX = (leftHip.x + rightHip.x) / 2
        val hipCenterY = (leftHip.y + rightHip.y) / 2
        
        // 计算倾斜角度
        val dx = shoulderCenterX - hipCenterX
        val dy = shoulderCenterY - hipCenterY
        val angleRadians = atan2(dx, dy)
        
        // 转换为度数，并调整为相对于垂直线的角度
        return Math.toDegrees(angleRadians.toDouble()).toFloat()
    }
    
    /**
     * 归一化角度到0-360度范围
     * @param angle 原始角度
     * @return 归一化后的角度
     */
    fun normalizeAngle(angle: Float): Float {
        var normalized = angle % 360
        if (normalized < 0) {
            normalized += 360
        }
        return normalized
    }
} 