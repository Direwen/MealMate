package com.minsikhein_bj01lr.mealmate.ui.screen.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.component.LoadingScreen
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.RecipesCreateViewModel
import com.minsikhein_bj01lr.mealmate.ui.component.recipes.CreateRecipeForm
import com.minsikhein_bj01lr.mealmate.ui.theme.CreamyYellow

@Composable
fun RecipesCreateScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: RecipesCreateViewModel = viewModel()
) {
    val uiState by viewModel.createRecipeUiState.collectAsState()

    AuthenticatedScreen(
        authViewModel = authViewModel,
        navController = navController
    ) { innerPadding ->

        Column {
            Text("Create Recipe")
            LoadingScreen(isLoading = uiState.isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    CreateRecipeForm(
                        uiState = uiState,
                        onUiStateChange = { viewModel.onUiStateChange(it) },
                        onSubmit = {
                            viewModel.submitRecipe(
                                currentUserId = authViewModel.currentUser?.uid ?: "",
                                onSuccess = { navController.popBackStack() },
                                onError = { e -> println("Error: ${e.message}") }
                            )
                        }
                    )
                }
            }
        }
    }
}
