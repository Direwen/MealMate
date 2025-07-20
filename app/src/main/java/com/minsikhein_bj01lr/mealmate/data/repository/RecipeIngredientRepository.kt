package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.RecipeIngredient
import kotlinx.coroutines.tasks.await

class RecipeIngredientRepository {
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
}