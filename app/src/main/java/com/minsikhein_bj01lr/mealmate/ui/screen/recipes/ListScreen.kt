package com.minsikhein_bj01lr.mealmate.ui.screen.recipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.RecipeListViewModel

@Composable
fun RecipesListScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: RecipeListViewModel = viewModel()
) {
    val currentUserId = authViewModel.currentUser?.uid ?: return
    val recipes by viewModel.recipes.collectAsState()

    // Fetch recipes when screen enters composition
    LaunchedEffect(currentUserId) {
        viewModel.getRecipes(currentUserId)
    }

    AuthenticatedScreen(
        authViewModel = authViewModel,
        navController = navController
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(DeepRed),
                onClick = {
                    navController.navigate(Routes.RECIPES_CREATE)
                }
            ) {
                Text("Create Recipe")
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(recipes) { recipe ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Optional: Navigate to detail/edit screen
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = recipe.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Prep time: ${recipe.preparationTime} min",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Servings: ${recipe.servings}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
