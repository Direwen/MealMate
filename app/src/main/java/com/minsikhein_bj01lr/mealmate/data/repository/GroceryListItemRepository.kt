// In GroceryListItemRepository.kt

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

    // Finds existing GroceryListItem or creates a new one
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

    // Adds all ingredients of a recipe to the grocery list
    suspend fun addRecipeIngredientsToGroceryList(
        groceryListId: String,
        recipeIngredients: List<RecipeIngredientWithDetail>
    ) {
        try {
            Log.d(TAG, "Starting import process for ${recipeIngredients.size} ingredients...")

            // 1. Get all existing sources for these recipe ingredients
            val existingSources = groceryListItemSourceRepository.getSourcesByRecipeIngredientIds(
                groceryListId,
                recipeIngredients.map { it.recipeIngredient.id }
            )

            Log.d(TAG, "Found ${existingSources.size} existing sources to potentially remove")

            // 2. Delete existing sources
            if (existingSources.isNotEmpty()) {
                Log.d(TAG, "Deleting ${existingSources.size} existing sources...")
                groceryListItemSourceRepository.deleteSources(existingSources.map { it.id })

                // 3. Find and delete orphaned grocery items
                val orphanedItemIds = existingSources
                    .groupBy { it.groceryItemId }
                    .filter { (itemId, _) ->
                        groceryListItemSourceRepository.getSourcesByGroceryItemId(itemId).isEmpty()
                    }
                    .keys

                if (orphanedItemIds.isNotEmpty()) {
                    Log.d(TAG, "Deleting ${orphanedItemIds.size} orphaned grocery items...")
                    orphanedItemIds.forEach { itemId ->
                        groceryListItemCollection.document(itemId).delete().await()
                    }
                }
            }

            // 4. Add new sources
            Log.d(TAG, "Adding new sources for ${recipeIngredients.size} ingredients...")
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

    // Loads and maps all grocery items to display data
    suspend fun getAllGroceries(
        groceryListId: String
    ): List<GroceryItemDisplay> {
        return try {
            Log.d(TAG, ">>> [getAllGroceries] Start - Fetching grocery items for listId=$groceryListId")

            val groceryItems = groceryListItemCollection
                .whereEqualTo("groceryListId", groceryListId)
                .get()
                .await()
                .toObjects(GroceryListItem::class.java)

            Log.d(TAG, "[getAllGroceries] Retrieved ${groceryItems.size} grocery items")

            if (groceryItems.isEmpty()) {
                Log.d(TAG, "[getAllGroceries] No grocery items found. Returning empty list.")
                return emptyList()
            }

            // Fetch sources
            val sources = groceryListItemSourceRepository.getSourcesByGroceryListId(groceryListId)
            Log.d(TAG, "[getAllGroceries] Retrieved ${sources.size} sources for grocery items")

            // Fetch recipe ingredients
            val recipeIngredientIds = sources.map { it.recipeIngredientId }
            Log.d(TAG, "[getAllGroceries] Extracted ${recipeIngredientIds.size} recipeIngredientIds")

            val recipeIngredients = recipeIngredientIds.chunked(10).flatMap { chunk ->
                Log.d(TAG, "[getAllGroceries] Fetching recipeIngredients chunk: $chunk")
                recipeIngredientRepository.getByIds(chunk)
            }

            Log.d(TAG, "[getAllGroceries] Retrieved ${recipeIngredients.size} recipeIngredients")

            // Fetch ingredient data
            val ingredientIds = recipeIngredients.map { it.ingredientId }.distinct()
            Log.d(TAG, "[getAllGroceries] Extracted ${ingredientIds.size} unique ingredientIds")

            val ingredients = ingredientRepository.getIngredientsByIds(ingredientIds)
            Log.d(TAG, "[getAllGroceries] Retrieved ${ingredients.size} ingredients")

            // Create maps
            val ingredientMap = ingredients.associateBy { it.id }
            val recipeIngredientMap = recipeIngredients.associateBy { it.id }
            val sourcesByItemId = sources.groupBy { it.groceryItemId }

            Log.d(TAG, "[getAllGroceries] Mapping display models for ${groceryItems.size} items")

            groceryItems.map { item ->
                val itemSources = sourcesByItemId[item.id] ?: emptyList()
                Log.d(TAG, "[getAllGroceries] Mapping itemId=${item.id}, ingredientId=${item.ingredientId}, sourceCount=${itemSources.size}")

                val ingredient = ingredientMap[item.ingredientId]
//                var name = ""
//                if (ingredient != null) {
//                    name = ingredient.name
//                }

                if (ingredient == null) {
                    Log.e(TAG, "[getAllGroceries] Ingredient NOT FOUND for itemId=${item.id}, ingredientId=${item.ingredientId}")
                    throw IllegalStateException("Ingredient not found for itemId=${item.id}, ingredientId=${item.ingredientId}")
                }

                val amounts = itemSources.mapNotNull { source ->
                    val amount = recipeIngredientMap[source.recipeIngredientId]?.amount
                    if (amount == null) {
                        Log.w(TAG, "[getAllGroceries] Missing amount for recipeIngredientId=${source.recipeIngredientId}")
                    }
                    amount
                }

                Log.d(TAG, "[getAllGroceries] Finalizing GroceryItemDisplay for itemId=${item.id}")

                GroceryItemDisplay(
                    id = item.id,
                    name = ingredient.name,
                    amounts = amounts,
                    isPurchased = item.purchased
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "!!! [getAllGroceries] Exception thrown while getting groceries for list $groceryListId", e)
            throw e
        }
    }


    // Toggles the purchased status of a grocery item
    suspend fun togglePurchasedStatus(itemId: String) {
        try {
            Log.d(TAG, "Toggling purchased status for itemId=$itemId")
            val item = groceryListItemCollection.document(itemId).get().await()
                .toObject(GroceryListItem::class.java)
            item?.let {
                groceryListItemCollection.document(itemId)
                    .update("isPurchased", !it.purchased)
                    .await()
                Log.d(TAG, "Updated isPurchased to ${!it.purchased}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle purchase status for itemId=$itemId", e)
            throw e
        }
    }
}
