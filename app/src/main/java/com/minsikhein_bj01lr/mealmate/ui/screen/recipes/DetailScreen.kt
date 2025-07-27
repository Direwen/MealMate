package com.minsikhein_bj01lr.mealmate.ui.screen.recipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.SetMeal
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.compose.ui.draw.clip
import com.minsikhein_bj01lr.mealmate.data.model.Recipe
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientWithDetail
import com.minsikhein_bj01lr.mealmate.data.util.ImageStorageHelper
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.component.LoadingScreen
import com.minsikhein_bj01lr.mealmate.ui.theme.CreamyYellow
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed
import com.minsikhein_bj01lr.mealmate.ui.theme.Neutral10
import com.minsikhein_bj01lr.mealmate.ui.theme.Neutral100
import com.minsikhein_bj01lr.mealmate.ui.theme.SoftCreamyYellow
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
) {
    val context = LocalContext.current
    val viewModel: RecipeDetailViewModel = viewModel {
        RecipeDetailViewModel { context }
    }
    val uiState by viewModel.uiState.collectAsState()
    val importState by viewModel.importState.collectAsState()
    val currentUserId = authViewModel.currentUser?.uid ?: return

    LaunchedEffect(recipeId) {
        viewModel.loadRecipeDetails(recipeId)
    }

    // Handle import state changes
    LaunchedEffect(importState) {
        when (importState) {
            ImportState.Success -> {
                // Show snackbar on success
                // You can implement a proper snackbar here if needed
            }
            is ImportState.Error -> {
                // Show error message if needed
            }
            else -> {}
        }
    }

    AuthenticatedScreen(
        authViewModel = authViewModel,
        navController = navController
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftCreamyYellow)
        ) {
            LoadingScreen(isLoading = uiState is RecipeDetailUiState.Loading) {
                when (val state = uiState) {
                    is RecipeDetailUiState.Error -> {
                        Text(
                            text = "Error: ${state.message}",
                            modifier = Modifier.padding(16.dp),
                            color = DeepRed
                        )
                    }
                    is RecipeDetailUiState.Success -> {
                        RecipeDetailContent(
                            navController = navController,
                            recipe = state.recipe,
                            ingredients = state.ingredients,
                            onImportClick = {
                                viewModel.importIngredientsToGroceryList(currentUserId)
                            },
                            isImporting = importState is ImportState.Loading,
                            modifier = Modifier.padding(16.dp)
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
    navController: NavController,
    recipe: Recipe,
    ingredients: List<RecipeIngredientWithDetail>,
    onImportClick: () -> Unit,
    isImporting: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageStorageHelper = remember { ImageStorageHelper(context.applicationContext) }
    val imageBitmap = remember(recipe.imagePath) {
        recipe.imagePath.takeIf { it.isNotEmpty() }?.let { path ->
            imageStorageHelper.loadImage(path)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Back button and title row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = DeepRed
                )
            }
            Text(
                text = "Recipe Details",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = DeepRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recipe Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(WarmBrown.copy(alpha = 0.2f))
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "Recipe image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.SetMeal,
                    contentDescription = "No image",
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center),
                    tint = WarmBrown
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recipe Title
        Text(
            text = recipe.title.replaceFirstChar { it.titlecase() },
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = DeepRed
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Recipe Metadata
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoWithIcon(
                icon = Icons.Outlined.AccessTime,
                text = "${recipe.preparationTime} mins",
                iconColor = WarmBrown
            )
            InfoWithIcon(
                icon = Icons.Outlined.Restaurant,
                text = "Serves ${recipe.servings}",
                iconColor = WarmBrown
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ingredients Section
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = WarmBrown
                )

                Button(
                    onClick = onImportClick,
                    enabled = !isImporting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepRed,
                        contentColor = Neutral100
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Add to Grocery List",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isImporting) "Adding..." else "Add")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CreamyYellow)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ingredients.forEach { ingredient ->
                    Text(
                        text = "• ${ingredient.ingredient.name}: ${ingredient.recipeIngredient.amount}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Neutral10
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Instructions Section
        Column {
            Text(
                text = "Instructions",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = WarmBrown
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = recipe.instructions,
                style = MaterialTheme.typography.bodyLarge,
                color = Neutral10,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CreamyYellow)
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun InfoWithIcon(
    icon: ImageVector,
    text: String,
    iconColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = iconColor
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Neutral10
        )
    }
}