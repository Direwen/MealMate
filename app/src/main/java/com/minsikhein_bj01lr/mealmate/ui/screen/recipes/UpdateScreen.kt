package com.minsikhein_bj01lr.mealmate.ui.screen.recipes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.component.LoadingScreen
import com.minsikhein_bj01lr.mealmate.ui.component.recipes.UpdateRecipeForm
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.RecipeUpdateViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.UpdateRecipeUiState

@Composable
fun RecipesUpdateScreen(
    recipeId: String,
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    val context = LocalContext.current
    val viewModel: RecipeUpdateViewModel = viewModel {
        RecipeUpdateViewModel { context }
    }
    val uiState by viewModel.uiState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        println("Image selection result: $uri")
        uri?.let { viewModel.setImageUri(it) }
    }

    LaunchedEffect(recipeId) {
        viewModel.loadRecipeForEditing(recipeId)
    }

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
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Update Recipe",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LoadingScreen(isLoading = uiState.isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    UpdateRecipeForm(
                        uiState = UpdateRecipeUiState(
                            id = uiState.id,
                            title = uiState.title,
                            instructions = uiState.instructions,
                            preparationTime = uiState.preparationTime,
                            servings = uiState.servings,
                            ingredients = uiState.ingredients,
                            imageUri = uiState.imageUri,
                            currentImagePath = uiState.currentImagePath,
                            isLoading = uiState.isLoading,
                            error = uiState.error
                        ),
                        onUiStateChange = {
                            viewModel.onUiStateChange(
                                uiState.copy(
                                    title = it.title,
                                    instructions = it.instructions,
                                    preparationTime = it.preparationTime,
                                    servings = it.servings,
                                    ingredients = it.ingredients,
                                    error = it.error
                                )
                            )
                        },
                        onSubmit = {
                            viewModel.submitUpdate(
                                onSuccess = { navController.navigate(Routes.RECIPES_LIST) },
                                onError = { e -> println("Update Error: ${e.message}") }
                            )
                        },
                        onImageSelect = { launcher.launch("image/*") }
                    )
                }
            }
        }
    }
}