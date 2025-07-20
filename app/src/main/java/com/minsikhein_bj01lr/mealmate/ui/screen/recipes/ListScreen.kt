package com.minsikhein_bj01lr.mealmate.ui.screen.recipes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel

@Composable
fun RecipesListScreen(
    navController: NavController,
    authViewModel: AuthViewModel
){
    AuthenticatedScreen(
        authViewModel = authViewModel,
        navController = navController
    ) {innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text("Recipes")
            Button(
                onClick = {
                    navController.navigate(Routes.RECIPES_CREATE)
                }
            ) {
                Text("Create Recipe")
            }
        }
    }
}