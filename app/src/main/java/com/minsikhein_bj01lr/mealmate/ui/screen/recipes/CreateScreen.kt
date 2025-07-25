package com.minsikhein_bj01lr.mealmate.ui.screen.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.component.LoadingScreen
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.RecipesCreateViewModel
import com.minsikhein_bj01lr.mealmate.ui.component.recipes.CreateRecipeForm
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.ui.theme.CreamyYellow
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed
import androidx.compose.material3.IconButton


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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate(Routes.RECIPES_LIST) }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBackIosNew,
                        contentDescription = "Go Back",
                        tint = DeepRed
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Create Recipe",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = DeepRed
                )
            }


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
