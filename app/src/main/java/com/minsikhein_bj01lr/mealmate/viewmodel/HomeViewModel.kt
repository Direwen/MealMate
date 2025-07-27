package com.minsikhein_bj01lr.mealmate.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListItemRepository
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListItemSourceRepository
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListRepository
import com.minsikhein_bj01lr.mealmate.data.repository.IngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeRepository
import com.minsikhein_bj01lr.mealmate.data.util.ImageStorageHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeScreenUiState(
    val total_recipes: Int = 0,
    val total_grocery_items: Int = 0
)

// Update HomeViewModel.kt

class HomeViewModel(
    private val contextProvider: () -> Context
): ViewModel() {

    private val ingredientRepository = IngredientRepository()
    private val groceryListRepository = GroceryListRepository()
    private val groceryListItemSourceRepository = GroceryListItemSourceRepository()
    private val recipeIngredientRepository = RecipeIngredientRepository(
        ingredientRepository = ingredientRepository
    )
    private val recipeRepository = RecipeRepository(
        ingredientRepository = ingredientRepository,
        recipeIngredientRepository = recipeIngredientRepository,
        groceryListItemSourceRepository = groceryListItemSourceRepository,
        imageStorageHelper = ImageStorageHelper(contextProvider().applicationContext)
    )
    private val groceryListItemRepository = GroceryListItemRepository(
        ingredientRepository = ingredientRepository,
        recipeRepository = recipeRepository,
        recipeIngredientRepository = recipeIngredientRepository,
        groceryListItemSourceRepository = groceryListItemSourceRepository
    )

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadHomeScreenState(currentUserId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch recipes count
                val recipes = recipeRepository.getRecipesByCreatorId(currentUserId)
                val recipesCount = recipes?.size ?: 0

                // Get Grocery List
                val groceryList = groceryListRepository.getOrCreateGroceryList(creatorId = currentUserId)

                // Fetch grocery items count
                val groceryItems = groceryListItemRepository.getAllGroceries(
                    groceryListId = groceryList?.id ?: ""
                )
                val groceryItemsCount = groceryItems.size

                _uiState.value = HomeScreenUiState(
                    total_recipes = recipesCount,
                    total_grocery_items = groceryItemsCount
                )
            } catch (e: Exception) {
                // Handle error (you might want to show an error state)
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        fun provideFactory(contextProvider: () -> Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(contextProvider) as T
                }
            }
        }
    }
}