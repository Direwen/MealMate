package com.minsikhein_bj01lr.mealmate.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed

@Composable
fun MealMateIntro(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MealMateIcon(size = 96.dp)

        Text(
            text = "MealMate",
            style = MaterialTheme.typography.headlineLarge,
            color = DeepRed
        )

        Text(
            text = "Your personal cooking companion",
            style = MaterialTheme.typography.bodyMedium,
            color = DeepRed
        )
    }
}
