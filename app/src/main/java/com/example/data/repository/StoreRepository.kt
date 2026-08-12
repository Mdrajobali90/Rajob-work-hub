package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class StoreRepository(private val db: AppDatabase) {

    val productDao = db.productDao()
    val categoryDao = db.categoryDao()
    val orderDao = db.orderDao()
    val messageDao = db.messageDao()
    val bannerDao = db.bannerDao()
    val settingsDao = db.settingsDao()
    val wishlistDao = db.wishlistDao()
    val reviewDao = db.reviewDao()
    val notificationDao = db.notificationDao()

    // Products
    val activeProducts: Flow<List<Product>> = productDao.getAllActiveProducts()
    val adminProducts: Flow<List<Product>> = productDao.getAllProductsAdmin()
    val featuredProducts: Flow<List<Product>> = productDao.getFeaturedProducts()
    val bestSellingProducts: Flow<List<Product>> = productDao.getBestSellingProducts()
    val newArrivalProducts: Flow<List<Product>> = productDao.getNewArrivalProducts()
    val specialOfferProducts: Flow<List<Product>> = productDao.getSpecialOfferProducts()

    fun getProductById(id: String): Flow<Product?> = productDao.getProductById(id)
    suspend fun getProductByIdSync(id: String): Product? = productDao.getProductByIdSync(id)
    fun getProductsByCategory(categoryId: String): Flow<List<Product>> = productDao.getProductsByCategory(categoryId)
    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)

    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
    suspend fun deleteProductById(id: String) = productDao.deleteProductById(id)

    // Categories
    val activeCategories: Flow<List<Category>> = categoryDao.getAllActiveCategories()
    val adminCategories: Flow<List<Category>> = categoryDao.getAllCategoriesAdmin()

    suspend fun insertCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    // Banners
    val activeBanners: Flow<List<Banner>> = bannerDao.getAllActiveBanners()
    val adminBanners: Flow<List<Banner>> = bannerDao.getAllBannersAdmin()

    suspend fun insertBanner(banner: Banner) = bannerDao.insertBanner(banner)
    suspend fun updateBanner(banner: Banner) = bannerDao.updateBanner(banner)
    suspend fun deleteBanner(banner: Banner) = bannerDao.deleteBanner(banner)

    // Orders
    val allOrders: Flow<List<OrderRequest>> = orderDao.getAllOrders()
    fun getOrdersByPhone(phone: String): Flow<List<OrderRequest>> = orderDao.getOrdersByPhone(phone)
    suspend fun submitOrder(order: OrderRequest) = orderDao.insertOrder(order)
    suspend fun updateOrderStatus(orderId: String, status: String) = orderDao.updateOrderStatus(orderId, status)
    suspend fun deleteOrder(order: OrderRequest) = orderDao.deleteOrder(order)

    // Messages
    val allMessages: Flow<List<CustomerMessage>> = messageDao.getAllMessages()
    suspend fun sendMessage(message: CustomerMessage) = messageDao.insertMessage(message)
    suspend fun markMessageRead(id: String) = messageDao.markAsRead(id)
    suspend fun deleteMessage(message: CustomerMessage) = messageDao.deleteMessage(message)

    // Settings
    val storeSettings: Flow<StoreSettings?> = settingsDao.getSettings()
    suspend fun getSettingsSync(): StoreSettings? = settingsDao.getSettingsSync()
    suspend fun saveSettings(settings: StoreSettings) = settingsDao.saveSettings(settings)

    // Wishlist
    val wishlistProductIds: Flow<List<String>> = wishlistDao.getWishlistProductIds()
    fun isWishlisted(productId: String): Flow<Boolean> = wishlistDao.isWishlisted(productId)
    suspend fun addToWishlist(productId: String) = wishlistDao.addToWishlist(WishlistItem(productId))
    suspend fun removeFromWishlist(productId: String) = wishlistDao.removeFromWishlist(productId)

    // Reviews
    fun getReviewsForProduct(productId: String): Flow<List<ProductReview>> = reviewDao.getApprovedReviewsForProduct(productId)
    suspend fun submitReview(review: ProductReview) = reviewDao.insertReview(review)

    // Notifications
    val notifications: Flow<List<AppNotification>> = notificationDao.getAllNotifications()
    val unreadNotifCount: Flow<Int> = notificationDao.getUnreadCount()
    suspend fun markNotifRead(id: String) = notificationDao.markAsRead(id)
    suspend fun addNotification(notif: AppNotification) = notificationDao.insertNotification(notif)

    // Admin Stats
    val productCount: Flow<Int> = productDao.getProductCount()
    val outOfStockCount: Flow<Int> = productDao.getOutOfStockCount()
    val categoryCount: Flow<Int> = categoryDao.getCategoryCount()
    val totalOrdersCount: Flow<Int> = orderDao.getTotalOrdersCount()
    val pendingOrdersCount: Flow<Int> = orderDao.getPendingOrdersCount()
    val completedOrdersCount: Flow<Int> = orderDao.getCompletedOrdersCount()
    val messageCount: Flow<Int> = messageDao.getMessageCount()
    val unreadMessageCount: Flow<Int> = messageDao.getUnreadMessageCount()
}
