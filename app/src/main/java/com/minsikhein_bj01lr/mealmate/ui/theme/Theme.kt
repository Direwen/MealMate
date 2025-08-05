package com.minsikhein_bj01lr.mealmate.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = DeepRed,
    onPrimary = Neutral100,
    background = SoftCreamyYellow,
    secondary = CreamyYellow,
    onSecondary = DeepRed,
    onBackground = Neutral0,
    surface = Neutral100,
    onSurface = Neutral0,
)

private val DarkColorScheme = darkColorScheme(
    primary = SoftOrange,
    onPrimary = Neutral10,
    background = Neutral10,
    onBackground = Neutral100,
    secondary = Color(0xFF2B2B2B),
    onSecondary = CreamyYellow,
    surface = Color(0xFF2B2B2B),
    onSurface = Neutral100,
)


@Composable
fun MealMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}