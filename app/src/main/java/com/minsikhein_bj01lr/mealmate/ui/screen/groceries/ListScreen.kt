package com.minsikhein_bj01lr.mealmate.ui.screen.groceries

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.component.LoadingScreen
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.ui.theme.*
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.groceries.GroceryListViewModel

@Composable
fun GroceriesListScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: GroceryListViewModel = viewModel()
) {
    val currentUserId = authViewModel.currentUser?.uid ?: return
    val state by viewModel.viewState.collectAsState()

    LaunchedEffect(currentUserId) {
        viewModel.loadGroceries(currentUserId)
    }

    AuthenticatedScreen(
        authViewModel = authViewModel,
        navController = navController
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(SoftCreamyYellow)
        ) {
            // Header row
            Text(
                text = "My Grocery List",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = DeepRed
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoadingScreen(isLoading = state.isLoading && !state.isRefreshing) {
                Column {
                    // Grocery stats (unchanged)
                    if (state.totalItems > 0) {
                        val percent = (state.purchasedCount.toFloat() / state.totalItems).coerceIn(0f, 1f)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = CreamyYellow),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Your Grocery Progress",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = DeepRed
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                LinearProgressIndicator(
                                    progress = percent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = DeepRed,
                                    trackColor = WarmBrown.copy(alpha = 0.2f)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("🧾 Total: ${state.totalItems}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = WarmBrown)
                                    Text("✅ Purchased: ${state.purchasedCount}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = WarmBrown)
                                    Text(
                                        "${(percent * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (percent == 1f) Color(0xFF43A047) else DeepRed
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Error block (unchanged)
                    state.error?.let { error ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(error, color = DeepRed)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.clearError()
                                    viewModel.refresh(currentUserId)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SoftOrange)
                            ) {
                                Text("Retry")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Main content - Updated grocery items list
                    if (state.items.isEmpty()) {
                        // Empty state (unchanged)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocalGroceryStore,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(bottom = 12.dp),
                                tint = DeepRed
                            )
                            Text(
                                "No groceries yet!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = WarmBrown
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Add items manually or explore your saved recipes to fill your list.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = WarmBrown.copy(alpha = 0.8f),
                                modifier = Modifier.padding(horizontal = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.navigate(Routes.RECIPES_LIST) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DeepRed,
                                    contentColor = CreamyYellow
                                )
                            ) {
                                Text("Browse Saved Recipes")
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)  // Reduced spacing
                        ) {
                            items(state.items) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = CreamyYellow
                                    ),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.togglePurchasedStatus(item.id) }  // Make whole row clickable
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = item.name,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None,
                                                    fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.Medium
                                                ),
                                                color = if (item.isPurchased) WarmBrown.copy(alpha = 0.6f) else DeepRed  // Changed to DeepRed for better contrast
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                item.amounts.joinToString(", "),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                                                ),
                                                color = if (item.isPurchased) WarmBrown.copy(alpha = 0.5f) else WarmBrown
                                            )
                                        }

                                        // Visual indicator instead of checkbox
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (item.isPurchased) DeepRed.copy(alpha = 0.2f) else Color.Transparent
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (item.isPurchased) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Check,
                                                    contentDescription = "Purchased",
                                                    tint = DeepRed,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}