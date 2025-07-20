package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.Recipe
import kotlinx.coroutines.tasks.await
import java.util.*

class RecipeRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val recipeCollection = firestore.collection("recipes")
    private val TAG = "RecipeRepository"


    fun createRecipeWithId(
        creatorId: String,
        title: String,
        instructions: String,
        preparationTime: Int,
        servings: Int
    ): Recipe {
        val id = recipeCollection.document().id
        return Recipe(
            id = id,
            creatorId = creatorId,
            title = title,
            instructions = instructions,
            preparationTime = preparationTime,
            servings = servings,
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    fun isRecipeValid(recipe: Recipe): Boolean {
        return recipe.title.isNotBlank() &&
                recipe.instructions.isNotBlank() &&
                recipe.servings > 0 &&
                recipe.preparationTime >= 0
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

    suspend fun addRecipe(recipe: Recipe): Boolean {
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
