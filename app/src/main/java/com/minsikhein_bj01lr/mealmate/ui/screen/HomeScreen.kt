package com.minsikhein_bj01lr.mealmate.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.component.MealMateIntro
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.ui.theme.*
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthState
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.HomeViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.RecipesCreateViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    AuthenticatedScreen(
        authViewModel = authViewModel,
        navController = navController
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {

            val authState by authViewModel.authState.collectAsState()
            val currentUserId = authViewModel.currentUser?.uid ?: ""
            val email = when (authState) {
                is AuthState.Authenticated -> (authState as AuthState.Authenticated).dbUser.name
                else -> "Guest"
            }
            val displayName = when (authState) {
                is AuthState.Authenticated -> (authState as AuthState.Authenticated).dbUser.name.uppercase()
                else -> "GUEST"
            }
            val context = LocalContext.current
            val viewModel: HomeViewModel = viewModel {
                HomeViewModel { context }
            }
            val uiState by viewModel.uiState.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()

            LaunchedEffect(currentUserId) {
                viewModel.loadHomeScreenState(currentUserId = currentUserId)
            }

            // Header with capitalized & bigger username
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MealMateIntro()

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Welcome back,",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.primary, // Replaced WarmBrown
                        fontWeight = FontWeight.Normal
                    )
                )

                Text(
                    text = displayName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.primary, // Replaced DeepRed
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Stats Cards in one row with two columns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // First Card
                StatCard(
                    title = "Saved Recipes",
                    count = uiState.total_recipes,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    textColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    isLoading = isLoading,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )

                // Second Card
                StatCard(
                    title = "Grocery Items",
                    count = uiState.total_grocery_items,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f),
                    isLoading = isLoading
                )

            }

            // Divider with padding after stats cards
            Spacer(modifier = Modifier.height(16.dp))
            Divider(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), // Replaced WarmBrown
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // User Metadata stacked vertically with full width
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.Top
            ) {
                // User Metadata Card (40%)
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surface, // Replaced CreamyYellow
                            shape = MaterialTheme.shapes.medium
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), // Replaced WarmBrown
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        UserMetadataItem(
                            label = "Last Login",
                            value = formatCompactTimestamp(authViewModel.lastLoginTimestamp)
                        )
                        UserMetadataItem(
                            label = "Member Since",
                            value = formatCompactTimestamp(authViewModel.creationTimestamp)
                        )
                    }
                }

                // Cooking Tip Card (60%)
                CookingTipCard(
                    modifier = Modifier.weight(0.6f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    count: Int,
    backgroundColor: Color,
    textColor: Color,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    borderColor: Color? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (borderColor != null) {
                    Modifier
                        .border(1.dp, borderColor, shape = MaterialTheme.shapes.large)
                } else Modifier
            )
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.large
            )
            .padding(vertical = 20.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = textColor)
            }
        } else {
            Text(
                text = "$count",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = textColor.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
private fun UserMetadataItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.primary, // Replaced DeepRed
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.primary, // Replaced WarmBrown
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun CookingTipCard(
    modifier: Modifier = Modifier
) {
    val tips = listOf(
        "Freeze herbs in olive oil for easy cooking.",
        "A pinch of salt enhances sweet dishes.",
        "Use veggie scraps to make homemade stock.",
        "Store mushrooms in paper bags to prevent moisture."
    )
    val randomTip = remember { tips.random() }

    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface, // Replaced CreamyYellow
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), // Replaced WarmBrown
                shape = MaterialTheme.shapes.medium
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Chef's Tip of the Day",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.primary, // Replaced DeepRed
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = randomTip,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground, // Replaced Neutral10
                    fontStyle = FontStyle.Italic
                )
            )
        }
    }
}

private fun formatCompactTimestamp(timestamp: Long?): String {
    if (timestamp == null) return "N/A"
    val sdf = SimpleDateFormat("MMM dd, yy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}