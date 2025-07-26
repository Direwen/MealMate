package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.GroceryListItem
import com.minsikhein_bj01lr.mealmate.viewmodel.groceries.GroceryItemDisplay
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

    private suspend fun isOrphanedItem(itemId: String): Boolean {
        return groceryListItemSourceRepository.getSourcesByGroceryItemId(itemId).isEmpty()
    }

    private suspend fun getOrCreateGroceryListItem(
        groceryListId: String,
        ingredientId: String
    ): GroceryListItem {
        Log.d(TAG, "Searching for existing grocery item for ingredientId=$ingredientId in listId=$groceryListId")
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
                Log.d(TAG, "No existing grocery item found. Creating new item.")
                val newItem = GroceryListItem(
                    id = UUID.randomUUID().toString(),
                    groceryListId = groceryListId,
                    ingredientId = ingredientId,
                    purchased = false
                )
                groceryListItemCollection.document(newItem.id).set(newItem).await()
                newItem
            }
    }

    suspend fun addRecipeIngredientsToGroceryList(
        groceryListId: String,
        recipeIngredients: List<RecipeIngredientWithDetail>
    ) {
        try {
            Log.d(TAG, "Starting import process for ${recipeIngredients.size} ingredients...")

            // Get and delete existing sources
            val existingSources = groceryListItemSourceRepository.getSourcesByRecipeIngredientIds(
                groceryListId,
                recipeIngredients.map { it.recipeIngredient.id }
            )

            if (existingSources.isNotEmpty()) {
                Log.d(TAG, "Deleting ${existingSources.size} existing sources...")
                groceryListItemSourceRepository.deleteSources(existingSources.map { it.id })

                // Clean up orphaned items
                val orphanedItemIds = existingSources
                    .groupBy { it.groceryItemId }
                    .filter { (itemId, _) -> isOrphanedItem(itemId) }
                    .keys

                if (orphanedItemIds.isNotEmpty()) {
                    Log.d(TAG, "Deleting ${orphanedItemIds.size} orphaned grocery items...")
                    orphanedItemIds.forEach { itemId ->
                        deleteGroceryItem(itemId)
                    }
                }
            }

            // Add new sources
            recipeIngredients.forEach { item ->
                val groceryItem = getOrCreateGroceryListItem(
                    groceryListId = groceryListId,
                    ingredientId = item.ingredient.id
                )

                groceryListItemSourceRepository.createGroceryItemSource(
                    groceryItemId = groceryItem.id,
                    recipeIngredientId = item.recipeIngredient.id,
                    groceryListId = groceryListId
                )
            }

            Log.d(TAG, "Successfully reimported ${recipeIngredients.size} ingredients")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reimport recipe ingredients", e)
            throw e
        }
    }

    suspend fun getAllGroceries(groceryListId: String): List<GroceryItemDisplay> {
        return try {
            // Get all items and filter out orphans
            val groceryItems = groceryListItemCollection
                .whereEqualTo("groceryListId", groceryListId)
                .get()
                .await()
                .toObjects(GroceryListItem::class.java)
                .filterNot { item ->
                    isOrphanedItem(item.id).also { isOrphan ->
                        if (isOrphan) {
                            Log.d(TAG, "Auto-removing orphaned item ${item.id}")
                            deleteGroceryItem(item.id)
                        }
                    }
                }

            if (groceryItems.isEmpty()) {
                Log.d(TAG, "[getAllGroceries] No valid grocery items found")
                return emptyList()
            }

            // Get related data
            val sources = groceryListItemSourceRepository.getSourcesByGroceryListId(groceryListId)
            val recipeIngredients = recipeIngredientRepository.getByIds(
                sources.map { it.recipeIngredientId }
            )
            val ingredients = ingredientRepository.getIngredientsByIds(
                recipeIngredients.map { it.ingredientId }.distinct()
            )

            // Create mappings
            val ingredientMap = ingredients.associateBy { it.id }
            val recipeIngredientMap = recipeIngredients.associateBy { it.id }
            val sourcesByItemId = sources.groupBy { it.groceryItemId }

            // Map to display models
            groceryItems.mapNotNull { item ->
                val itemSources = sourcesByItemId[item.id] ?: emptyList()
                val ingredient = ingredientMap[item.ingredientId]
                    ?: run {
                        Log.e(TAG, "Ingredient not found for item ${item.id}")
                        return@mapNotNull null
                    }

                GroceryItemDisplay(
                    id = item.id,
                    name = ingredient.name,
                    amounts = itemSources.mapNotNull {
                        recipeIngredientMap[it.recipeIngredientId]?.amount
                    },
                    isPurchased = item.purchased
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting groceries", e)
            throw e
        }
    }

    suspend fun togglePurchasedStatus(itemId: String) {
        try {
            val item = groceryListItemCollection.document(itemId).get().await()
                .toObject(GroceryListItem::class.java)
            item?.let {
                groceryListItemCollection.document(itemId)
                    .update("purchased", !it.purchased)
                    .await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle purchase status", e)
            throw e
        }
    }

    suspend fun deleteGroceryItem(itemId: String) {
        try {
            groceryListItemCollection.document(itemId).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete grocery item", e)
        }
    }
}