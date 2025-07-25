package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.minsikhein_bj01lr.mealmate.data.model.GroceryListItemSource
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GroceryListItemSourceRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val groceryListItemSourceCollection = firestore.collection("groceryListItemSources")
    private val TAG = "GroceryListItemSourceRepository"

    suspend fun createGroceryItemSource(
        groceryItemId: String,
        recipeIngredientId: String,
        groceryListId: String
    ) {
        val source = GroceryListItemSource(
            id = UUID.randomUUID().toString(),
            groceryListId = groceryListId,
            groceryItemId = groceryItemId,
            recipeIngredientId = recipeIngredientId
        )
        groceryListItemSourceCollection.document(source.id).set(source).await()
    }

    suspend fun getGroceryItemSources(
        groceryListId: String,
        recipeIngredientIds: List<String>
    ): QuerySnapshot? {
        return try {
            groceryListItemSourceCollection
                .whereEqualTo("groceryListId", groceryListId)
                .whereIn("recipeIngredientId", recipeIngredientIds)
                .get()
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking existing sources", e)
            null
        }
    }

    suspend fun getSourcesByGroceryListId(
        groceryListId: String
    ): List<GroceryListItemSource> {
        return try {
            groceryListItemSourceCollection
                .whereEqualTo("groceryListId", groceryListId)
                .get()
                .await()
                .toObjects(GroceryListItemSource::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting sources for list $groceryListId", e)
            emptyList()
        }
    }
}