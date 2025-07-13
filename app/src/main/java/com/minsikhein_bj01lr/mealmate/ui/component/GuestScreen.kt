package com.minsikhein_bj01lr.mealmate.ui.component

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthState
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel

@Composable
fun GuestScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    content: @Composable () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Unauthenticated -> content()
        else -> {
            LaunchedEffect(Unit) {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

}
