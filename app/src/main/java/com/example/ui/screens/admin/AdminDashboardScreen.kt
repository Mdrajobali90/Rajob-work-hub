package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.AdminViewModel

data class StatItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminViewModel: AdminViewModel,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalProducts by adminViewModel.totalProductsCount.collectAsStateWithLifecycle()
    val totalCategories by adminViewModel.totalCategoriesCount.collectAsStateWithLifecycle()
    val totalOrders by adminViewModel.totalOrdersCount.collectAsStateWithLifecycle()
    val pendingOrders by adminViewModel.pendingOrdersCount.collectAsStateWithLifecycle()
    val totalMessages by adminViewModel.totalMessagesCount.collectAsStateWithLifecycle()
    val outOfStock by adminViewModel.outOfStockCount.collectAsStateWithLifecycle()

    val stats = listOf(
        StatItem("Total Products", totalProducts.toString(), Icons.Default.Inventory, MaterialTheme.colorScheme.primary),
        StatItem("Total Categories", totalCategories.toString(), Icons.Default.Category, Color(0xFF0288D1)),
        StatItem("Total Messages", totalMessages.toString(), Icons.Default.Email, Color(0xFF7B1FA2)),
        StatItem("Total Orders", totalOrders.toString(), Icons.Default.Receipt, Color(0xFF2E7D32)),
        StatItem("Pending Orders", pendingOrders.toString(), Icons.Default.PendingActions, Color(0xFFE65100)),
        StatItem("Out of Stock", outOfStock.toString(), Icons.Default.Warning, Color.Red)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Exit")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate("admin_settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.testTag("admin_dashboard_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Store Performance Overview",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stat Cards Grid (2x3)
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(item = stats[0], modifier = Modifier.weight(1f))
                    StatCard(item = stats[1], modifier = Modifier.weight(1f))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(item = stats[2], modifier = Modifier.weight(1f))
                    StatCard(item = stats[3], modifier = Modifier.weight(1f))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(item = stats[4], modifier = Modifier.weight(1f))
                    StatCard(item = stats[5], modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Quick Actions",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            QuickActionTile(
                title = "Add New Product",
                subtitle = "Create product with images, pricing, stock & options",
                icon = Icons.Default.AddCircle,
                iconColor = MaterialTheme.colorScheme.primary,
                onClick = { onNavigate("admin_product_edit_new") }
            )

            QuickActionTile(
                title = "Manage Products",
                subtitle = "View, edit, duplicate, activate/deactivate products",
                icon = Icons.Default.Inventory,
                iconColor = Color(0xFF0288D1),
                onClick = { onNavigate("admin_products") }
            )

            QuickActionTile(
                title = "Manage Categories",
                subtitle = "Add, edit or organize product category icons",
                icon = Icons.Default.Category,
                iconColor = Color(0xFF7B1FA2),
                onClick = { onNavigate("admin_categories") }
            )

            QuickActionTile(
                title = "Customer Order Requests",
                subtitle = "View and update customer order request statuses",
                icon = Icons.Default.Receipt,
                iconColor = Color(0xFF2E7D32),
                onClick = { onNavigate("admin_orders") }
            )

            QuickActionTile(
                title = "Customer Messages",
                subtitle = "Read customer inquiries and reply via WhatsApp/Phone",
                icon = Icons.Default.Email,
                iconColor = Color(0xFFE65100),
                onClick = { onNavigate("admin_messages") }
            )

            QuickActionTile(
                title = "Homepage Banners",
                subtitle = "Add and manage promotional hero slider banners",
                icon = Icons.Default.ViewCarousel,
                iconColor = Color(0xFF00897B),
                onClick = { onNavigate("admin_banners") }
            )

            QuickActionTile(
                title = "App & Store Settings",
                subtitle = "Configure WhatsApp number, store logo, address & policies",
                icon = Icons.Default.Settings,
                iconColor = Color(0xFF546E7A),
                onClick = { onNavigate("admin_settings") }
            )
        }
    }
}

@Composable
fun StatCard(item: StatItem, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
