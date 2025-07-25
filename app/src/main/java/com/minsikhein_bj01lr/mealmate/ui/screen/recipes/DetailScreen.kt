package com.minsikhein_bj01lr.mealmate.ui.screen.recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientWithDetail
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.component.LoadingScreen
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed
import com.minsikhein_bj01lr.mealmate.ui.theme.WarmBrown
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.ImportState
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
    val importState by viewModel.importState.collectAsState()
    val currentUserId = authViewModel.currentUser?.uid ?: return


    LaunchedEffect(recipeId) {
        viewModel.loadRecipeDetails(recipeId)
    }

    LaunchedEffect(importState) {
        when (importState) {
            is ImportState.Success -> {
                // TODO: Show success feedback (snackbar)
            }
            is ImportState.Error -> {
                // TODO: Show error feedback
            }
            else -> {}
        }
    }

    AuthenticatedScreen(
        authViewModel = authViewModel,
        navController = navController
    ) { innerPadding ->
        Column() {
            // Title and Back Button
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

            LoadingScreen(isLoading = uiState is RecipeDetailUiState.Loading) {
                when (val state = uiState) {
                    is RecipeDetailUiState.Error -> Text("Error: ${state.message}")
                    is RecipeDetailUiState.Success -> {
                        RecipeDetailContent(
                            title = state.recipe.title,
                            instructions = state.recipe.instructions,
                            preparationTime = state.recipe.preparationTime,
                            servings = state.recipe.servings,
                            ingredients = state.ingredients,
                            onImportClick = {
                                viewModel.importIngredientsToGroceryList(currentUserId)
                            },
                            isImporting = importState is ImportState.Loading
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun RecipeDetailContent(
    title: String,
    instructions: String,
    preparationTime: Int,
    servings: Int,
    ingredients: List<RecipeIngredientWithDetail>,
    onImportClick: () -> Unit,
    isImporting: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = title.split(" ").joinToString(" ") { it.replaceFirstChar(Char::titlecase) },
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = DeepRed
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoWithIcon(
                icon = Icons.Outlined.AccessTime,
                text = "$preparationTime mins",
                modifier = Modifier.padding(end = 16.dp)
            )
            InfoWithIcon(
                icon = Icons.Outlined.Restaurant,
                text = "Serves $servings"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = WarmBrown
            )

            Button(
                onClick = onImportClick,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(vertical = 8.dp),
                enabled = !isImporting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepRed,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Add to Grocery List",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isImporting) "Adding..." else "Add to Grocery List")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        ingredients.forEach {
            Text(
                text = "• ${it.ingredient.name}: ${it.recipeIngredient.amount}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Instructions:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = WarmBrown
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = instructions,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
fun InfoWithIcon(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .padding(end = 4.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}