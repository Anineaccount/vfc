package cn.skstudio.fitness.data.repository

import androidx.camera.core.ImageProxy
import cn.skstudio.fitness.domain.model.PoseLandmarkType
import cn.skstudio.fitness.domain.model.PosePoint
import cn.skstudio.fitness.domain.usecase.PoseDetector
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 基于Google ML Kit的姿态检测器实现
 * 使用ML Kit的Pose Detection API进行人体姿态识别
 */
@Singleton
class MLKitPoseDetector @Inject constructor() : PoseDetector {
    
    private var poseDetector: com.google.mlkit.vision.pose.PoseDetector? = null
    private var isDetecting = false
    private var useAccurateMode = false
    private val poseFlow = MutableStateFlow<List<PosePoint>>(emptyList())
    
    /**
     * 开始姿态检测
     * @return 姿态点数据流
     */
    override fun startDetection(): Flow<List<PosePoint>> {
        // 初始化检测器
        initializeDetector()
        isDetecting = true
        return poseFlow
    }
    
    /**
     * 停止姿态检测
     */
    override fun stopDetection() {
        isDetecting = false
        poseDetector?.close()
        poseDetector = null
        poseFlow.value = emptyList()
    }
    
    /**
     * 检查是否正在检测
     */
    override fun isDetecting(): Boolean = isDetecting
    
    /**
     * 设置精度模式
     */
    override fun setAccurateMode(useAccurateMode: Boolean) {
        this.useAccurateMode = useAccurateMode
        if (isDetecting) {
            // 如果正在检测，重新初始化检测器
            poseDetector?.close()
            initializeDetector()
        }
    }
    
    /**
     * 处理相机图像帧
     * @param imageProxy 相机图像代理
     * @param onPoseDetected 检测到姿态时的回调
     */
    fun processImageProxy(
        imageProxy: ImageProxy,
        onPoseDetected: (List<PosePoint>) -> Unit
    ) {
        if (!isDetecting) return
        
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )
            
            poseDetector?.process(image)
                ?.addOnSuccessListener { pose ->
                    val posePoints = convertToPosePoints(pose)
                    poseFlow.value = posePoints
                    onPoseDetected(posePoints)
                }
                ?.addOnFailureListener { e ->
                    // 处理检测失败
                    e.printStackTrace()
                    poseFlow.value = emptyList()
                }
                ?.addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
    
    /**
     * 初始化姿态检测器
     */
    private fun initializeDetector() {
        val options = if (useAccurateMode) {
            AccuratePoseDetectorOptions.Builder()
                .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
                .build()
        } else {
            PoseDetectorOptions.Builder()
                .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                .build()
        }
        
        poseDetector = PoseDetection.getClient(options)
    }
    
    /**
     * 将ML Kit的Pose转换为应用的PosePoint列表
     */
    private fun convertToPosePoints(pose: Pose): List<PosePoint> {
        return pose.allPoseLandmarks.mapNotNull { landmark ->
            val type = mapLandmarkType(landmark.landmarkType)
            type?.let {
                PosePoint(
                    type = it,
                    x = landmark.position.x,
                    y = landmark.position.y,
                    z = landmark.position3D.z,
                    confidence = landmark.inFrameLikelihood
                )
            }
        }
    }
    
    /**
     * 映射ML Kit的关键点类型到应用的类型
     */
    private fun mapLandmarkType(mlKitType: Int): PoseLandmarkType? {
        return when (mlKitType) {
            PoseLandmark.NOSE -> PoseLandmarkType.NOSE
            PoseLandmark.LEFT_EYE_INNER -> PoseLandmarkType.LEFT_EYE_INNER
            PoseLandmark.LEFT_EYE -> PoseLandmarkType.LEFT_EYE
            PoseLandmark.LEFT_EYE_OUTER -> PoseLandmarkType.LEFT_EYE_OUTER
            PoseLandmark.RIGHT_EYE_INNER -> PoseLandmarkType.RIGHT_EYE_INNER
            PoseLandmark.RIGHT_EYE -> PoseLandmarkType.RIGHT_EYE
            PoseLandmark.RIGHT_EYE_OUTER -> PoseLandmarkType.RIGHT_EYE_OUTER
            PoseLandmark.LEFT_EAR -> PoseLandmarkType.LEFT_EAR
            PoseLandmark.RIGHT_EAR -> PoseLandmarkType.RIGHT_EAR
            PoseLandmark.LEFT_MOUTH -> PoseLandmarkType.MOUTH_LEFT
            PoseLandmark.RIGHT_MOUTH -> PoseLandmarkType.MOUTH_RIGHT
            PoseLandmark.LEFT_SHOULDER -> PoseLandmarkType.LEFT_SHOULDER
            PoseLandmark.RIGHT_SHOULDER -> PoseLandmarkType.RIGHT_SHOULDER
            PoseLandmark.LEFT_ELBOW -> PoseLandmarkType.LEFT_ELBOW
            PoseLandmark.RIGHT_ELBOW -> PoseLandmarkType.RIGHT_ELBOW
            PoseLandmark.LEFT_WRIST -> PoseLandmarkType.LEFT_WRIST
            PoseLandmark.RIGHT_WRIST -> PoseLandmarkType.RIGHT_WRIST
            PoseLandmark.LEFT_PINKY -> PoseLandmarkType.LEFT_PINKY
            PoseLandmark.RIGHT_PINKY -> PoseLandmarkType.RIGHT_PINKY
            PoseLandmark.LEFT_INDEX -> PoseLandmarkType.LEFT_INDEX
            PoseLandmark.RIGHT_INDEX -> PoseLandmarkType.RIGHT_INDEX
            PoseLandmark.LEFT_THUMB -> PoseLandmarkType.LEFT_THUMB
            PoseLandmark.RIGHT_THUMB -> PoseLandmarkType.RIGHT_THUMB
            PoseLandmark.LEFT_HIP -> PoseLandmarkType.LEFT_HIP
            PoseLandmark.RIGHT_HIP -> PoseLandmarkType.RIGHT_HIP
            PoseLandmark.LEFT_KNEE -> PoseLandmarkType.LEFT_KNEE
            PoseLandmark.RIGHT_KNEE -> PoseLandmarkType.RIGHT_KNEE
            PoseLandmark.LEFT_ANKLE -> PoseLandmarkType.LEFT_ANKLE
            PoseLandmark.RIGHT_ANKLE -> PoseLandmarkType.RIGHT_ANKLE
            PoseLandmark.LEFT_HEEL -> PoseLandmarkType.LEFT_HEEL
            PoseLandmark.RIGHT_HEEL -> PoseLandmarkType.RIGHT_HEEL
            PoseLandmark.LEFT_FOOT_INDEX -> PoseLandmarkType.LEFT_FOOT_INDEX
            PoseLandmark.RIGHT_FOOT_INDEX -> PoseLandmarkType.RIGHT_FOOT_INDEX
            else -> null
        }
    }
} 