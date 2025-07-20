package com.minsikhein_bj01lr.mealmate.ui.component.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    Column {
        OutlinedTextField(
            value = uiState.title,
            onValueChange = { onUiStateChange(uiState.copy(title = it)) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.instructions,
            onValueChange = { onUiStateChange(uiState.copy(instructions = it)) },
            label = { Text("Instructions") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.preparationTime.toString(),
            onValueChange = {
                val time = it.toIntOrNull() ?: 1
                onUiStateChange(uiState.copy(preparationTime = time))
            },
            label = { Text("Preparation Time (minutes)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.servings.toString(),
            onValueChange = {
                val servings = it.toIntOrNull() ?: 1
                onUiStateChange(uiState.copy(servings = servings))
            },
            label = { Text("Servings") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text("Ingredients", style = MaterialTheme.typography.titleMedium)

        // ✅ List of added ingredients
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            uiState.ingredients.forEachIndexed { index, ingredient ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${ingredient.name} - ${ingredient.amount}",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            val updated = uiState.ingredients.toMutableList()
                            updated.removeAt(index)
                            onUiStateChange(uiState.copy(ingredients = updated))
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Input section for new ingredient
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = newIngredientName,
                onValueChange = { newIngredientName = it },
                label = { Text("Ingredient Name") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = newIngredientAmount,
                onValueChange = { newIngredientAmount = it },
                label = { Text("Amount") },
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
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Ingredient")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = onSubmit, modifier = Modifier.align(Alignment.End)) {
            Text("Submit Recipe")
        }
    }
}
