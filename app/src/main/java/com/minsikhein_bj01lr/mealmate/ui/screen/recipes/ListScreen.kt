package com.minsikhein_bj01lr.mealmate.ui.screen.recipes

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.data.model.Recipe
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.ui.theme.CreamyYellow
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed
import com.minsikhein_bj01lr.mealmate.ui.theme.Neutral10
import com.minsikhein_bj01lr.mealmate.ui.theme.Neutral100
import com.minsikhein_bj01lr.mealmate.ui.theme.SoftCreamyYellow
import com.minsikhein_bj01lr.mealmate.ui.theme.WarmBrown
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.RecipeListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Recipes",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = DeepRed
                )

                Button(
                    onClick = { navController.navigate(Routes.RECIPES_CREATE) },
                    colors = ButtonDefaults.buttonColors(DeepRed)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Create Recipe",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create")
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(
                    items = recipes,
                    key = { it.id },
                ) { recipe ->
                    SwipeToDismissItem(
                        item = recipe,
                        onRemove = { viewModel.deleteRecipe(recipe.id, onSuccess = {}, onError = {}) },
                        modifier = Modifier.animateItem(),
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}


@Composable
fun SwipeToDismissItem(
    item: Recipe,
    onRemove: (String) -> Unit,
    modifier: Modifier,
    navController: NavController,
    viewModel: RecipeListViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Show error snackbar if delete fails
    if (showError) {
        LaunchedEffect(showError) {
            delay(3000) // Auto-dismiss after 3 seconds
            showError = false
        }
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { showError = false }) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(errorMessage)
        }
    }

    val swipeToDismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            when (state) {
                SwipeToDismissBoxValue.EndToStart -> {
                    coroutineScope.launch {
                        delay(200) // Let the animation complete
                        viewModel.deleteRecipe(
                            recipeId = item.id,
                            onSuccess = { /* Success handled by state update */ },
                            onError = { e ->
                                errorMessage = "Failed to delete recipe: ${e.message}"
                                showError = true
                            }
                        )
                    }
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    navController.navigate("${Routes.RECIPES_UPDATE}/${item.id}")
                    true
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissState,
        backgroundContent = {
            val backgroundColor by animateColorAsState(
                targetValue = when (swipeToDismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFD32F2F)  // Delete color
                    SwipeToDismissBoxValue.StartToEnd -> WarmBrown // Edit color
                    SwipeToDismissBoxValue.Settled -> Color.Transparent // When not swiping
                },
                label = "BackgroundColorAnimation"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(bottom = 10.dp)
            ) {
                // Edit action (shown when swiping from right to left)
                if (swipeToDismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = Neutral100,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Edit",
                            color = Neutral100,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                // Delete action (shown when swiping from left to right)
                if (swipeToDismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Neutral100,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Delete",
                            color = Neutral100,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }},
        modifier = modifier
    ) {
        RecipeOverviewRow(recipe = item, navController = navController)
    }
}

@Composable
fun RecipeOverviewRow(recipe: Recipe, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("${Routes.RECIPES_DETAIL}/${recipe.id}")
            }
            .background(SoftCreamyYellow)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Title and Edit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepRed,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        navController.navigate("${Routes.RECIPES_UPDATE}/${recipe.id}")
                    }
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit Recipe")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RecipeMetadataItem(
                    icon = Icons.Outlined.Timer,
                    contentDescription = "Preparation time",
                    text = "${recipe.preparationTime} min"
                )

                RecipeMetadataItem(
                    icon = Icons.Outlined.SetMeal,
                    contentDescription = "Servings",
                    text = "${recipe.servings} servings"
                )

                RecipeMetadataItem(
                    icon = Icons.Outlined.CalendarToday,
                    contentDescription = "Created date",
                    text = remember(recipe.createdAt) {
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(recipe.createdAt)
                    }
                )
            }
        }
    }
}

@Composable
private fun RecipeMetadataItem(
    icon: ImageVector,
    contentDescription: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.widthIn(min = 80.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Neutral10,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
