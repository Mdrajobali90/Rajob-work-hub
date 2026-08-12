package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.StoreBottomNavBar
import com.example.ui.components.StoreTopAppBar
import com.example.ui.screens.admin.*
import com.example.ui.screens.customer.*
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun AppNavigation(
    storeViewModel: StoreViewModel,
    adminViewModel: AdminViewModel
) {
    val navController = rememberNavController()

    val settings by storeViewModel.storeSettings.collectAsStateWithLifecycle()
    val wishlistCount by storeViewModel.wishlistCount.collectAsStateWithLifecycle()
    val unreadNotifCount by storeViewModel.unreadNotifCount.collectAsStateWithLifecycle()
    val isAdminLoggedIn by adminViewModel.isLoggedIn.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    // Top & Bottom bar visibility rules
    val showCustomerBars = currentRoute in listOf(
        "home",
        "product_list",
        "product_list?category={category}",
        "categories",
        "search",
        "wishlist",
        "notifications",
        "order_tracker",
        "account"
    )

    Scaffold(
        topBar = {
            if (showCustomerBars) {
                StoreTopAppBar(
                    settings = settings,
                    unreadNotifCount = unreadNotifCount,
                    wishlistCount = wishlistCount,
                    onSearchClick = { navController.navigate("search") },
                    onNotificationClick = { navController.navigate("notifications") },
                    onWishlistClick = { navController.navigate("wishlist") },
                    onAdminClick = {
                        if (isAdminLoggedIn) {
                            navController.navigate("admin_dashboard")
                        } else {
                            navController.navigate("admin_login")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showCustomerBars) {
                StoreBottomNavBar(
                    currentRoute = currentRoute.split("?").first(),
                    onNavigate = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Customer Routes
            composable("home") {
                HomeScreen(
                    viewModel = storeViewModel,
                    onProductClick = { prodId -> navController.navigate("product_detail/$prodId") },
                    onCategoryClick = { catId -> navController.navigate("product_list?category=$catId") },
                    onViewAllProducts = { navController.navigate("product_list") },
                    onContactToBuy = { prodId -> navController.navigate("contact_order/$prodId/1") }
                )
            }

            composable(
                route = "product_list?category={category}",
                arguments = listOf(navArgument("category") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val categoryArg = backStackEntry.arguments?.getString("category")
                ProductListScreen(
                    viewModel = storeViewModel,
                    initialCategoryId = categoryArg,
                    onProductClick = { prodId -> navController.navigate("product_detail/$prodId") }
                )
            }

            composable("product_list") {
                ProductListScreen(
                    viewModel = storeViewModel,
                    onProductClick = { prodId -> navController.navigate("product_detail/$prodId") }
                )
            }

            composable(
                route = "product_detail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val prodId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(
                    productId = prodId,
                    viewModel = storeViewModel,
                    onBackClick = { navController.popBackStack() },
                    onContactToBuy = { id, qty -> navController.navigate("contact_order/$id/$qty") },
                    onRelatedProductClick = { id -> navController.navigate("product_detail/$id") }
                )
            }

            composable(
                route = "contact_order/{productId}/{quantity}",
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType },
                    navArgument("quantity") { type = NavType.IntType; defaultValue = 1 }
                )
            ) { backStackEntry ->
                val prodId = backStackEntry.arguments?.getString("productId") ?: ""
                val qty = backStackEntry.arguments?.getInt("quantity") ?: 1
                ContactOrderScreen(
                    productId = prodId,
                    initialQuantity = qty,
                    viewModel = storeViewModel,
                    onBackClick = { navController.popBackStack() },
                    onOrderSubmitted = { navController.navigate("home") }
                )
            }

            composable("categories") {
                CategoryListScreen(
                    viewModel = storeViewModel,
                    onCategoryClick = { catId -> navController.navigate("product_list?category=$catId") }
                )
            }

            composable("search") {
                SearchScreen(
                    viewModel = storeViewModel,
                    onProductClick = { prodId -> navController.navigate("product_detail/$prodId") }
                )
            }

            composable("wishlist") {
                WishlistScreen(
                    viewModel = storeViewModel,
                    onProductClick = { prodId -> navController.navigate("product_detail/$prodId") }
                )
            }

            composable("notifications") {
                NotificationsScreen(
                    viewModel = storeViewModel
                )
            }

            composable("order_tracker") {
                OrderTrackerScreen(
                    viewModel = storeViewModel
                )
            }

            composable("account") {
                AccountScreen(
                    viewModel = storeViewModel,
                    onNavigateWishlist = { navController.navigate("wishlist") },
                    onNavigateOrders = { navController.navigate("order_tracker") },
                    onNavigateAdmin = {
                        if (isAdminLoggedIn) navController.navigate("admin_dashboard")
                        else navController.navigate("admin_login")
                    }
                )
            }

            // Admin Routes
            composable("admin_login") {
                AdminLoginScreen(
                    adminViewModel = adminViewModel,
                    onBackClick = { navController.popBackStack() },
                    onLoginSuccess = {
                        navController.navigate("admin_dashboard") {
                            popUpTo("admin_login") { inclusive = true }
                        }
                    }
                )
            }

            composable("admin_dashboard") {
                AdminDashboardScreen(
                    adminViewModel = adminViewModel,
                    onNavigate = { route -> navController.navigate(route) },
                    onLogout = {
                        adminViewModel.logout()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("admin_products") {
                AdminProductListScreen(
                    adminViewModel = adminViewModel,
                    onBackClick = { navController.popBackStack() },
                    onEditProduct = { id -> navController.navigate("admin_product_edit/$id") },
                    onAddNewProduct = { navController.navigate("admin_product_edit/new") }
                )
            }

            composable(
                route = "admin_product_edit/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val prodId = backStackEntry.arguments?.getString("productId") ?: "new"
                AdminProductEditScreen(
                    productId = prodId,
                    adminViewModel = adminViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable("admin_product_edit_new") {
                AdminProductEditScreen(
                    productId = "new",
                    adminViewModel = adminViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable("admin_categories") {
                AdminCategoryScreen(
                    adminViewModel = adminViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("admin_orders") {
                AdminOrderListScreen(
                    adminViewModel = adminViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("admin_messages") {
                AdminMessageListScreen(
                    adminViewModel = adminViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("admin_banners") {
                AdminBannerScreen(
                    adminViewModel = adminViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("admin_settings") {
                AdminSettingsScreen(
                    adminViewModel = adminViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
