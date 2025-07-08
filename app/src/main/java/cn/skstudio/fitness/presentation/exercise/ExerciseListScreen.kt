package cn.skstudio.fitness.presentation.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.skstudio.fitness.data.repository.ExerciseRepository
import cn.skstudio.fitness.domain.model.Exercise

/**
 * 健身动作列表页面
 * 显示所有可用的健身动作
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    onExerciseClick: (Exercise) -> Unit
) {
    val exerciseRepository = remember { ExerciseRepository() }
    val exercises = remember { exerciseRepository.getAllExercises() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "智能健身教练",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "选择一个动作开始训练",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            items(exercises) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onClick = { onExerciseClick(exercise) }
                )
            }
        }
    }
}

/**
 * 健身动作卡片组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge
                )
                
                // 难度标签
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = getDifficultyColor(exercise.difficulty)
                ) {
                    Text(
                        text = getDifficultyText(exercise.difficulty),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 目标肌群
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "目标肌群：",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = exercise.targetMuscles.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * 获取难度对应的颜色
 */
@Composable
fun getDifficultyColor(difficulty: Int): androidx.compose.ui.graphics.Color {
    return when (difficulty) {
        1 -> MaterialTheme.colorScheme.tertiary
        2 -> MaterialTheme.colorScheme.primary
        3 -> MaterialTheme.colorScheme.secondary
        4 -> MaterialTheme.colorScheme.error
        5 -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surface
    }
}

/**
 * 获取难度对应的文本
 */
fun getDifficultyText(difficulty: Int): String {
    return when (difficulty) {
        1 -> "入门"
        2 -> "初级"
        3 -> "中级"
        4 -> "高级"
        5 -> "专业"
        else -> "未知"
    }
} 