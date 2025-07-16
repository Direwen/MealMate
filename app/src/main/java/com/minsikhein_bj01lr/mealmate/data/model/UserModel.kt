package com.minsikhein_bj01lr.mealmate.data.model

import java.util.Date

data class User(
    val id: String = "",
    val name: String,
    val email: String,
    val createdAt: Date = Date()
)