package cn.skstudio.fitness

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 应用Application类
 * 使用@HiltAndroidApp注解启用Hilt依赖注入
 */
@HiltAndroidApp
class FitnessApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 应用初始化逻辑
    }
} 