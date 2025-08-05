package com.minsikhein_bj01lr.mealmate.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.minsikhein_bj01lr.mealmate.ui.theme.CreamyYellow
import com.minsikhein_bj01lr.mealmate.R
import com.minsikhein_bj01lr.mealmate.ui.theme.DeepRed

@Composable
fun MealMateIcon(size: Dp = 64.dp) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(colorScheme.primary), // was DeepRed
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.chef_hat),
            contentDescription = null,
            tint = colorScheme.background, // was CreamyYellow
            modifier = Modifier.size(size / 2)
        )
    }
}
