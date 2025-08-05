package com.minsikhein_bj01lr.mealmate.ui.navigation

// File: ui/navigation/NavGraph.kt
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.minsikhein_bj01lr.mealmate.data.repository.IngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeRepository
import com.minsikhein_bj01lr.mealmate.ui.screen.HomeScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.LoginScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.RegisterScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.SplashScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.groceries.GroceriesListScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.recipes.RecipesCreateScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.recipes.RecipesDetailScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.recipes.RecipesListScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.recipes.RecipesUpdateScreen
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthState
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.RecipeDetailViewModel

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
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
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
                authViewModel = authViewModel,
            )
        }
        composable(Routes.RECIPES_LIST) {
            RecipesListScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(Routes.RECIPES_CREATE) {
            RecipesCreateScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable(
            route = Routes.RECIPES_UPDATE_WITH_ARG,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable

            RecipesUpdateScreen(
                recipeId = recipeId,
                navController = navController,
                authViewModel = authViewModel,
            )
        }
        composable(
            route = Routes.RECIPES_DETAIL_WITH_ARG,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable

            RecipesDetailScreen(
                recipeId = recipeId,
                navController = navController,
                authViewModel = authViewModel,
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