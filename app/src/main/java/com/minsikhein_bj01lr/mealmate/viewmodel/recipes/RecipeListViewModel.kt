package com.minsikhein_bj01lr.mealmate.viewmodel.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minsikhein_bj01lr.mealmate.data.model.Recipe
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListItemRepository
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListItemSourceRepository
import com.minsikhein_bj01lr.mealmate.data.repository.IngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class RecipeListViewModel: ViewModel() {
    private val ingredientRepository = IngredientRepository()
    private val groceryListItemSourceRepository = GroceryListItemSourceRepository()
    private val recipeIngredientRepository = RecipeIngredientRepository(
        ingredientRepository = ingredientRepository
    )
    private val recipeRepository = RecipeRepository(
        ingredientRepository = ingredientRepository,
        recipeIngredientRepository = recipeIngredientRepository,
        groceryListItemSourceRepository = groceryListItemSourceRepository
    )
    private val groceryListItemRepository = GroceryListItemRepository(
        ingredientRepository = ingredientRepository,
        recipeRepository =recipeRepository,
        recipeIngredientRepository = recipeIngredientRepository,
        groceryListItemSourceRepository = groceryListItemSourceRepository
    )
    private var _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes = _recipes.asStateFlow()


    fun getRecipes(currentUserId: String) {
        viewModelScope.launch {
            try {
                val result = recipeRepository.getRecipesByCreatorId(currentUserId)
                _recipes.value = result ?: emptyList()
            } catch (e: Exception) {

            }
        }
    }

    fun deleteRecipe(
        recipeId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val success = recipeRepository.deleteRecipe(recipeId, groceryListItemRepository)
                if (success) {
                    // Remove from local state
                    _recipes.value = _recipes.value.filter { it.id != recipeId }
                    onSuccess()
                } else {
                    onError(Exception("Failed to delete recipe"))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun removeRecipeLocally(recipeId: String) {
        _recipes.value = _recipes.value.filter { it.id != recipeId }
    }
}