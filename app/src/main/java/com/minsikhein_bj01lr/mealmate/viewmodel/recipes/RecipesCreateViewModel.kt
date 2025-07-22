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
    val ingredients: List<IngredientInput> = emptyList<IngredientInput>(),
    val isLoading: Boolean = false,
    val error: String = ""
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

    private fun setLoading(isLoading: Boolean) {
        _createRecipeUiState.value = _createRecipeUiState.value.copy(isLoading = isLoading)
    }

    private fun setError(error: String) {
        _createRecipeUiState.value = _createRecipeUiState.value.copy(error = error)
    }

    fun submitRecipe(currentUserId: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        setLoading(true)
        setError("") // Clear previous error

        val currentState = _createRecipeUiState.value

        // 🔍 Validation Step
        when {
            currentState.title.isBlank() -> {
                setError("Title cannot be empty")
                setLoading(false)
                return
            }

            currentState.instructions.isBlank() -> {
                setError("Instructions cannot be empty")
                setLoading(false)
                return
            }

            currentState.ingredients.isEmpty() -> {
                setError("At least one ingredient is required")
                setLoading(false)
                return
            }

            currentState.preparationTime < 1 -> {
                setError("Preparation time must be at least 1 minute")
                setLoading(false)
                return
            }

            currentState.servings < 1 -> {
                setError("Servings must be at least 1")
                setLoading(false)
                return
            }

            currentState.preparationTime > 2880 -> {
                setError("Preparation time can't exceed 48 hours (2880 mins)")
                setLoading(false)
                return
            }

            currentState.servings > 50 -> {
                setError("Servings can't be more than 50")
                setLoading(false)
                return
            }

            else -> {
                // All validations passed, proceed with submission
                viewModelScope.launch {
                    try {
                        recipeRepository.createRecipeWithIngredients(
                            uiState = currentState,
                            creatorId = currentUserId
                        )
                        onSuccess()
                    } catch (e: Exception) {
                        setError("Failed to submit recipe: ${e.message}")
                        onError(e)
                    } finally {
                        setLoading(false)
                    }
                }
            }
        }
    }

}