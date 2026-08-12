package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ProductCard
import com.example.ui.components.StoreSearchBar
import com.example.ui.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: StoreViewModel,
    initialCategoryId: String? = null,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.storeSettings.collectAsStateWithLifecycle()
    val categories by viewModel.activeCategories.collectAsStateWithLifecycle()
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()
    val onlyInStock by viewModel.onlyInStock.collectAsStateWithLifecycle()
    val onlyOnSale by viewModel.onlyOnSale.collectAsStateWithLifecycle()

    var showFilterBottomSheet by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(initialCategoryId) {
        if (!initialCategoryId.isNull_or_blank_local(initialCategoryId) && initialCategoryId != "all") {
            viewModel.setSelectedCategory(initialCategoryId)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("product_list_screen")
    ) {
        // Search bar
        StoreSearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.setSearchQuery(it) },
            onFilterClick = { showFilterBottomSheet = true }
        )

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null || selectedCategory == "all",
                    onClick = { viewModel.setSelectedCategory(null) },
                    label = { Text("All", fontSize = 12.sp) }
                )
            }
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category.id,
                    onClick = {
                        if (selectedCategory == category.id) {
                            viewModel.setSelectedCategory(null)
                        } else {
                            viewModel.setSelectedCategory(category.id)
                        }
                    },
                    label = { Text(category.name, fontSize = 12.sp) }
                )
            }
        }

        // Sort & Filter Status Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${products.size} Products Found",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sort Dropdown Button
                Box {
                    AssistChip(
                        onClick = { showSortMenu = true },
                        label = {
                            Text(
                                text = when (sortBy) {
                                    "price_low" -> "Price: Low -> High"
                                    "price_high" -> "Price: High -> Low"
                                    "popularity" -> "Popularity"
                                    else -> "Newest"
                                },
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Newest") },
                            onClick = { viewModel.setSortBy("newest"); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Price: Low to High") },
                            onClick = { viewModel.setSortBy("price_low"); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Price: High to Low") },
                            onClick = { viewModel.setSortBy("price_high"); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Popularity") },
                            onClick = { viewModel.setSortBy("popularity"); showSortMenu = false }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Reset Filters Button
                if (selectedCategory != null || searchQuery.isNotBlank() || onlyInStock || onlyOnSale) {
                    IconButton(
                        onClick = { viewModel.resetFilters() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Product Grid Layout
        if (products.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Products Match Your Criteria",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try clearing your filters or search terms.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.resetFilters() }) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        currencySymbol = settings.currency,
                        isWishlisted = wishlistIds.contains(product.id),
                        onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterBottomSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Filter Products",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // In Stock Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Only In-Stock Products", fontSize = 14.sp)
                    Switch(
                        checked = onlyInStock,
                        onCheckedChange = { viewModel.setOnlyInStock(it) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // On Sale Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Discounted & On Sale", fontSize = 14.sp)
                    Switch(
                        checked = onlyOnSale,
                        onCheckedChange = { viewModel.setOnlyOnSale(it) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.resetFilters()
                            showFilterBottomSheet = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset")
                    }

                    Button(
                        onClick = { showFilterBottomSheet = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply Filters")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private fun String?.isNull_or_blank_local(str: String?): Boolean {
    return str == null || str.isBlank()
}
