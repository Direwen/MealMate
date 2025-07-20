package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.Ingredient
import kotlinx.coroutines.tasks.await

class IngredientRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val ingredientCollection = firestore.collection("ingredients")
    private val TAG = "IngredientRepository"

    suspend fun getAllIngredients(): List<Ingredient>? {
        return try {
            val snapshot = ingredientCollection.get().await()
            snapshot.toObjects(Ingredient::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching ingredients", e)
            null
        }
    }

    suspend fun getIngredientById(id: String): Ingredient? {
        return try {
            val document = ingredientCollection.document(id).get().await()
            document.toObject(Ingredient::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching ingredient by ID: $id", e)
            null
        }
    }

    suspend fun addIngredient(ingredient: Ingredient): Boolean {
        return try {
            ingredientCollection.document(ingredient.id).set(ingredient).await()
            Log.d(TAG, "Ingredient added with id=${ingredient.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding ingredient", e)
            false
        }
    }

    suspend fun updateIngredient(ingredient: Ingredient): Boolean {
        return try {
            ingredientCollection.document(ingredient.id).set(ingredient).await()
            Log.d(TAG, "Ingredient updated with id=${ingredient.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating ingredient", e)
            false
        }
    }

    suspend fun deleteIngredient(id: String): Boolean {
        return try {
            ingredientCollection.document(id).delete().await()
            Log.d(TAG, "Ingredient deleted with id=$id")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting ingredient", e)
            false
        }
    }
}