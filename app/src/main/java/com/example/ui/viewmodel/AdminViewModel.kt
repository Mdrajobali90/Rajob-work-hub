package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.StoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StoreRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = StoreRepository(database)
    }

    // Admin Auth State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val defaultAdminPin = "1234" // Safe default PIN for store management demo

    fun login(pin: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (pin.trim() == defaultAdminPin || pin.trim() == "admin") {
            _isLoggedIn.value = true
            onSuccess()
        } else {
            onError("Invalid PIN/Password. Default admin PIN is 1234.")
        }
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    // Admin Statistics
    val totalProductsCount: StateFlow<Int> = repository.productCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val outOfStockCount: StateFlow<Int> = repository.outOfStockCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCategoriesCount: StateFlow<Int> = repository.categoryCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalOrdersCount: StateFlow<Int> = repository.totalOrdersCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingOrdersCount: StateFlow<Int> = repository.pendingOrdersCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedOrdersCount: StateFlow<Int> = repository.completedOrdersCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalMessagesCount: StateFlow<Int> = repository.messageCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val unreadMessagesCount: StateFlow<Int> = repository.unreadMessageCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // All Lists for Admin Management
    val allProducts: StateFlow<List<Product>> = repository.adminProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<Category>> = repository.adminCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderRequest>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMessages: StateFlow<List<CustomerMessage>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBanners: StateFlow<List<Banner>> = repository.adminBanners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val storeSettings: StateFlow<StoreSettings> = repository.storeSettings
        .map { it ?: com.example.data.local.InitialData.defaultSettings }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.data.local.InitialData.defaultSettings)

    // Product CRUD
    fun saveProduct(product: Product, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (product.name.isBlank() || product.price <= 0 || product.categoryId.isBlank()) {
            onError("Please enter valid Product Name, Price, and select a Category.")
            return
        }

        viewModelScope.launch {
            try {
                repository.insertProduct(product)
                onSuccess()
            } catch (e: Exception) {
                onError("Error saving product: ${e.localizedMessage}")
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun duplicateProduct(product: Product) {
        viewModelScope.launch {
            val newId = "PROD-" + System.currentTimeMillis().toString().takeLast(5)
            val duplicated = product.copy(
                id = newId,
                name = "${product.name} (Copy)"
            )
            repository.insertProduct(duplicated)
        }
    }

    fun toggleProductStatus(product: Product) {
        viewModelScope.launch {
            val updated = product.copy(isActive = !product.isActive)
            repository.updateProduct(updated)
        }
    }

    // Category CRUD
    fun saveCategory(category: Category, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (category.name.isBlank()) {
            onError("Category name cannot be empty.")
            return
        }

        viewModelScope.launch {
            try {
                repository.insertCategory(category)
                onSuccess()
            } catch (e: Exception) {
                onError("Error saving category: ${e.localizedMessage}")
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    // Banner CRUD
    fun saveBanner(banner: Banner, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (banner.title.isBlank() || banner.imageUrl.isBlank()) {
            onError("Banner title and Image URL are required.")
            return
        }

        viewModelScope.launch {
            try {
                repository.insertBanner(banner)
                onSuccess()
            } catch (e: Exception) {
                onError("Error saving banner: ${e.localizedMessage}")
            }
        }
    }

    fun deleteBanner(banner: Banner) {
        viewModelScope.launch {
            repository.deleteBanner(banner)
        }
    }

    // Order Status Update
    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun deleteOrder(order: OrderRequest) {
        viewModelScope.launch {
            repository.deleteOrder(order)
        }
    }

    // Message Management
    fun markMessageAsRead(id: String) {
        viewModelScope.launch {
            repository.markMessageRead(id)
        }
    }

    fun deleteMessage(message: CustomerMessage) {
        viewModelScope.launch {
            repository.deleteMessage(message)
        }
    }

    // Store Settings Management
    fun saveSettings(settings: StoreSettings, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.saveSettings(settings)
                onSuccess()
            } catch (e: Exception) {
                onError("Error updating settings: ${e.localizedMessage}")
            }
        }
    }
}
