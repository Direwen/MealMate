package com.minsikhein_bj01lr.mealmate.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthState
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel

@Composable
fun AuthenticatedScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    content: @Composable () -> Unit
) {

    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Authenticated -> content()
        else -> {

            //With 'Unit' in LaunchedEffect, it runs this code once when this composable appears on screen,
            LaunchedEffect(Unit) {
                navController.navigate(Routes.LOGIN) {
                    // Clears any existing stack
                    popUpTo(Routes.LOGIN) { inclusive = true }
                    // Prevent re-launching the same destination repeatedly
                    launchSingleTop = true
                }
            }
        }
    }
}