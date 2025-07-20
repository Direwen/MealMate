package com.minsikhein_bj01lr.mealmate.ui.component.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.minsikhein_bj01lr.mealmate.ui.component.MealMateTextField
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed
import com.minsikhein_bj01lr.mealmate.ui.theme.WarmBrown
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.CreateRecipeUiState
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.IngredientInput

@Composable
fun CreateRecipeForm(
    uiState: CreateRecipeUiState,
    onUiStateChange: (CreateRecipeUiState) -> Unit,
    onSubmit: () -> Unit
) {
    var newIngredientName by remember { mutableStateOf("") }
    var newIngredientAmount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        // Title Field
        MealMateTextField(
            value = uiState.title,
            onValueChange = { onUiStateChange(uiState.copy(title = it)) },
            label = "Title",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Instructions Field
        MealMateTextField(
            value = uiState.instructions,
            onValueChange = { onUiStateChange(uiState.copy(instructions = it)) },
            label = "Instructions",
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            isSingleLine = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preparation Time
        MealMateTextField(
            value = uiState.preparationTime.toString(),
            onValueChange = {
                val time = it.toIntOrNull() ?: 1
                onUiStateChange(uiState.copy(preparationTime = time))
            },
            label = "Preparation Time (minutes)",
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Servings
        MealMateTextField(
            value = uiState.servings.toString(),
            onValueChange = {
                val servings = it.toIntOrNull() ?: 1
                onUiStateChange(uiState.copy(servings = servings))
            },
            label = "Servings",
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Ingredients List
        Text("Ingredients", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (uiState.ingredients.isEmpty()) {
                Text(
                    text = "No ingredients added yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                uiState.ingredients.forEachIndexed { index, ingredient ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${ingredient.name} - ${ingredient.amount}",
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                val updated = uiState.ingredients.toMutableList().apply {
                                    removeAt(index)
                                }
                                onUiStateChange(uiState.copy(ingredients = updated))
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add New Ingredient
        Text("Add Ingredient", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MealMateTextField(
                value = newIngredientName,
                onValueChange = { newIngredientName = it },
                label = "Ingredient Name",
                modifier = Modifier.weight(1f)
            )

            MealMateTextField(
                value = newIngredientAmount,
                onValueChange = { newIngredientAmount = it },
                label = "Amount",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val updated = uiState.ingredients.toMutableList()
                updated.add(IngredientInput(newIngredientName, newIngredientAmount))
                onUiStateChange(uiState.copy(ingredients = updated))
                newIngredientName = ""
                newIngredientAmount = ""
            },
            enabled = newIngredientName.isNotBlank() && newIngredientAmount.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(WarmBrown)
        ) {
            Text("Add Ingredient")
        }

        // Error Message
        if (uiState.error.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
        }

        // Submit Button
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(DeepRed)
        ) {
            Text("Create Recipe")
        }
    }
}
