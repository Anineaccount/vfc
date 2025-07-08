package cn.skstudio.fitness.presentation.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.skstudio.fitness.data.repository.ExerciseRepository

/**
 * 动作详情页面
 * 显示健身动作的详细信息和说明
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    onBackClick: () -> Unit,
    onStartPractice: () -> Unit
) {
    val exerciseRepository = remember { ExerciseRepository() }
    val exercise = remember { exerciseRepository.getExerciseById(exerciseId) }
    
    if (exercise == null) {
        // 处理找不到动作的情况
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("动作未找到")
        }
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onStartPractice,
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                text = { Text("开始练习") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 基本信息卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "基本信息",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("类型：")
                        Text(getExerciseTypeText(exercise.type))
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("难度：")
                        Text(getDifficultyText(exercise.difficulty))
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    
                    Text(
                        text = "动作说明",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = exercise.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // 目标肌群卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "目标肌群",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    exercise.targetMuscles.forEach { muscle ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("•", fontWeight = FontWeight.Bold)
                            Text(muscle)
                        }
                    }
                }
            }
            
            // 动作要点卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "动作要点",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    exercise.standardPoseSequence.forEach { pose ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = pose.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                            
                            pose.keyAngles.forEach { angle ->
                                Text(
                                    text = "• ${angle.jointName}：${angle.minAngle.toInt()}°-${angle.maxAngle.toInt()}°",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // 常见错误卡片
            if (exercise.commonMistakes.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "常见错误",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        
                        exercise.commonMistakes.forEach { mistake ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = mistake.description,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = "纠正方法：${mistake.correctionAdvice}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // 底部留白，避免被FAB遮挡
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * 获取动作类型的中文文本
 */
fun getExerciseTypeText(type: cn.skstudio.fitness.domain.model.ExerciseType): String {
    return when (type) {
        cn.skstudio.fitness.domain.model.ExerciseType.STRENGTH -> "力量训练"
        cn.skstudio.fitness.domain.model.ExerciseType.CARDIO -> "有氧运动"
        cn.skstudio.fitness.domain.model.ExerciseType.FLEXIBILITY -> "柔韧性训练"
        cn.skstudio.fitness.domain.model.ExerciseType.BALANCE -> "平衡训练"
        cn.skstudio.fitness.domain.model.ExerciseType.COMPOUND -> "复合动作"
    }
} 