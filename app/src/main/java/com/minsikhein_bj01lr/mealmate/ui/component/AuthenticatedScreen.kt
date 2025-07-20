package com.minsikhein_bj01lr.mealmate.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthState
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.NavigationBarItemDefaults
import com.minsikhein_bj01lr.mealmate.ui.theme.CreamyYellow
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed
import com.minsikhein_bj01lr.mealmate.ui.theme.Neutral90
import com.minsikhein_bj01lr.mealmate.ui.theme.SoftCreamyYellow

@Composable
fun AuthenticatedScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Authenticated -> {
            val items = listOf(
                NavItem("Home", Icons.Default.Home, Routes.HOME),
                NavItem("Recipes", Icons.Default.Favorite, Routes.RECIPES_LIST),
                NavItem("Groceries", Icons.Default.ShoppingCart, Routes.GROCERIES_LIST),
            )

            Scaffold(
                modifier = Modifier.fillMaxSize().background(SoftCreamyYellow),
                containerColor = SoftCreamyYellow,
                bottomBar = {
                    NavigationBar(
                        modifier = Modifier,
                        containerColor = DeepRed, // Background color of the bottom nav bar
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        items.forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.route,
                                onClick = {
                                    navController.navigate(item.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        color = SoftCreamyYellow
                                    )
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = if (currentRoute == item.route) DeepRed else SoftCreamyYellow
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = SoftCreamyYellow, // Set the background color for selected item
                                    selectedIconColor = SoftCreamyYellow,
                                    unselectedIconColor = Neutral90,
                                    selectedTextColor = SoftCreamyYellow,
                                    unselectedTextColor = Neutral90
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(SoftCreamyYellow)
                ) {
                    content(innerPadding)
                }
            }
        }

        else -> {
            LaunchedEffect(Unit) {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)