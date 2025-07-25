package com.minsikhein_bj01lr.mealmate.viewmodel.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minsikhein_bj01lr.mealmate.data.model.Recipe
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListItemRepository
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListItemSourceRepository
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListRepository
import com.minsikhein_bj01lr.mealmate.data.repository.IngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientWithDetail
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeRepository
import kotlinx.coroutines.delay
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

// Add new sealed class for import states
sealed class ImportState {
    object Idle : ImportState()
    object Loading : ImportState()
    object Success : ImportState()
    data class Error(val message: String) : ImportState()
}



class RecipeDetailViewModel : ViewModel() {

    //Repositories
    private val ingredientRepository = IngredientRepository()
    private val groceryListRepository = GroceryListRepository()
    private val groceryListItemSourceRepository = GroceryListItemSourceRepository()
    private val recipeRepository = RecipeRepository(
        ingredientRepository = ingredientRepository,
        recipeIngredientRepository = RecipeIngredientRepository(ingredientRepository = ingredientRepository)
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

    //States
    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()
    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState.asStateFlow()


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

    fun importIngredientsToGroceryList(currentUserId: String) {
        _importState.value = ImportState.Loading

        viewModelScope.launch {
            try {
                // 1. Get or create grocery list for user
                val groceryList = groceryListRepository.getOrCreateGroceryList(currentUserId)
                if (groceryList == null) {
                    _importState.value = ImportState.Error("Failed to access grocery list")
                    return@launch
                }

                // 2. Get current recipe ingredients
                val currentState = _uiState.value
                if (currentState !is RecipeDetailUiState.Success) {
                    _importState.value = ImportState.Error("Recipe data not loaded")
                    return@launch
                }

                // 3. Import ingredients
                groceryListItemRepository.addRecipeIngredientsToGroceryList(
                    groceryListId = groceryList.id,
                    recipeIngredients = currentState.ingredients
                )

                _importState.value = ImportState.Success
            } catch (e: Exception) {
                _importState.value = ImportState.Error("Import failed: ${e.message}")
            }
        }
    }
}

