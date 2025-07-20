package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.RecipeIngredient
import kotlinx.coroutines.tasks.await

class RecipeIngredientRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val recipeIngredientCollection = firestore.collection("recipeIngredients")
    private val TAG = "RecipeIngredientRepo"

    suspend fun getIngredientsForRecipe(recipeId: String): List<RecipeIngredient>? {
        return try {
            val snapshot = recipeIngredientCollection
                .whereEqualTo("recipeId", recipeId)
                .get()
                .await()

            snapshot.toObjects(RecipeIngredient::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching recipe ingredients for recipe ID: $recipeId", e)
            null
        }
    }

    suspend fun addRecipeIngredient(recipeIngredient: RecipeIngredient): Boolean {
        return try {
            recipeIngredientCollection.document(recipeIngredient.id).set(recipeIngredient).await()
            Log.d(TAG, "RecipeIngredient added with id=${recipeIngredient.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding recipe ingredient", e)
            false
        }
    }

    suspend fun updateRecipeIngredient(recipeIngredient: RecipeIngredient): Boolean {
        return try {
            recipeIngredientCollection.document(recipeIngredient.id).set(recipeIngredient).await()
            Log.d(TAG, "RecipeIngredient updated with id=${recipeIngredient.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating recipe ingredient", e)
            false
        }
    }

    suspend fun deleteRecipeIngredient(id: String): Boolean {
        return try {
            recipeIngredientCollection.document(id).delete().await()
            Log.d(TAG, "RecipeIngredient deleted with id=$id")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting recipe ingredient", e)
            false
        }
    }

    suspend fun deleteAllIngredientsForRecipe(recipeId: String): Boolean {
        return try {
            val snapshot = recipeIngredientCollection
                .whereEqualTo("recipeId", recipeId)
                .get()
                .await()

            for (document in snapshot.documents) {
                document.reference.delete().await()
            }

            Log.d(TAG, "All ingredients deleted for recipe ID: $recipeId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting all ingredients for recipe ID: $recipeId", e)
            false
        }
    }
}