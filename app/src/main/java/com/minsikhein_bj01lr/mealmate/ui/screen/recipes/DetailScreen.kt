package com.minsikhein_bj01lr.mealmate.ui.screen.recipes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.component.LoadingScreen
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.RecipeDetailUiState
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.RecipeDetailViewModel

@Composable
fun RecipesDetailScreen(
    recipeId: String,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    viewModel: RecipeDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

     LaunchedEffect(recipeId) {
         viewModel.loadRecipeDetails(recipeId)
     }

    AuthenticatedScreen(
        authViewModel = authViewModel,
        navController = navController
    ) { innerPadding ->

        Column {

            //Title and Back Button
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
                    text = "Recipe Details",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = DeepRed
                )
            }

            // Wrap main content in your custom loading screen
            LoadingScreen(isLoading = uiState is RecipeDetailUiState.Loading) {
                when (val state = uiState) {
                    is RecipeDetailUiState.Error -> Text("Error: ${state.message}")
                    is RecipeDetailUiState.Success -> {
                        Column {
                            Text(text = state.recipe.title)
                            Text(text = "Instructions: ${state.recipe.instructions}")
                            state.ingredients.forEach {
                                Text("- ${it.ingredient.name}: ${it.recipeIngredient.amount}")
                            }
                        }
                    }

                    else -> {} // For loading, no need to handle, already showing overlay
                }
            }

        }
    }
}

