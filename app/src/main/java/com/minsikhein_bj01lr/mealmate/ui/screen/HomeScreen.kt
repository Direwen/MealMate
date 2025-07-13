package com.minsikhein_bj01lr.mealmate.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthState
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    AuthenticatedScreen(
        authViewModel = authViewModel,
        navController = navController
    ) {
        val authState by authViewModel.authState.collectAsState()

        // Extract user name from AuthState if authenticated
        val userName = when (authState) {
            is AuthState.Authenticated -> (authState as AuthState.Authenticated).user.displayName ?: "User"
            else -> "Guest"
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Greeting
            Text(
                text = "Welcome, $userName",
                style = MaterialTheme.typography.headlineMedium
            )

            // Add Recipe Button
            Button(
                onClick = { /* Navigate to AddRecipeScreen later */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Add New Recipe")
            }

            // Grocery List Button
            Button(
                onClick = { navController.navigate(Routes.HOME) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("View Grocery List")
            }

            // Delegation Button
            Button(
                onClick = { /* Implement SMS delegation logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Delegate Shopping List")
            }

            // Settings / Profile Button
            Button(
                onClick = { /* Navigate to settings or profile screen */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Profile")
            }

            // Logout Button
            OutlinedButton(
                onClick = {
                    authViewModel.signOut()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Logout")
            }
        }
    }
}