package com.minsikhein_bj01lr.mealmate.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.minsikhein_bj01lr.mealmate.data.model.Recipe
import com.minsikhein_bj01lr.mealmate.data.model.RecipeIngredient
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.CreateRecipeUiState
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.UpdateRecipeUiState
import kotlinx.coroutines.tasks.await
import java.util.*

class RecipeRepository(
    private val ingredientRepository: IngredientRepository,
    private val recipeIngredientRepository: RecipeIngredientRepository,
    private val groceryListItemSourceRepository: GroceryListItemSourceRepository,
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

    suspend fun getRecipesByCreatorId(creatorId: String): List<Recipe>? {
        return try {
            val snapshot = recipeCollection
                .whereEqualTo("creatorId", creatorId)
                .get()
                .await()

            snapshot.toObjects(Recipe::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recipes for user=$creatorId", e)
            null
        }
    }

    suspend fun getRecipeWithIngredients(recipeId: String): Triple<Recipe?, List<RecipeIngredientWithDetail>, List<String>> {
        return try {
            //Get Recipe Document
            val recipeDoc = recipeCollection.document(recipeId).get().await()
            //Turn Document into Recipe model
            val recipe = recipeDoc.toObject(Recipe::class.java)
            //Get Ingredients included in this Recipe
            val recipeIngredients = recipeIngredientRepository.getRecipeIngredients(recipeId) ?: emptyList()
            //List those ingredient ids
            val ingredientIds = recipeIngredients.map { it.ingredientId }
            //Get Info of those ingredients
            val ingredients = ingredientRepository.getIngredientsByIds(ingredientIds)
            //Turn the list of Ingredient objects into a map, where the key is each ingredient’s id
            //From List<Ingredient> to Map<String, Ingredient>
            val ingredientsById = ingredients.associateBy { it.id }
            //Enriching the raw join table (RecipeIngredient) with the actual data (Ingredient)
            val enriched = recipeIngredients.mapNotNull { ri ->
                val ing = ingredientsById[ri.ingredientId]
                ing?.let {
                    RecipeIngredientWithDetail(recipeIngredient = ri, ingredient = it)
                }
            }

            Triple(recipe, enriched, ingredientIds)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get recipe with full ingredients", e)
            Triple(null, emptyList(), emptyList())
        }
    }

    suspend fun updateRecipeWithIngredients(uiState: UpdateRecipeUiState, groceryListItemRepository: GroceryListItemRepository) {
        val recipeId = uiState.id

        try {
            // 1. Update recipe metadata - FIXED UPDATE SYNTAX
            val updates = mapOf(
                "title" to uiState.title,
                "instructions" to uiState.instructions,
                "preparationTime" to uiState.preparationTime,
                "servings" to uiState.servings,
                "updatedAt" to Date()
            )
            recipeCollection.document(recipeId).update(updates).await()

            // 2. Get current ingredients
            val currentIngredients = recipeIngredientRepository.getRecipeIngredients(recipeId) ?: emptyList()

            // 3. Create maps for comparison
            val currentIngredientMap = currentIngredients.associate {
                ingredientRepository.getIngredientById(it.ingredientId)?.name to it
            }.filterKeys { it != null }.mapKeys { it.key!! }

            // 4. Process deletions
            uiState.ingredients.map { it.name }.let { newNames ->
                currentIngredientMap.filterKeys { it !in newNames }.values.forEach { toRemove ->
                    // Delete from recipe ingredients
                    recipeIngredientRepository.deleteIngredientRecipe(toRemove.id)

                    // Clean up grocery list links
                    groceryListItemSourceRepository.deleteSourcesByRecipeIngredientId(toRemove.id)

                    // Optional: Delete orphaned grocery items
                    val sources = groceryListItemSourceRepository.getSourcesByRecipeIngredientId(toRemove.id)
                    sources.firstOrNull()?.groceryItemId?.let { itemId ->
                        val remainingSources = groceryListItemSourceRepository.getSourcesByGroceryItemId(itemId)
                        if (remainingSources.size <= 1) { // Only this source or none
                            groceryListItemRepository.deleteGroceryItem(itemId)
                        }
                    }
                }
            }

            // 5. Process additions
            uiState.ingredients.forEach { newIngredient ->
                if (!currentIngredientMap.containsKey(newIngredient.name)) {
                    ingredientRepository.getOrCreateIngredient(newIngredient.name)?.let { ingredient ->
                        val recipeIngredient = RecipeIngredient(
                            id = UUID.randomUUID().toString(),
                            recipeId = recipeId,
                            ingredientId = ingredient.id,
                            amount = newIngredient.amount
                        )
                        recipeIngredientRepository.createIngredientRecipe(recipeIngredient)
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to update recipe with ingredients", e)
            throw e
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
