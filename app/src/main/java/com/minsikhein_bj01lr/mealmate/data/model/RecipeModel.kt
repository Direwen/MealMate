package com.minsikhein_bj01lr.mealmate.data.model

import java.util.Date

data class Recipe(
    val id: String = "",
    val creatorId: String = "", // User ID who created it
    val title: String = "",
    val instructions: String = "",
    val preparationTime: Int = 3, // in minutes
    val servings: Int = 1,
    val imagePath: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)