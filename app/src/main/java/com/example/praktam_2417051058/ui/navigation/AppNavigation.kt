package com.example.praktam_2417051058.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.praktam_2417051058.ui.dashboard.AddActivityScreen
import com.example.praktam_2417051058.ui.dashboard.CategoryDetailScreen
import com.example.praktam_2417051058.ui.dashboard.LifePatternDashboard
import com.example.praktam_2417051058.ui.dashboard.RecommendationScreen
import com.example.praktam_2417051058.ui.dashboard.SummaryScreen
import com.example.praktam_2417051058.viewmodel.ActivityViewModel
import com.example.praktam_2417051058.viewmodel.RecommendationViewModel

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object AddActivity : Screen("add_activity")
    object Summary : Screen("summary")
    object Recommendation : Screen("recommendation")
    object CategoryDetail : Screen("category_detail/{categoryId}") {
        fun createRoute(categoryId: Int) = "category_detail/$categoryId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            LifePatternDashboard(navController = navController)
        }

        composable(Screen.AddActivity.route) {
            val viewModel = hiltViewModel<ActivityViewModel>()
            AddActivityScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Summary.route) {
            val viewModel = hiltViewModel<ActivityViewModel>()
            SummaryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Recommendation.route) {
            val viewModel = hiltViewModel<RecommendationViewModel>()
            RecommendationScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CategoryDetail.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            val viewModel = hiltViewModel<ActivityViewModel>()
            CategoryDetailScreen(
                categoryId = categoryId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
