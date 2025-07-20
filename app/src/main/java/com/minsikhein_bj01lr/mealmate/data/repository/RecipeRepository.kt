package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.Recipe
import com.minsikhein_bj01lr.mealmate.data.model.RecipeIngredient
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.CreateRecipeUiState
import kotlinx.coroutines.tasks.await
import java.util.*

class RecipeRepository(
    private val ingredientRepository: IngredientRepository,
    private val recipeIngredientRepository: RecipeIngredientRepository
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val recipeCollection = firestore.collection("recipes")
    private val TAG = "RecipeRepository"

    suspend fun createRecipeWithIngredients(uiState: CreateRecipeUiState, creatorId: String) {
        val recipeId = UUID.randomUUID().toString()

        val recipe = Recipe(
            id = recipeId,
            creatorId = creatorId,
            title = uiState.title,
            instructions = uiState.instructions,
            preparationTime = uiState.preparationTime,
            servings = uiState.servings,
        )

        try {
            // 1. Create the Recipe document
            recipeCollection.document(recipeId).set(recipe).await()
            Log.d(TAG, "Recipe created with ID = $recipeId")

            // 2. Loop through ingredients from UI state
            for (item in uiState.ingredients) {
                // Assume item has ingredientName and amount
                val ingredient = ingredientRepository.getOrCreateIngredient(item.name)

                if (ingredient != null) {
                    val recipeIngredient = RecipeIngredient(
                        id = UUID.randomUUID().toString(),
                        recipeId = recipe.id,
                        ingredientId = ingredient.id,
                        amount = item.amount
                    )
                    recipeIngredientRepository.createIngredientRecipe(recipeIngredient)
                    Log.d(TAG, "Linked ingredient ${ingredient.name} to recipe ${recipe.title}")
                } else {
                    Log.e(TAG, "Failed to get/create ingredient: ${item.name}")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to create recipe with ingredients", e)
            // You can emit some error state or return a result if needed
        }
    }

    suspend fun getRecipesByCreatorId(userId: String): List<Recipe>? {
        return try {
            val snapshot = recipeCollection
                .whereEqualTo("creatorId", userId)
                .get()
                .await()

            snapshot.toObjects(Recipe::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipes for user=$userId", e)
            null
        }
    }

    suspend fun createRecipe(recipe: Recipe): Boolean {
        return try {
            recipeCollection.document(recipe.id).set(recipe).await()
            Log.d(TAG, "Recipe added with id=${recipe.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding recipe with id=${recipe.id}", e)
            false
        }
    }

    suspend fun getRecipeById(recipeId: String): Recipe? {
        return try {
            val document = recipeCollection.document(recipeId).get().await()
            document.toObject(Recipe::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching recipe with id=$recipeId", e)
            null
        }
    }

    suspend fun deleteRecipe(recipeId: String): Boolean {
        return try {
            recipeCollection.document(recipeId).delete().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting recipe with id=$recipeId", e)
            false
        }
    }
}
