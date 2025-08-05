// File: ui/screen/SplashScreen.kt

package com.minsikhein_bj01lr.mealmate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.component.MealMateIcon
import com.minsikhein_bj01lr.mealmate.ui.component.MealMateIntro
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.ui.theme.CreamyYellow
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthState
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // Collect auth state to observe changes
    val authState = authViewModel.authState.collectAsState().value

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated || authState is AuthState.Unauthenticated) {
            delay(3000)

            when (authState) {
                is AuthState.Authenticated -> {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
                else -> {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            }
        }
    }


    // Display splash UI regardless of auth state until delay finishes
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamyYellow)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MealMateIcon(size = 160.dp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "MealMate",
            style = MaterialTheme.typography.headlineLarge,
            color = DeepRed
        )
    }
}