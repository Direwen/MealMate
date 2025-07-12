package com.minsikhein_bj01lr.mealmate.ui.navigation

// File: ui/navigation/NavGraph.kt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.minsikhein_bj01lr.mealmate.ui.screen.HomeScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.LoginScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.RegisterScreen

@Composable
fun MealMateNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                modifier = Modifier
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                navController = navController,
                modifier = Modifier
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
                modifier = Modifier
            )
        }
    }
}