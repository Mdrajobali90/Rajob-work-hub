package com.example.ui.screens.customer

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Product
import com.example.ui.components.*
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.StoreViewModel
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: StoreViewModel,
    onBackClick: () -> Unit,
    onContactToBuy: (String, Int) -> Unit,
    onRelatedProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.storeSettings.collectAsStateWithLifecycle()
    val productState by viewModel.getProductFlow(productId).collectAsStateWithLifecycle(initialValue = null)
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()
    val allProducts by viewModel.activeProducts.collectAsStateWithLifecycle()

    val product = productState ?: return

    val isWishlisted = wishlistIds.contains(product.id)

    // Gallery images state
    val galleryImages = remember(product) {
        listOf(product.imageUrl) + product.additionalImageList
    }
    var selectedImageIndex by remember(product) { mutableIntStateOf(0) }

    // Color & Size Selection state
    var selectedColor by remember(product) {
        mutableStateOf(product.colorList.firstOrNull() ?: "")
    }
    var selectedSize by remember(product) {
        mutableStateOf(product.sizeList.firstOrNull() ?: "")
    }

    // Quantity State
    var quantity by remember(product) { mutableIntStateOf(1) }

    val relatedProducts = remember(allProducts, product) {
        allProducts.filter { it.categoryId == product.categoryId && it.id != product.id }.take(6)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleWishlist(product.id) }) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, product.name)
                            putExtra(Intent.EXTRA_TEXT, "Check out ${product.name} on ${settings.storeName} for ${settings.currency}${product.price}!")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Product"))
                    }) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            // Main Bottom Action Bar matching UI mockup
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Main CTA: Contact to Buy
                    Button(
                        onClick = { onContactToBuy(product.id, quantity) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("contact_to_buy_btn")
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Contact to Buy", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Secondary CTAs: WhatsApp & Call Now
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // WhatsApp Button
                        Button(
                            onClick = {
                                val cleanNum = settings.whatsAppNumber.replace(Regex("[^0-9+]"), "")
                                val msgText = """
                                    Hello, I am interested in this product:
                                    
                                    Product: ${product.name}
                                    Price: ${settings.currency}${String.format("%.2f", product.price)}
                                    Quantity: $quantity
                                    Product ID: ${product.id}
                                    ${if (selectedColor.isNotBlank()) "Color: $selectedColor" else ""}
                                    ${if (selectedSize.isNotBlank()) "Size: $selectedSize" else ""}
                                    
                                    Please provide availability and ordering information.
                                """.trimIndent()

                                val encoded = URLEncoder.encode(msgText, "UTF-8")
                                val uri = Uri.parse("https://wa.me/$cleanNum?text=$encoded")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "WhatsApp is not installed. Opening browser...", Toast.LENGTH_SHORT).show()
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("whatsapp_order_btn")
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WhatsApp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Call Now Button
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${settings.phoneNumber}"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("call_now_btn")
                        ) {
                            Icon(Icons.Outlined.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Now", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        },
        modifier = modifier.testTag("product_detail_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Main Product Image Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(galleryImages.getOrElse(selectedImageIndex) { product.imageUrl })
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )

                // Top badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    if (product.discountPercent > 0) {
                        DiscountBadge(percent = product.discountPercent)
                    }
                    if (product.isNewArrival) {
                        FeatureBadge(text = "NEW")
                    }
                    if (product.isFeatured) {
                        FeatureBadge(text = "FEATURED", color = Color(0xFF7B1FA2))
                    }
                }
            }

            // Thumbnail Gallery Picker
            if (galleryImages.size > 1) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(galleryImages.size) { index ->
                        val isSelected = index == selectedImageIndex
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedImageIndex = index }
                                .padding(4.dp)
                        ) {
                            AsyncImage(
                                model = galleryImages[index],
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Product Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = product.brand,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price and Stock row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${settings.currency}${String.format("%.2f", product.price)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (product.originalPrice > product.price) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${settings.currency}${String.format("%.2f", product.originalPrice)}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    // Stock Status
                    if (product.inStock && product.stockQuantity > 0) {
                        StatusBadge(text = "In Stock (${product.stockQuantity})", isSuccess = true)
                    } else {
                        StatusBadge(text = "Out of Stock", isSuccess = false)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rating & Review row
                RatingBar(rating = product.rating, reviewCount = product.reviewCount, starSize = 18.dp)

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Color selector if available
                if (product.colorList.isNotEmpty()) {
                    Text(
                        text = "Available Colors",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.colorList.forEach { colorName ->
                            FilterChip(
                                selected = selectedColor == colorName,
                                onClick = { selectedColor = colorName },
                                label = { Text(colorName, fontSize = 12.sp) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Size selector if available
                if (product.sizeList.isNotEmpty()) {
                    Text(
                        text = "Available Sizes",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.sizeList.forEach { sizeName ->
                            FilterChip(
                                selected = selectedSize == sizeName,
                                onClick = { selectedSize = sizeName },
                                label = { Text(sizeName, fontSize = 12.sp) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Quantity Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Quantity",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    QuantitySelector(
                        quantity = quantity,
                        maxStock = product.stockQuantity.coerceAtLeast(1),
                        onQuantityChange = { quantity = it }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Description
                Text(
                    text = "Description",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.fullDescription.ifBlank { product.shortDescription },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                // Specifications
                if (product.specifications.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Specifications",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            product.specifications.split(";").forEach { specPair ->
                                if (specPair.isNotBlank()) {
                                    Text(
                                        text = "• ${specPair.trim()}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SKU & Category info card
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                        .padding(10.dp)
                ) {
                    Text("Product SKU: ${product.id}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Category: ${product.categoryId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Related Products Section
                if (relatedProducts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Related Products",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(relatedProducts) { relProd ->
                            ProductCard(
                                product = relProd,
                                currencySymbol = settings.currency,
                                isWishlisted = wishlistIds.contains(relProd.id),
                                onWishlistToggle = { viewModel.toggleWishlist(relProd.id) },
                                onClick = { onRelatedProductClick(relProd.id) },
                                modifier = Modifier.width(150.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
