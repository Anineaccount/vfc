package cn.skstudio.fitness.domain.usecase

import cn.skstudio.fitness.domain.model.PosePoint
import kotlinx.coroutines.flow.Flow

/**
 * 姿态检测器接口
 * 定义姿态检测的核心功能
 */
interface PoseDetector {
    /**
     * 开始姿态检测
     * @return 返回姿态点的Flow流
     */
    fun startDetection(): Flow<List<PosePoint>>
    
    /**
     * 停止姿态检测
     */
    fun stopDetection()
    
    /**
     * 检查检测器是否正在运行
     */
    fun isDetecting(): Boolean
    
    /**
     * 设置检测精度模式
     * @param useAccurateMode true使用高精度模式，false使用快速模式
     */
    fun setAccurateMode(useAccurateMode: Boolean)
} 