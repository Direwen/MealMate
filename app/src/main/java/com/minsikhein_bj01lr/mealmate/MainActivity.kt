package com.minsikhein_bj01lr.mealmate

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.minsikhein_bj01lr.mealmate.ui.screen.LoginScreen
import com.minsikhein_bj01lr.mealmate.ui.screen.RegisterScreen
import com.minsikhein_bj01lr.mealmate.ui.theme.MealMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MealMateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //Navigation Setup
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "screen_login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable ("screen_login"){
                            LoginScreen(navController = navController, modifier = Modifier)
                        }
                        composable("screen_register") {
                            RegisterScreen(navController = navController, modifier = Modifier)
                        }
                    }
                }
            }
        }
    }
}

