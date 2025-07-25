package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.Ingredient
import com.minsikhein_bj01lr.mealmate.data.model.RecipeIngredient
import kotlinx.coroutines.tasks.await

class RecipeIngredientRepository(
    private val ingredientRepository: IngredientRepository
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val recipeIngredientCollection = firestore.collection("recipeIngredients")
    private val TAG = "RecipeIngredientRepo"

    suspend fun createIngredientRecipe(recipeIngredient: RecipeIngredient): Boolean {
        return try {
            recipeIngredientCollection.document(recipeIngredient.id).set(recipeIngredient).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getRecipeIngredients(recipeId: String): List<RecipeIngredient>? {
        return try {
            val snapshot = recipeIngredientCollection
                .whereEqualTo("recipeId", recipeId)
                .get()
                .await()
            snapshot.toObjects(RecipeIngredient::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteIngredientsByRecipeId(recipeId: String) {
        try {
            val recipeIngredients = getRecipeIngredients(recipeId)
            recipeIngredients?.forEach { recipeIngredient ->
                recipeIngredientCollection.document(recipeIngredient.id).delete().await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete ingredients for recipeId: $recipeId", e)
        }
    }

    suspend fun getByIds(ids: List<String>): List<RecipeIngredient> {
        return try {
            if (ids.isEmpty()) return emptyList()
            recipeIngredientCollection
                .whereIn("id", ids)
                .get()
                .await()
                .toObjects(RecipeIngredient::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipe ingredients by ids", e)
            emptyList()
        }
    }

}

data class RecipeIngredientWithDetail(
    val recipeIngredient: RecipeIngredient,
    val ingredient: Ingredient
)
