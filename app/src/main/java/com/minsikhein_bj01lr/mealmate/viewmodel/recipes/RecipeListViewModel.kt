package com.minsikhein_bj01lr.mealmate.viewmodel.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minsikhein_bj01lr.mealmate.data.model.Recipe
import com.minsikhein_bj01lr.mealmate.data.repository.IngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class RecipeListViewModel: ViewModel() {
    private val recipeRepository = RecipeRepository(
        ingredientRepository = IngredientRepository(),
        recipeIngredientRepository = RecipeIngredientRepository()
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
}