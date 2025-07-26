package com.minsikhein_bj01lr.mealmate.viewmodel.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListItemRepository
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListItemSourceRepository
import com.minsikhein_bj01lr.mealmate.data.repository.IngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class UpdateRecipeUiState(
    val id: String = "", // required for updating
    val title: String = "",
    val instructions: String = "",
    val preparationTime: Int = 1,
    val servings: Int = 1,
    val ingredients: List<IngredientInput> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)


class RecipeUpdateViewModel : ViewModel() {

    private val ingredientRepository = IngredientRepository()
    private val groceryListItemSourceRepository = GroceryListItemSourceRepository()
    private val recipeRepository = RecipeRepository(
        ingredientRepository = ingredientRepository,
        recipeIngredientRepository = RecipeIngredientRepository(ingredientRepository),
        groceryListItemSourceRepository = groceryListItemSourceRepository
    )
    private val recipeIngredientRepository = RecipeIngredientRepository(
        ingredientRepository = ingredientRepository
    )
    private val groceryListItemRepository = GroceryListItemRepository(
        ingredientRepository = ingredientRepository,
        recipeRepository =recipeRepository,
        recipeIngredientRepository = recipeIngredientRepository,
        groceryListItemSourceRepository = groceryListItemSourceRepository
    )

    private val _uiState = MutableStateFlow(UpdateRecipeUiState())
    val uiState: StateFlow<UpdateRecipeUiState> = _uiState

    fun loadRecipeForEditing(recipeId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = "")

        viewModelScope.launch {
            try {
                val (recipe, ingredients, _) = recipeRepository.getRecipeWithIngredients(recipeId)

                if (recipe == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Recipe not found."
                    )
                    return@launch
                }

                _uiState.value = UpdateRecipeUiState(
                    id = recipe.id,
                    title = recipe.title,
                    instructions = recipe.instructions,
                    preparationTime = recipe.preparationTime,
                    servings = recipe.servings,
                    ingredients = ingredients.map {
                        IngredientInput(
                            name = it.ingredient.name,
                            amount = it.recipeIngredient.amount
                        )
                    },
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error loading recipe: ${e.message}"
                )
            }
        }
    }

    fun onUiStateChange(newState: UpdateRecipeUiState) {
        _uiState.value = newState
    }

    fun submitUpdate(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val current = _uiState.value

        _uiState.value = current.copy(isLoading = true, error = "")

        // 🔍 Optional: validation (same as in create)
        if (current.title.isBlank() || current.instructions.isBlank()) {
            _uiState.value = current.copy(
                isLoading = false,
                error = "Please fill in all required fields."
            )
            return
        }

        viewModelScope.launch {
            try {
                recipeRepository.updateRecipeWithIngredients(current, groceryListItemRepository)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = current.copy(
                    isLoading = false,
                    error = "Update failed: ${e.message}"
                )
                onError(e)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
