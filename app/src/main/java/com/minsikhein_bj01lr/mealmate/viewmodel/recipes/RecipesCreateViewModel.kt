package com.minsikhein_bj01lr.mealmate.viewmodel.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.minsikhein_bj01lr.mealmate.data.repository.IngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class IngredientInput(
    var name: String = "",
    var amount: String = ""
)

data class CreateRecipeUiState(
    val title: String = "",
    val instructions: String = "",
    val preparationTime: Int = 1,
    val servings: Int = 1,
    val ingredients: List<IngredientInput> = emptyList<IngredientInput>()
)

class RecipesCreateViewModel : ViewModel() {
    private val recipeRepository = RecipeRepository(
        ingredientRepository = IngredientRepository(),
        recipeIngredientRepository = RecipeIngredientRepository()
    )
    private val _createRecipeUiState = MutableStateFlow(CreateRecipeUiState())
    val createRecipeUiState: StateFlow<CreateRecipeUiState> = _createRecipeUiState

    fun onUiStateChange(newState: CreateRecipeUiState) {
        _createRecipeUiState.value = newState
    }

    fun submitRecipe(currentUserId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                recipeRepository.createRecipeWithIngredients(
                    uiState = _createRecipeUiState.value,
                    creatorId = currentUserId
                )
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}