package com.minsikhein_bj01lr.mealmate.data.model

data class Ingredient(
    val id: String = "",
    val name: String = "",
)

data class RecipeIngredient(
    val id: String = "",
    val recipeId: String = "",
    val ingredientId: String = "",
    val amount: String = "",
)

data class GroceryListItem(
    val id: String = "",
    val groceryListId: String = "",
    val ingredientId: String = "",  // Reference to the base ingredient
    val purchased: Boolean = false
)

data class GroceryListItemSource(
    val id: String = "",
    val groceryListId: String = "",  // Added for direct filtering
    val groceryItemId: String = "",
    val recipeIngredientId: String = ""
)

