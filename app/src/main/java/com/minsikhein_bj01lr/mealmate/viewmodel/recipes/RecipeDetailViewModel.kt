package com.minsikhein_bj01lr.mealmate.viewmodel.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minsikhein_bj01lr.mealmate.data.model.Ingredient
import com.minsikhein_bj01lr.mealmate.data.model.Recipe
import com.minsikhein_bj01lr.mealmate.data.repository.IngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientWithDetail
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RecipeDetailUiState {
    object Loading : RecipeDetailUiState()
    data class Success(
        val recipe: Recipe,
        val ingredients: List<RecipeIngredientWithDetail>,
        val ingredientIds: List<String>
    ) : RecipeDetailUiState()

    data class Error(val message: String) : RecipeDetailUiState()
}


class RecipeDetailViewModel : ViewModel() {

    private val ingredientRepository = IngredientRepository()
    private val recipeRepository = RecipeRepository(
        ingredientRepository = ingredientRepository,
        recipeIngredientRepository = RecipeIngredientRepository(ingredientRepository = ingredientRepository)
    )
    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipeDetails(recipeId: String) {
        _uiState.value = RecipeDetailUiState.Loading

        viewModelScope.launch {
            try {
                val (recipe, ingredients, ingredientIds) = recipeRepository.getRecipeWithIngredients(recipeId)

                if (recipe == null) {
                    _uiState.value = RecipeDetailUiState.Error("Recipe not found.")
                    return@launch
                }

                _uiState.value = RecipeDetailUiState.Success(
                    recipe = recipe,
                    ingredients = ingredients,
                    ingredientIds = ingredientIds
                )
            } catch (e: Exception) {
                _uiState.value = RecipeDetailUiState.Error("Something went wrong: ${e.message}")
            }
        }
    }
}

