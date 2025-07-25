package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.GroceryListItem
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GroceryListItemRepository(
    private val ingredientRepository: IngredientRepository,
    private val recipeRepository: RecipeRepository,
    private val recipeIngredientRepository: RecipeIngredientRepository,
    private val groceryListItemSourceRepository: GroceryListItemSourceRepository,
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val groceryListItemCollection = firestore.collection("groceryListItems")
    private val TAG = "GroceryListItemRepository"

    private suspend fun getOrCreateGroceryListItem(
        groceryListId: String,
        ingredientId: String
    ): GroceryListItem {
        return groceryListItemCollection
            .whereEqualTo("groceryListId", groceryListId)
            .whereEqualTo("ingredientId", ingredientId)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toObject(GroceryListItem::class.java)
            ?: run {
                val newItem = GroceryListItem(
                    id = UUID.randomUUID().toString(),
                    groceryListId = groceryListId,
                    ingredientId = ingredientId,
                    isPurchased = false
                )
                groceryListItemCollection.document(newItem.id).set(newItem).await()
                newItem
            }
    }

    // In GroceryListItemRepository.kt
    suspend fun addRecipeIngredientsToGroceryList(
        groceryListId: String,
        recipeIngredients: List<RecipeIngredientWithDetail>
    ) {
        try {
            // 1. Check for existing sources to prevent duplicates
            val existingSources = groceryListItemSourceRepository.getGroceryItemSources(
                groceryListId,
                recipeIngredients.map { it.recipeIngredient.id }
            )

            if (existingSources != null && !existingSources.isEmpty) {
                Log.d(TAG, "Some ingredients already exist in this grocery list")
                return
            }

            // 2. Process each recipe ingredient
            recipeIngredients.forEach { item ->
                // Find or create base grocery list item
                val groceryItem = getOrCreateGroceryListItem(
                    groceryListId = groceryListId,
                    ingredientId = item.ingredient.id
                )

                // Create source link
                groceryListItemSourceRepository.createGroceryItemSource(
                    groceryItemId = groceryItem.id,
                    recipeIngredientId = item.recipeIngredient.id,
                    groceryListId = groceryListId
                )
            }
            Log.d(TAG, "Added ${recipeIngredients.size} items to grocery list $groceryListId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add recipe ingredients", e)
            throw e
        }
    }

}