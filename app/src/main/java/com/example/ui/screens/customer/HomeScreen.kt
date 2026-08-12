package com.example.ui.screens.customer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Category
import com.example.data.model.Product
import com.example.ui.components.*
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.StoreViewModel
import java.net.URLEncoder

@Composable
fun HomeScreen(
    viewModel: StoreViewModel,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onViewAllProducts: () -> Unit,
    onContactToBuy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.storeSettings.collectAsStateWithLifecycle()
    val banners by viewModel.activeBanners.collectAsStateWithLifecycle()
    val categories by viewModel.activeCategories.collectAsStateWithLifecycle()
    val featuredProducts by viewModel.featuredProducts.collectAsStateWithLifecycle()
    val bestSellingProducts by viewModel.bestSellingProducts.collectAsStateWithLifecycle()
    val newArrivalProducts by viewModel.newArrivalProducts.collectAsStateWithLifecycle()
    val specialOffers by viewModel.specialOfferProducts.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()

    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        // 1. Promotional Banner Slider
        item {
            if (banners.isNotEmpty()) {
                BannerSlider(
                    banners = banners,
                    onBannerClick = { banner ->
                        if (banner.ctaDestination.startsWith("category:")) {
                            val catId = banner.ctaDestination.removePrefix("category:")
                            onCategoryClick(catId)
                        } else if (banner.ctaDestination.startsWith("product:")) {
                            val prodId = banner.ctaDestination.removePrefix("product:")
                            onProductClick(prodId)
                        } else {
                            onViewAllProducts()
                        }
                    }
                )
            }
        }

        // 2. Categories Section
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                SectionHeader(
                    title = "Categories",
                    onViewAll = { onCategoryClick("all") }
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    items(categories) { category ->
                        CategoryCard(
                            category = category,
                            onClick = { onCategoryClick(category.id) }
                        )
                    }
                }
            }
        }

        // 3. Featured Products
        if (featuredProducts.isNotEmpty()) {
            item {
                ProductSectionHorizontal(
                    title = "Featured Products",
                    products = featuredProducts,
                    currencySymbol = settings.currency,
                    wishlistIds = wishlistIds,
                    onWishlistToggle = { viewModel.toggleWishlist(it) },
                    onProductClick = onProductClick,
                    onViewAll = onViewAllProducts
                )
            }
        }

        // 4. Best Selling Products
        if (bestSellingProducts.isNotEmpty()) {
            item {
                ProductSectionHorizontal(
                    title = "Best Selling",
                    products = bestSellingProducts,
                    currencySymbol = settings.currency,
                    wishlistIds = wishlistIds,
                    onWishlistToggle = { viewModel.toggleWishlist(it) },
                    onProductClick = onProductClick,
                    onViewAll = onViewAllProducts
                )
            }
        }

        // 5. Special Offers / Discounts
        if (specialOffers.isNotEmpty()) {
            item {
                ProductSectionHorizontal(
                    title = "Special Offers",
                    products = specialOffers,
                    currencySymbol = settings.currency,
                    wishlistIds = wishlistIds,
                    onWishlistToggle = { viewModel.toggleWishlist(it) },
                    onProductClick = onProductClick,
                    onViewAll = onViewAllProducts
                )
            }
        }

        // 6. New Arrivals
        if (newArrivalProducts.isNotEmpty()) {
            item {
                ProductSectionHorizontal(
                    title = "New Arrivals",
                    products = newArrivalProducts,
                    currencySymbol = settings.currency,
                    wishlistIds = wishlistIds,
                    onWishlistToggle = { viewModel.toggleWishlist(it) },
                    onProductClick = onProductClick,
                    onViewAll = onViewAllProducts
                )
            }
        }

        // 7. About Store Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "About ${settings.storeName}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = settings.storeDescription,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = settings.address,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 8. Quick Contact Bar (WhatsApp, Call, Contact Form)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Need Help or Custom Orders?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Contact us directly via WhatsApp or phone call",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // WhatsApp
                        Button(
                            onClick = {
                                val cleanNum = settings.whatsAppNumber.replace(Regex("[^0-9+]"), "")
                                val text = URLEncoder.encode("Hello! I am browsing your store ${settings.storeName} and would like to inquire about ordering.", "UTF-8")
                                val uri = Uri.parse("https://wa.me/$cleanNum?text=$text")
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "WhatsApp",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Call
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${settings.phoneNumber}"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Call,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Call Now",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        TextTextButton(onClick = onViewAll)
    }
}

@Composable
fun TextTextButton(onClick: () -> Unit) {
    Text(
        text = "View All",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun ProductSectionHorizontal(
    title: String,
    products: List<Product>,
    currencySymbol: String,
    wishlistIds: List<String>,
    onWishlistToggle: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onViewAll: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        SectionHeader(title = title, onViewAll = onViewAll)

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    currencySymbol = currencySymbol,
                    isWishlisted = wishlistIds.contains(product.id),
                    onWishlistToggle = { onWishlistToggle(product.id) },
                    onClick = { onProductClick(product.id) },
                    modifier = Modifier.width(160.dp)
                )
            }
        }
    }
}
