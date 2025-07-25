package com.minsikhein_bj01lr.mealmate.ui.screen.groceries

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.minsikhein_bj01lr.mealmate.ui.component.AuthenticatedScreen
import com.minsikhein_bj01lr.mealmate.ui.component.LoadingScreen
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

    // Load groceries on initial display
    LaunchedEffect(currentUserId) {
        viewModel.loadGroceries(currentUserId)
    }

    AuthenticatedScreen(
        authViewModel = authViewModel,
        navController = navController
    ) { innerPadding ->

        LoadingScreen(isLoading = state.isLoading && !state.isRefreshing) {
            Column(modifier = Modifier.padding(innerPadding)) {

                // Stats header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total: ${state.totalItems}")
                    Text("Purchased: ${state.purchasedCount}")
                }

                // Error handling
                state.error?.let { error ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(error, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.clearError()
                            viewModel.refresh(currentUserId)
                        }) {
                            Text("Retry")
                        }
                    }
                }

                // Grocery List
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.items) { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        item.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        item.amounts.joinToString(", "),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Checkbox(
                                    checked = item.isPurchased,
                                    onCheckedChange = { viewModel.togglePurchasedStatus(item.id) }
                                )
                            }
                            Divider(modifier = Modifier.padding(top = 12.dp))
                        }
                    }
                }
            }
        }
    }
}
