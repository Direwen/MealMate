package com.minsikhein_bj01lr.mealmate.ui.navigation

// File: ui/navigation/NavGraph.kt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.minsikhein_bj01lr.mealmate.ui.screen.HomeScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.LoginScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.RegisterScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.groceries.GroceriesListScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.recipes.RecipesListScreen
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthState
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel

@Composable
fun MealMateNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier
) {
    // Collect as state is used to only read values of public authState from passed authViewModel and trigger recompositions when updated
    val authState by authViewModel.authState.collectAsState()

    val startDestination = when (authState) {
        is AuthState.Authenticated -> Routes.HOME
        else -> Routes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Routes.RECIPES_LIST) {
            RecipesListScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Routes.GROCERIES_LIST) {
            GroceriesListScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}