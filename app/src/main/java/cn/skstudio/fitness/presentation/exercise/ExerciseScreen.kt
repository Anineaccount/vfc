package cn.skstudio.fitness.presentation.exercise

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.skstudio.fitness.data.repository.ExerciseRepository
import cn.skstudio.fitness.data.repository.MLKitPoseDetector
import cn.skstudio.fitness.domain.model.PosePoint
import cn.skstudio.fitness.domain.usecase.ExerciseAnalyzer
import cn.skstudio.fitness.presentation.camera.CameraPreview
import cn.skstudio.fitness.presentation.camera.PoseOverlay
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

/**
 * 动作练习页面
 * 集成相机预览、姿态检测和动作分析
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ExerciseScreen(
    exerciseId: String,
    onBackClick: () -> Unit
) {
    val exerciseRepository = remember { ExerciseRepository() }
    val exercise = remember { exerciseRepository.getExerciseById(exerciseId) }
    val poseDetector = remember { MLKitPoseDetector() }
    val exerciseAnalyzer = remember { ExerciseAnalyzer() }
    val scope = rememberCoroutineScope()
    
    var currentPosePoints by remember { mutableStateOf<List<PosePoint>>(emptyList()) }
    var feedback by remember { mutableStateOf("准备开始...") }
    var score by remember { mutableStateOf(0f) }
    var isDetecting by remember { mutableStateOf(false) }
    
    // 相机权限处理
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    // 启动姿态检测
    LaunchedEffect(Unit) {
        scope.launch {
            poseDetector.startDetection().collect { posePoints ->
                currentPosePoints = posePoints
                isDetecting = true
                
                // 分析动作
                if (posePoints.isNotEmpty() && exercise != null) {
                    val analysisResult = exerciseAnalyzer.analyzePose(
                        posePoints = posePoints,
                        exercise = exercise
                    )
                    feedback = analysisResult.feedback
                    score = analysisResult.overallScore
                } else if (posePoints.isEmpty()) {
                    feedback = "未检测到姿态，请调整位置"
                }
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            poseDetector.stopDetection()
        }
    }
    
    if (exercise == null) {
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
        }
    ) { paddingValues ->
        if (cameraPermissionState.status.isGranted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 相机预览层
                CameraPreview(
                    poseDetector = poseDetector,
                    onPoseDetected = { posePoints ->
                        // 这里不需要重复处理，因为已经在 LaunchedEffect 中处理了
                    }
                )
                
                // 姿态覆盖层
                PoseOverlay(
                    posePoints = currentPosePoints,
                    modifier = Modifier.fillMaxSize()
                )
                
                // 反馈信息层
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(16.dp)
                ) {
                    // 分数显示
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "动作评分",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        
                        Text(
                            text = "${(score * 100).toInt()}%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = getScoreColor(score)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 反馈信息
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    ) {
                        Text(
                            text = feedback,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // 顶部提示
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                    )
                ) {
                    Text(
                        text = if (isDetecting && currentPosePoints.isNotEmpty()) {
                            "检测到姿态，正在分析..."
                        } else {
                            "请将全身置于画面中"
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        } else {
            // 权限请求界面
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (cameraPermissionState.status.shouldShowRationale) {
                        "需要相机权限才能进行姿态检测"
                    } else {
                        "请授予相机权限"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { cameraPermissionState.launchPermissionRequest() }
                ) {
                    Text("授予权限")
                }
            }
        }
    }
}

/**
 * 根据分数获取对应的颜色
 */
fun getScoreColor(score: Float): Color {
    return when {
        score >= 0.9f -> Color.Green
        score >= 0.7f -> Color.Yellow
        score >= 0.5f -> Color(0xFFFFA500) // Orange
        else -> Color.Red
    }
} 