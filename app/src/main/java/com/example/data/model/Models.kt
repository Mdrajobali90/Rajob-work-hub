package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String, // SKU/Product ID, e.g., "PROD-101"
    val name: String,
    val categoryId: String,
    val brand: String,
    val price: Double,
    val originalPrice: Double = 0.0,
    val discountPercent: Int = 0,
    val stockQuantity: Int = 10,
    val inStock: Boolean = true,
    val shortDescription: String,
    val fullDescription: String,
    val specifications: String = "", // Key: Value; Key: Value
    val imageUrl: String,
    val additionalImages: String = "", // Comma-separated URLs
    val videoUrl: String = "",
    val availableColors: String = "", // Comma-separated
    val availableSizes: String = "", // Comma-separated
    val tags: String = "", // Comma-separated
    val rating: Float = 4.8f,
    val reviewCount: Int = 24,
    val isFeatured: Boolean = false,
    val isBestSelling: Boolean = false,
    val isNewArrival: Boolean = false,
    val isOnSale: Boolean = false,
    val isActive: Boolean = true
) {
    val additionalImageList: List<String>
        get() = if (additionalImages.isBlank()) emptyList() else additionalImages.split(",").map { it.trim() }

    val colorList: List<String>
        get() = if (availableColors.isBlank()) emptyList() else availableColors.split(",").map { it.trim() }

    val sizeList: List<String>
        get() = if (availableSizes.isBlank()) emptyList() else availableSizes.split(",").map { it.trim() }

    val tagList: List<String>
        get() = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }
}

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String, // e.g. "devices", "checkroom", "home", "spa", "sports", "book", "toys", "directions_car", "watch", "grid"
    val imageUrl: String = "",
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "order_requests")
data class OrderRequest(
    @PrimaryKey val id: String,
    val customerName: String,
    val phone: String,
    val whatsAppNumber: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val totalPrice: Double,
    val address: String,
    val customerMessage: String = "",
    val status: String = "New", // New, Pending, Confirmed, Processing, Completed, Cancelled
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "customer_messages")
data class CustomerMessage(
    @PrimaryKey val id: String,
    val customerName: String,
    val phone: String,
    val email: String = "",
    val message: String,
    val productId: String = "",
    val productName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "banners")
data class Banner(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val ctaText: String,
    val ctaDestination: String = "",
    val imageUrl: String,
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "store_settings")
data class StoreSettings(
    @PrimaryKey val id: Int = 1,
    val storeName: String = "My Store",
    val storeSubtitle: String = "Shop • Discover • Contact",
    val storeLogoUrl: String = "",
    val storeDescription: String = "Your trusted marketplace for quality products at affordable prices.",
    val whatsAppNumber: String = "+1234567890",
    val phoneNumber: String = "+1234567890",
    val email: String = "support@mystore.com",
    val address: String = "123 Commercial Plaza, Suite 400, Retail City",
    val facebookUrl: String = "https://facebook.com/mystore",
    val instagramUrl: String = "https://instagram.com/mystore",
    val messengerUrl: String = "https://m.me/mystore",
    val businessHours: String = "Mon - Sat: 9:00 AM - 8:00 PM",
    val currency: String = "$",
    val deliveryInfo: String = "Fast nationwide delivery within 2-3 business days. Cash on delivery available.",
    val returnPolicy: String = "7 days easy return & money back guarantee for damaged items.",
    val privacyPolicy: String = "We protect your customer data and personal contact information.",
    val termsConditions: String = "All order requests are verified via WhatsApp/Phone call prior to dispatch."
)

@Entity(tableName = "wishlist")
data class WishlistItem(
    @PrimaryKey val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "product_reviews")
data class ProductReview(
    @PrimaryKey val id: String,
    val productId: String,
    val customerName: String,
    val rating: Float,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isApproved: Boolean = true
)

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "promo" // order, message, stock, promo
)
