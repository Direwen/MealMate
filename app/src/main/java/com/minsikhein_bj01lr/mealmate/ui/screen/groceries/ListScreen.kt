package com.minsikhein_bj01lr.mealmate.ui.screen.groceries

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel

@Composable
fun GroceriesListScreen(
    navController: NavController,
    authViewModel: AuthViewModel
){
    AuthenticatedScreen(
        authViewModel = authViewModel,
        navController = navController
    ) {
        Text("Groceries")
    }
}