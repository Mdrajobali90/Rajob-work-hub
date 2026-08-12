package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProductsAdmin(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: String): Flow<Product?>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductByIdSync(id: String): Product?

    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND isActive = 1")
    fun getProductsByCategory(categoryId: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isFeatured = 1 AND isActive = 1")
    fun getFeaturedProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isBestSelling = 1 AND isActive = 1")
    fun getBestSellingProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isNewArrival = 1 AND isActive = 1")
    fun getNewArrivalProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isOnSale = 1 AND isActive = 1")
    fun getSpecialOfferProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE (name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' OR fullDescription LIKE '%' || :query || '%') AND isActive = 1")
    fun searchProducts(query: String): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: String)

    @Query("SELECT COUNT(*) FROM products")
    fun getProductCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE stockQuantity = 0 OR inStock = 0")
    fun getOutOfStockCount(): Flow<Int>
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun getAllActiveCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    fun getAllCategoriesAdmin(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT COUNT(*) FROM categories")
    fun getCategoryCount(): Flow<Int>
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM order_requests ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderRequest>>

    @Query("SELECT * FROM order_requests WHERE id = :id")
    fun getOrderById(id: String): Flow<OrderRequest?>

    @Query("SELECT * FROM order_requests WHERE phone = :phone OR whatsAppNumber = :phone ORDER BY createdAt DESC")
    fun getOrdersByPhone(phone: String): Flow<List<OrderRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderRequest)

    @Query("UPDATE order_requests SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    @Delete
    suspend fun deleteOrder(order: OrderRequest)

    @Query("SELECT COUNT(*) FROM order_requests")
    fun getTotalOrdersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM order_requests WHERE status = 'New' OR status = 'Pending'")
    fun getPendingOrdersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM order_requests WHERE status = 'Completed'")
    fun getCompletedOrdersCount(): Flow<Int>
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM customer_messages ORDER BY createdAt DESC")
    fun getAllMessages(): Flow<List<CustomerMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: CustomerMessage)

    @Query("UPDATE customer_messages SET isRead = 1 WHERE id = :messageId")
    suspend fun markAsRead(messageId: String)

    @Delete
    suspend fun deleteMessage(message: CustomerMessage)

    @Query("SELECT COUNT(*) FROM customer_messages")
    fun getMessageCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM customer_messages WHERE isRead = 0")
    fun getUnreadMessageCount(): Flow<Int>
}

@Dao
interface BannerDao {
    @Query("SELECT * FROM banners WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun getAllActiveBanners(): Flow<List<Banner>>

    @Query("SELECT * FROM banners ORDER BY displayOrder ASC")
    fun getAllBannersAdmin(): Flow<List<Banner>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: Banner)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanners(banners: List<Banner>)

    @Update
    suspend fun updateBanner(banner: Banner)

    @Delete
    suspend fun deleteBanner(banner: Banner)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM store_settings WHERE id = 1")
    fun getSettings(): Flow<StoreSettings?>

    @Query("SELECT * FROM store_settings WHERE id = 1")
    suspend fun getSettingsSync(): StoreSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: StoreSettings)
}

@Dao
interface WishlistDao {
    @Query("SELECT productId FROM wishlist")
    fun getWishlistProductIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE productId = :productId)")
    fun isWishlisted(productId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistItem)

    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun removeFromWishlist(productId: String)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM product_reviews WHERE productId = :productId AND isApproved = 1 ORDER BY createdAt DESC")
    fun getApprovedReviewsForProduct(productId: String): Flow<List<ProductReview>>

    @Query("SELECT * FROM product_reviews ORDER BY createdAt DESC")
    fun getAllReviewsAdmin(): Flow<List<ProductReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ProductReview)

    @Query("UPDATE product_reviews SET isApproved = :approved WHERE id = :reviewId")
    suspend fun updateApproval(reviewId: String, approved: Boolean)

    @Delete
    suspend fun deleteReview(review: ProductReview)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>
}
