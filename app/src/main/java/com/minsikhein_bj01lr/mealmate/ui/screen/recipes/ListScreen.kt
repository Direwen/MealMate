package com.minsikhein_bj01lr.mealmate.ui.screen.recipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.SetMeal
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.data.model.Recipe
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed
import com.minsikhein_bj01lr.mealmate.ui.theme.Neutral10
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.RecipeListViewModel
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
        Column(modifier = Modifier
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

                items(recipes) { recipe ->
                    Column {

                        //Overview Recipe Row
                        RecipeOverviewRow(recipe, navController)

                        // Horizontal divider after each item except the last one
                        if (recipe != recipes.last()) {
                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeOverviewRow(recipe: Recipe, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("${Routes.RECIPES_DETAIL}/${recipe.id}")
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleLarge,
                color = DeepRed,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Main container for the metadata row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Preparation Time
                RecipeMetadataItem(
                    icon = Icons.Outlined.Timer,
                    contentDescription = "Preparation time",
                    text = "${recipe.preparationTime} min"
                )

                // Number of Servings
                RecipeMetadataItem(
                    icon = Icons.Outlined.SetMeal,
                    contentDescription = "Servings",
                    text = "${recipe.servings} servings"
                )

                // Date (Created At)
                RecipeMetadataItem(
                    icon = Icons.Outlined.CalendarToday,
                    contentDescription = "Created date",
                    text = remember(recipe.createdAt) {
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            .format(recipe.createdAt)
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
        modifier = Modifier.widthIn(min = 80.dp) // Set minimum width for each item
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