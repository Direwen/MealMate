package com.minsikhein_bj01lr.mealmate.ui.screen.groceries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.component.LoadingScreen
import com.minsikhein_bj01lr.mealmate.ui.navigation.Routes
import com.minsikhein_bj01lr.mealmate.ui.theme.SoftCreamyYellow
import com.minsikhein_bj01lr.mealmate.viewmodel.AuthViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.groceries.GroceryListViewModel
import com.minsikhein_bj01lr.mealmate.viewmodel.recipes.RecipesCreateViewModel

@Composable
fun GroceriesListScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    val context = LocalContext.current
    val viewModel: GroceryListViewModel = viewModel {
        GroceryListViewModel { context }
    }
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
                .background(MaterialTheme.colorScheme.background)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Grocery List",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(
                    onClick = { viewModel.shareGroceryList() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share grocery list",
                        tint = MaterialTheme.colorScheme.surface
                    )
                }
            }

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
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Your Grocery Progress",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.surface
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                LinearProgressIndicator(
                                    progress = percent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Total items",
                                            tint = MaterialTheme.colorScheme.surface,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "Total: ${state.totalItems}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.surface
                                        )
                                    }

                                    Row {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Purchased items",
                                            tint = MaterialTheme.colorScheme.surface,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "Purchased: ${state.purchasedCount}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.surface
                                        )
                                    }

                                    Text(
                                        "${(percent * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.surface
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
                            Text(error, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.clearError()
                                    viewModel.refresh(currentUserId)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
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
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "No groceries yet!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Add items manually or explore your saved recipes to fill your list.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.padding(horizontal = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.navigate(Routes.RECIPES_LIST) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.surface
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

                            items(state.items, key = { it.id }) { item ->
                                var expanded by remember { mutableStateOf(false) }
                                val isPurchased = item.isPurchased

                                // Card background color
                                val backgroundColor = when {
                                    isPurchased && expanded -> MaterialTheme.colorScheme.background
                                    isPurchased && !expanded -> MaterialTheme.colorScheme.background
                                    !isPurchased && expanded -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.background
                                }

                                // Primary text color (ingredient name)
                                val primaryTextColor = when {
                                    isPurchased && expanded -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    isPurchased && !expanded -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    !isPurchased && expanded -> MaterialTheme.colorScheme.background
                                    else -> MaterialTheme.colorScheme.primary
                                }

                                // Secondary text color (detail text)
                                val secondaryTextColor = when {
                                    isPurchased -> primaryTextColor.copy(alpha = 0.7f)
                                    expanded -> MaterialTheme.colorScheme.background
                                    else -> MaterialTheme.colorScheme.primary
                                }

                                // --- Swipe to Dismiss Wrapper ---
                                val swipeState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { value ->
                                        if (value == SwipeToDismissBoxValue.EndToStart) {
                                            viewModel.deleteGroceryItem(item.id)
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                )

                                SwipeToDismissBox(
                                    state = swipeState,
                                    backgroundContent = {
                                        val dismissColor = Color(0xFFD32F2F) // Material Red 700 for delete
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(dismissColor)
                                                .padding(bottom = 10.dp),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(end = 24.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Delete,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.surface,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Delete",
                                                    color = SoftCreamyYellow,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp)) // Match the inner card
                                ) {
                                    // This is your original item content (the card)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(backgroundColor)
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clickable { viewModel.togglePurchasedStatus(item.id) }
                                                ) {
                                                    Text(
                                                        text = item.name.uppercase(),
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            textDecoration = if (isPurchased) TextDecoration.LineThrough else TextDecoration.None
                                                        ),
                                                        color = primaryTextColor
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                }
                                                // Collapse Button
                                                IconButton(
                                                    onClick = { expanded = !expanded },
                                                    modifier = Modifier
                                                        .clip(CircleShape)
                                                        .padding(6.dp)
                                                        .size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                        contentDescription = if (expanded) "Collapse" else "Expand",
                                                        tint = primaryTextColor
                                                    )
                                                }
                                            }
                                            // Expanded details
                                            AnimatedVisibility(visible = expanded) {
                                                Column(modifier = Modifier.padding(top = 8.dp, start = 4.dp)) {
                                                    if (item.recipeSources.isNotEmpty()) {
                                                        Text(
                                                            text = "From recipes:",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                fontSize = MaterialTheme.typography.labelSmall.fontSize * 1.2f
                                                            ),
                                                            color = secondaryTextColor
                                                        )
                                                        item.recipeSources.forEach { source ->
                                                            Text(
                                                                "- ${source.amount} for ${source.recipeName}",
                                                                style = MaterialTheme.typography.bodySmall.copy(
                                                                    fontSize = MaterialTheme.typography.bodySmall.fontSize * 1.2f
                                                                ),
                                                                color = secondaryTextColor
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Divider(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                thickness = 1.dp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
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