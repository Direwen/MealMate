package com.minsikhein_bj01lr.mealmate.data.model

data class Ingredient(
    val id: String = "",
    val name: String = "",
    val categoryId: String = "",
)

data class RecipeIngredient(
    val id: String = "",
    val recipeId: String = "",
    val ingredientId: String = "",
    val amount: String = "",
)