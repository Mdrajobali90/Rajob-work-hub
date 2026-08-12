package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.StoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StoreRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = StoreRepository(database)
    }

    // Dynamic store settings
    val storeSettings: StateFlow<StoreSettings> = repository.storeSettings
        .map { it ?: com.example.data.local.InitialData.defaultSettings }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.data.local.InitialData.defaultSettings)

    // Active Catalog Data
    val activeProducts: StateFlow<List<Product>> = repository.activeProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCategories: StateFlow<List<Category>> = repository.activeCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBanners: StateFlow<List<Banner>> = repository.activeBanners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredProducts: StateFlow<List<Product>> = repository.featuredProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bestSellingProducts: StateFlow<List<Product>> = repository.bestSellingProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newArrivalProducts: StateFlow<List<Product>> = repository.newArrivalProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val specialOfferProducts: StateFlow<List<Product>> = repository.specialOfferProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wishlist
    val wishlistIds: StateFlow<List<String>> = repository.wishlistProductIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistCount: StateFlow<Int> = wishlistIds
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val wishlistProducts: StateFlow<List<Product>> = combine(activeProducts, wishlistIds) { products, ids ->
        products.filter { ids.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val notifications: StateFlow<List<AppNotification>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = repository.unreadNotifCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Search & Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _selectedPriceMax = MutableStateFlow<Double?>(null)
    val selectedPriceMax: StateFlow<Double?> = _selectedPriceMax.asStateFlow()

    private val _sortBy = MutableStateFlow("newest") // newest, price_low, price_high, popularity
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    private val _onlyInStock = MutableStateFlow(false)
    val onlyInStock: StateFlow<Boolean> = _onlyInStock.asStateFlow()

    private val _onlyOnSale = MutableStateFlow(false)
    val onlyOnSale: StateFlow<Boolean> = _onlyOnSale.asStateFlow()

    private data class FilterState(
        val query: String,
        val categoryId: String?,
        val maxPrice: Double?,
        val sort: String,
        val inStock: Boolean,
        val onSale: Boolean
    )

    private val filterGroup1 = combine(searchQuery, selectedCategoryId, selectedPriceMax) { query, cat, price ->
        Triple(query, cat, price)
    }

    private val filterGroup2 = combine(sortBy, onlyInStock, onlyOnSale) { sort, inStock, onSale ->
        Triple(sort, inStock, onSale)
    }

    private val filterStateFlow: Flow<FilterState> = combine(filterGroup1, filterGroup2) { f1, f2 ->
        FilterState(
            query = f1.first,
            categoryId = f1.second,
            maxPrice = f1.third,
            sort = f2.first,
            inStock = f2.second,
            onSale = f2.third
        )
    }

    // Filtered Products Result Flow
    val filteredProducts: StateFlow<List<Product>> = combine(
        activeProducts,
        filterStateFlow
    ) { products, filter ->
        var list = products

        if (filter.query.isNotBlank()) {
            val q = filter.query.lowercase().trim()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.id.lowercase().contains(q) ||
                it.tags.lowercase().contains(q) ||
                it.fullDescription.lowercase().contains(q)
            }
        }

        if (!filter.categoryId.isNull_or_blank_safe(filter.categoryId)) {
            list = list.filter { it.categoryId == filter.categoryId }
        }

        if (filter.maxPrice != null && filter.maxPrice > 0) {
            list = list.filter { it.price <= filter.maxPrice }
        }

        if (filter.inStock) {
            list = list.filter { it.inStock && it.stockQuantity > 0 }
        }

        if (filter.onSale) {
            list = list.filter { it.isOnSale || it.discountPercent > 0 }
        }

        when (filter.sort) {
            "price_low" -> list.sortedBy { it.price }
            "price_high" -> list.sortedByDescending { it.price }
            "popularity" -> list.sortedByDescending { it.reviewCount }
            else -> list.sortedByDescending { it.id } // Newest default
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Helper function safe String check
    private fun String?.isNull_or_blank_safe(str: String?): Boolean {
        return str == null || str.isBlank() || str == "all"
    }

    // Search Actions
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(catId: String?) {
        _selectedCategoryId.value = catId
    }

    fun setSortBy(sort: String) {
        _sortBy.value = sort
    }

    fun setPriceMax(max: Double?) {
        _selectedPriceMax.value = max
    }

    fun setOnlyInStock(value: Boolean) {
        _onlyInStock.value = value
    }

    fun setOnlyOnSale(value: Boolean) {
        _onlyOnSale.value = value
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _selectedCategoryId.value = null
        _selectedPriceMax.value = null
        _sortBy.value = "newest"
        _onlyInStock.value = false
        _onlyOnSale.value = false
    }

    // Wishlist Toggle
    fun toggleWishlist(productId: String) {
        viewModelScope.launch {
            if (wishlistIds.value.contains(productId)) {
                repository.removeFromWishlist(productId)
            } else {
                repository.addToWishlist(productId)
            }
        }
    }

    // Get Single Product Flow
    fun getProductFlow(productId: String): Flow<Product?> {
        return repository.getProductById(productId)
    }

    // Get Category Products
    fun getProductsByCategory(categoryId: String): Flow<List<Product>> {
        return repository.getProductsByCategory(categoryId)
    }

    // Submit Order Request
    fun submitOrderRequest(
        customerName: String,
        phone: String,
        whatsAppNumber: String,
        productId: String,
        productName: String,
        quantity: Int,
        totalPrice: Double,
        address: String,
        message: String,
        onSuccess: (OrderRequest) -> Unit,
        onError: (String) -> Unit
    ) {
        if (customerName.isBlank() || phone.isBlank() || address.isBlank()) {
            onError("Please fill in your Name, Phone Number, and Delivery Address.")
            return
        }

        viewModelScope.launch {
            try {
                val newOrderId = "ORD-" + System.currentTimeMillis().toString().takeLast(6)
                val order = OrderRequest(
                    id = newOrderId,
                    customerName = customerName.trim(),
                    phone = phone.trim(),
                    whatsAppNumber = if (whatsAppNumber.isNotBlank()) whatsAppNumber.trim() else phone.trim(),
                    productId = productId,
                    productName = productName,
                    quantity = quantity,
                    totalPrice = totalPrice,
                    address = address.trim(),
                    customerMessage = message.trim(),
                    status = "New",
                    createdAt = System.currentTimeMillis()
                )
                repository.submitOrder(order)

                // Add notification for admin/system
                repository.addNotification(
                    AppNotification(
                        id = "NOTIF-" + System.currentTimeMillis(),
                        title = "Order Request Submitted!",
                        message = "Your order request for $productName ($newOrderId) has been received. We will contact you shortly.",
                        type = "order"
                    )
                )

                onSuccess(order)
            } catch (e: Exception) {
                onError("Failed to submit order request: ${e.localizedMessage}")
            }
        }
    }

    // Submit Customer Message
    fun sendCustomerMessage(
        customerName: String,
        phone: String,
        email: String,
        message: String,
        productId: String = "",
        productName: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (customerName.isBlank() || phone.isBlank() || message.isBlank()) {
            onError("Please provide your name, phone number, and message.")
            return
        }

        viewModelScope.launch {
            try {
                val msg = CustomerMessage(
                    id = "MSG-" + System.currentTimeMillis().toString().takeLast(6),
                    customerName = customerName.trim(),
                    phone = phone.trim(),
                    email = email.trim(),
                    message = message.trim(),
                    productId = productId,
                    productName = productName,
                    createdAt = System.currentTimeMillis(),
                    isRead = false
                )
                repository.sendMessage(msg)
                onSuccess()
            } catch (e: Exception) {
                onError("Failed to send message: ${e.localizedMessage}")
            }
        }
    }

    // Order tracking by phone number
    fun getOrdersByPhone(phone: String): Flow<List<OrderRequest>> {
        return repository.getOrdersByPhone(phone.trim())
    }

    // Mark Notification Read
    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotifRead(id)
        }
    }
}
