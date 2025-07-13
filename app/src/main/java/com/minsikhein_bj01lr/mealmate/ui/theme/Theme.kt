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
    onPrimary = Color.White,
    primaryContainer = WarmBrown,
    onPrimaryContainer = Color.Black,

    secondary = SoftOrange,
    onSecondary = Color.White,
    secondaryContainer = CreamyYellow,
    onSecondaryContainer = Color.Black,

    tertiary = WarmBrown,
    background = Neutral90,
    onBackground = Neutral10,
    surface = Neutral100,
    onSurface = Neutral10,
    inverseSurface = Neutral10,
    onSurfaceVariant = Neutral10
)

private val DarkColorScheme = darkColorScheme(
    primary = SoftOrange,
    onPrimary = Color.Black,
    primaryContainer = DeepRed,
    onPrimaryContainer = Color.White,

    secondary = WarmBrown,
    onSecondary = Color.White,
    secondaryContainer = DeepRed,
    onSecondaryContainer = Color.White,

    tertiary = WarmBrown,
    background = Neutral10,
    onBackground = Neutral90,
    surface = Neutral0,
    onSurface = Neutral100,
    inverseSurface = Neutral100,
    onSurfaceVariant = Neutral90
)

@Composable
fun MealMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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