package cn.skstudio.fitness.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.skstudio.fitness.presentation.exercise.ExerciseDetailScreen
import cn.skstudio.fitness.presentation.exercise.ExerciseListScreen
import cn.skstudio.fitness.presentation.exercise.ExerciseScreen

/**
 * 应用导航路由
 */
object FitnessRoutes {
    const val EXERCISE_LIST = "exercise_list"
    const val EXERCISE_DETAIL = "exercise_detail/{exerciseId}"
    const val EXERCISE_PRACTICE = "exercise_practice/{exerciseId}"
    
    fun exerciseDetailRoute(exerciseId: String) = "exercise_detail/$exerciseId"
    fun exercisePracticeRoute(exerciseId: String) = "exercise_practice/$exerciseId"
}

/**
 * 应用导航主机
 * 管理应用的页面导航
 */
@Composable
fun FitnessNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = FitnessRoutes.EXERCISE_LIST
    ) {
        // 动作列表页面
        composable(FitnessRoutes.EXERCISE_LIST) {
            ExerciseListScreen(
                onExerciseClick = { exercise ->
                    navController.navigate(FitnessRoutes.exerciseDetailRoute(exercise.id))
                }
            )
        }
        
        // 动作详情页面
        composable(FitnessRoutes.EXERCISE_DETAIL) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
            ExerciseDetailScreen(
                exerciseId = exerciseId,
                onBackClick = {
                    navController.popBackStack()
                },
                onStartPractice = {
                    navController.navigate(FitnessRoutes.exercisePracticeRoute(exerciseId))
                }
            )
        }
        
        // 动作练习页面
        composable(FitnessRoutes.EXERCISE_PRACTICE) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
            ExerciseScreen(
                exerciseId = exerciseId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
} 