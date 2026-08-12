package com.example.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Product
import com.example.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductEditScreen(
    productId: String?,
    adminViewModel: AdminViewModel,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by adminViewModel.allCategories.collectAsStateWithLifecycle()
    val allProducts by adminViewModel.allProducts.collectAsStateWithLifecycle()

    val existingProduct = remember(productId, allProducts) {
        if (!productId.isNull_or_blank_local(productId) && productId != "new") {
            allProducts.find { it.id == productId }
        } else null
    }

    var id by remember(existingProduct) { mutableStateOf(existingProduct?.id ?: "PROD-${System.currentTimeMillis().toString().takeLast(5)}") }
    var name by remember(existingProduct) { mutableStateOf(existingProduct?.name ?: "") }
    var categoryId by remember(existingProduct) { mutableStateOf(existingProduct?.categoryId ?: (categories.firstOrNull()?.id ?: "cat_electronics")) }
    var brand by remember(existingProduct) { mutableStateOf(existingProduct?.brand ?: "") }
    var priceStr by remember(existingProduct) { mutableStateOf(existingProduct?.price?.toString() ?: "") }
    var originalPriceStr by remember(existingProduct) { mutableStateOf(existingProduct?.originalPrice?.toString() ?: "") }
    var discountPercentStr by remember(existingProduct) { mutableStateOf(existingProduct?.discountPercent?.toString() ?: "0") }
    var stockQuantityStr by remember(existingProduct) { mutableStateOf(existingProduct?.stockQuantity?.toString() ?: "10") }
    var imageUrl by remember(existingProduct) { mutableStateOf(existingProduct?.imageUrl ?: "https://picsum.photos/seed/product/600/600") }
    var additionalImages by remember(existingProduct) { mutableStateOf(existingProduct?.additionalImageList?.joinToString(",") ?: "") }
    var shortDescription by remember(existingProduct) { mutableStateOf(existingProduct?.shortDescription ?: "") }
    var fullDescription by remember(existingProduct) { mutableStateOf(existingProduct?.fullDescription ?: "") }
    var specifications by remember(existingProduct) { mutableStateOf(existingProduct?.specifications ?: "") }
    var colorsStr by remember(existingProduct) { mutableStateOf(existingProduct?.colorList?.joinToString(",") ?: "") }
    var sizesStr by remember(existingProduct) { mutableStateOf(existingProduct?.sizeList?.joinToString(",") ?: "") }

    var isFeatured by remember(existingProduct) { mutableStateOf(existingProduct?.isFeatured ?: false) }
    var isBestSelling by remember(existingProduct) { mutableStateOf(existingProduct?.isBestSelling ?: false) }
    var isNewArrival by remember(existingProduct) { mutableStateOf(existingProduct?.isNewArrival ?: false) }
    var isOnSale by remember(existingProduct) { mutableStateOf(existingProduct?.isOnSale ?: false) }
    var inStock by remember(existingProduct) { mutableStateOf(existingProduct?.inStock ?: true) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingProduct != null) "Edit Product" else "Add New Product", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.testTag("admin_product_edit_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("Product ID / SKU *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("product_id_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("product_name_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Dropdown
            Box {
                OutlinedButton(
                    onClick = { categoryExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Category: ${categories.find { it.id == categoryId }?.name ?: categoryId}")
                }

                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                categoryId = cat.id
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Brand / Manufacturer") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Price ($) *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("product_price_input")
                )

                OutlinedTextField(
                    value = originalPriceStr,
                    onValueChange = { originalPriceStr = it },
                    label = { Text("Original Price ($)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = discountPercentStr,
                    onValueChange = { discountPercentStr = it },
                    label = { Text("Discount %") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = stockQuantityStr,
                    onValueChange = { stockQuantityStr = it },
                    label = { Text("Stock Qty") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Main Image URL *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("product_image_url_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = additionalImages,
                onValueChange = { additionalImages = it },
                label = { Text("Additional Image URLs (comma-separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = shortDescription,
                onValueChange = { shortDescription = it },
                label = { Text("Short Description") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = fullDescription,
                onValueChange = { fullDescription = it },
                label = { Text("Full Detailed Description") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = specifications,
                onValueChange = { specifications = it },
                label = { Text("Specifications (key:value; key:value)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = colorsStr,
                onValueChange = { colorsStr = it },
                label = { Text("Colors (comma-separated e.g. Black,Blue,Silver)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = sizesStr,
                onValueChange = { sizesStr = it },
                label = { Text("Sizes (comma-separated e.g. S,M,L,XL)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Product Flags & Badges", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = inStock, onCheckedChange = { inStock = it })
                Text("In Stock")
                Spacer(modifier = Modifier.width(16.dp))
                Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
                Text("Featured")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isBestSelling, onCheckedChange = { isBestSelling = it })
                Text("Best Seller")
                Spacer(modifier = Modifier.width(16.dp))
                Checkbox(checked = isNewArrival, onCheckedChange = { isNewArrival = it })
                Text("New Arrival")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isOnSale, onCheckedChange = { isOnSale = it })
                Text("On Sale")
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = errorMessage ?: "", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    val origPrice = originalPriceStr.toDoubleOrNull() ?: price
                    val discount = discountPercentStr.toIntOrNull() ?: 0
                    val stockQty = stockQuantityStr.toIntOrNull() ?: 0

                    val productToSave = Product(
                        id = id.ifBlank { "PROD-${System.currentTimeMillis().toString().takeLast(5)}" },
                        name = name,
                        categoryId = categoryId,
                        brand = brand,
                        price = price,
                        originalPrice = origPrice,
                        discountPercent = discount,
                        stockQuantity = stockQty,
                        inStock = inStock && stockQty > 0,
                        imageUrl = imageUrl.ifBlank { "https://picsum.photos/seed/product/600/600" },
                        additionalImages = additionalImages,
                        shortDescription = shortDescription,
                        fullDescription = fullDescription,
                        specifications = specifications,
                        availableColors = colorsStr,
                        availableSizes = sizesStr,
                        isFeatured = isFeatured,
                        isBestSelling = isBestSelling,
                        isNewArrival = isNewArrival,
                        isOnSale = isOnSale,
                        rating = existingProduct?.rating ?: 4.5f,
                        reviewCount = existingProduct?.reviewCount ?: 10
                    )

                    adminViewModel.saveProduct(
                        product = productToSave,
                        onSuccess = onSaved,
                        onError = { err -> errorMessage = err }
                    )
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_product_btn")
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Product", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun String?.isNull_or_blank_local(str: String?): Boolean {
    return str == null || str.isBlank()
}
