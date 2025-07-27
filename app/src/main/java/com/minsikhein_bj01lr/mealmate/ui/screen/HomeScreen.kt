package com.minsikhein_bj01lr.mealmate.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthState
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.CategoryViewModel


@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    AuthenticatedScreen(
        authViewModel = authViewModel,
        navController = navController
    ) { innerPadding ->

        // Home Screen Height is fixed
        // So Lazy Column is not required to use
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // Getting Auth State from passed authViewMode
            val authState by authViewModel.authState.collectAsState()

            //Extracting Email from the current authenticated user
            val email = when (authState) {
                is AuthState.Authenticated -> (authState as AuthState.Authenticated).dbUser.name
                else -> "Guest"
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //Welcome Text
                Text(
                    text = "Welcome, $email",
                    style = MaterialTheme.typography.headlineMedium
                )


                //Logout Button
                LogoutButton(
                    authViewModel = authViewModel,
                    navController = navController
                )

            }
        }
    }
}

@Composable
fun LogoutButton(authViewModel: AuthViewModel, navController: NavController){
    OutlinedButton(
        onClick = {
            authViewModel.signOut()
            navController.popBackStack()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 4.dp)
    ) {
        Text("Logout")
    }
}