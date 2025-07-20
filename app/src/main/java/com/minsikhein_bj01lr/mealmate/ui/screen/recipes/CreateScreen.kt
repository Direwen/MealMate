package com.minsikhein_bj01lr.mealmate.ui.screen.recipes

import androidx.compose.foundation.layout.*
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            LoadingScreen(isLoading = uiState.isLoading) {
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
