package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Product::class,
        Category::class,
        OrderRequest::class,
        CustomerMessage::class,
        Banner::class,
        StoreSettings::class,
        WishlistItem::class,
        ProductReview::class,
        AppNotification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun orderDao(): OrderDao
    abstract fun messageDao(): MessageDao
    abstract fun bannerDao(): BannerDao
    abstract fun settingsDao(): SettingsDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun reviewDao(): ReviewDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mystore_catalog_db"
                )
                .addCallback(DatabaseCallback(context.applicationContext))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Ensure initial settings exist
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val existingSettings = database.settingsDao().getSettingsSync()
                        if (existingSettings == null) {
                            database.settingsDao().saveSettings(InitialData.defaultSettings)
                        }
                    }
                }
            }

            private suspend fun populateInitialData(db: AppDatabase) {
                db.settingsDao().saveSettings(InitialData.defaultSettings)
                db.categoryDao().insertCategories(InitialData.categories)
                db.bannerDao().insertBanners(InitialData.banners)
                db.productDao().insertProducts(InitialData.sampleProducts)
                for (order in InitialData.sampleOrders) {
                    db.orderDao().insertOrder(order)
                }
                for (msg in InitialData.sampleMessages) {
                    db.messageDao().insertMessage(msg)
                }
                db.notificationDao().insertNotification(
                    AppNotification(
                        id = "NOTIF-1",
                        title = "Welcome to My Store!",
                        message = "Browse products, filter categories, or contact us directly via WhatsApp to order.",
                        type = "promo"
                    )
                )
            }
        }
    }
}
