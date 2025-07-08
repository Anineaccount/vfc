package cn.skstudio.fitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cn.skstudio.fitness.presentation.navigation.FitnessNavigation
import cn.skstudio.fitness.ui.theme.VirtualFitnessCoachTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 应用主Activity
 * 作为应用的入口点，设置主题和导航
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VirtualFitnessCoachTheme {
                // 主题容器
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 应用导航
                    FitnessNavigation()
                }
            }
        }
    }
}