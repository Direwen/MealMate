package com.minsikhein_bj01lr.mealmate.viewmodel.groceries

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListItemRepository
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListItemSourceRepository
import com.minsikhein_bj01lr.mealmate.data.repository.GroceryListRepository
import com.minsikhein_bj01lr.mealmate.data.repository.IngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeIngredientRepository
import com.minsikhein_bj01lr.mealmate.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "GroceryVM"

class GroceryListViewModel : ViewModel() {

    private val ingredientRepository = IngredientRepository()
    private val groceryListRepository = GroceryListRepository()
    private val groceryListItemSourceRepository = GroceryListItemSourceRepository()
    private val recipeRepository = RecipeRepository(
        ingredientRepository = ingredientRepository,
        recipeIngredientRepository = RecipeIngredientRepository(ingredientRepository),
        groceryListItemSourceRepository = groceryListItemSourceRepository,
    )
    private val recipeIngredientRepository = RecipeIngredientRepository(ingredientRepository)
    private val groceryListItemRepository = GroceryListItemRepository(
        ingredientRepository,
        recipeRepository,
        recipeIngredientRepository,
        groceryListItemSourceRepository
    )

    private val _uiState = MutableStateFlow<GroceryListUiState>(GroceryListUiState.Loading)
    val uiState: StateFlow<GroceryListUiState> = _uiState.asStateFlow()

    private val _viewState = MutableStateFlow(GroceryListViewState())
    val viewState: StateFlow<GroceryListViewState> = _viewState.asStateFlow()

    fun loadGroceries(currentUserId: String, forceRefresh: Boolean = false) {
        Log.d(TAG, "loadGroceries called with userId=$currentUserId, forceRefresh=$forceRefresh")

        _uiState.value = GroceryListUiState.Loading
        _viewState.update {
            it.copy(isLoading = !forceRefresh, isRefreshing = forceRefresh, error = null)
        }

        viewModelScope.launch {
            try {
                Log.d(TAG, "Fetching grocery list for user...")
                val groceryList = groceryListRepository.getOrCreateGroceryList(currentUserId)

                if (groceryList == null) {
                    Log.e(TAG, "Failed to get or create grocery list.")
                    _uiState.value = GroceryListUiState.Error("Failed to load grocery list")
                    _viewState.update {
                        it.copy(isLoading = false, isRefreshing = false, error = "Failed to load grocery list")
                    }
                    return@launch
                }

                Log.d(TAG, "Grocery list fetched: ${groceryList.id}")
                val groceryItems = groceryListItemRepository.getAllGroceries(groceryList.id)
                Log.d(TAG, "Fetched ${groceryItems.size} grocery items with recipe sources")

                val purchasedCount = groceryItems.count { it.isPurchased }

                _uiState.value = GroceryListUiState.Success(
                    items = groceryItems,
                    totalItems = groceryItems.size,
                    purchasedCount = purchasedCount
                )

                _viewState.update {
                    it.copy(
                        items = groceryItems,
                        isLoading = false,
                        isRefreshing = false,
                        totalItems = groceryItems.size,
                        purchasedCount = purchasedCount
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error while loading groceries: ${e.message}", e)
                _uiState.value = GroceryListUiState.Error("Error: ${e.message}")
                _viewState.update {
                    it.copy(isLoading = false, isRefreshing = false, error = "Error: ${e.message}")
                }
            }
        }
    }

    fun togglePurchasedStatus(itemId: String) {
        Log.d(TAG, "Toggling purchase status for itemId=$itemId")

        val previousState = _viewState.value
        _viewState.update { currentState ->
            val updatedItems = currentState.items.map { item ->
                if (item.id == itemId) item.copy(isPurchased = !item.isPurchased)
                else item
            }
            currentState.copy(
                items = updatedItems,
                purchasedCount = updatedItems.count { it.isPurchased }
            )
        }

        viewModelScope.launch {
            try {
                groceryListItemRepository.togglePurchasedStatus(itemId)
                Log.d(TAG, "Purchase status toggled successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to toggle status: ${e.message}", e)
                _viewState.value = previousState.copy(
                    error = "Failed to update: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        Log.d(TAG, "Clearing error")
        _viewState.update { it.copy(error = null) }
    }

    fun refresh(currentUserId: String) {
        Log.d(TAG, "Refreshing grocery list...")
        loadGroceries(currentUserId, forceRefresh = true)
    }
}

// Data classes moved to a separate file (recommended)
data class GroceryItemDisplay(
    val id: String,
    val name: String,
    val amounts: List<String>,
    val recipeSources: List<RecipeSource>,
    val isPurchased: Boolean
)

data class RecipeSource(
    val recipeName: String,
    val amount: String
)

sealed class GroceryListUiState {
    object Loading : GroceryListUiState()
    data class Success(
        val items: List<GroceryItemDisplay>,
        val totalItems: Int,
        val purchasedCount: Int
    ) : GroceryListUiState()
    data class Error(val message: String) : GroceryListUiState()
}

data class GroceryListViewState(
    val items: List<GroceryItemDisplay> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalItems: Int = 0,
    val purchasedCount: Int = 0,
    val isRefreshing: Boolean = false
)