package com.minsikhein_bj01lr.mealmate

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.minsikhein_bj01lr.mealmate.ui.navigation.MealMateNavHost
import com.minsikhein_bj01lr.mealmate.ui.theme.MealMateTheme
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //init
        val authViewModel = AuthViewModel()
        enableEdgeToEdge()
        setContent {
            MealMateTheme {
                //Navigation Setup
                val navController = rememberNavController()
                MealMateNavHost(
                    navController = navController,
                    modifier = Modifier.padding(4.dp),
                    authViewModel = authViewModel
                )
            }
        }
    }
}

