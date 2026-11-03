package com.example.bi5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bi5.ui.screens.CategoryManagementScreen
import com.example.bi5.ui.screens.CourseDetailScreen
import com.example.bi5.ui.screens.CourseFormScreen
import com.example.bi5.ui.screens.CourseListScreen
import com.example.bi5.ui.screens.SettingsScreen
import com.example.bi5.ui.theme.Bài5Theme
import com.example.bi5.ui.viewmodel.CourseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Bài5Theme {
                val navController = rememberNavController()
                val viewModel: CourseViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "course_list"
                ) {
                    composable("course_list") {
                        CourseListScreen(
                            viewModel = viewModel,
                            onNavigateToDetail = { courseId ->
                                navController.navigate("course_detail/$courseId")
                            },
                            onNavigateToAddEdit = { courseId ->
                                if (courseId != null) {
                                    navController.navigate("course_form?courseId=$courseId")
                                } else {
                                    navController.navigate("course_form")
                                }
                            },
                            onNavigateToCategoryManagement = {
                                navController.navigate("category_management")
                            },
                            onNavigateToSettings = {
                                navController.navigate("settings")
                            }
                        )
                    }

                    composable(
                        route = "course_detail/{courseId}",
                        arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                        CourseDetailScreen(
                            courseId = courseId,
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToEdit = { id ->
                                navController.navigate("course_form?courseId=$id")
                            }
                        )
                    }

                    composable(
                        route = "course_form?courseId={courseId}",
                        arguments = listOf(
                            navArgument("courseId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val courseId = backStackEntry.arguments?.getString("courseId")
                        CourseFormScreen(
                            courseId = courseId,
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("category_management") {
                        CategoryManagementScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
