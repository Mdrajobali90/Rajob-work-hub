package com.example.ui.screens.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.OrderRequest
import com.example.ui.components.StatusBadge
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.AdminViewModel
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderListScreen(
    adminViewModel: AdminViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by adminViewModel.allOrders.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Orders (${orders.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.testTag("admin_order_list_screen")
    ) { innerPadding ->
        if (orders.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text("No Order Requests Received Yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(orders, key = { it.id }) { order ->
                    AdminOrderCard(
                        order = order,
                        onStatusChange = { newStatus -> adminViewModel.updateOrderStatus(order.id, newStatus) },
                        onDelete = { adminViewModel.deleteOrder(order) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(
    order: OrderRequest,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var statusMenuExpanded by remember { mutableStateOf(false) }
    val statuses = listOf("Pending", "Confirmed", "Processing", "Completed", "Cancelled")

    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(order.createdAt))

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Order ID: ${order.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)

                Box {
                    AssistChip(
                        onClick = { statusMenuExpanded = true },
                        label = { Text(order.status, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )

                    DropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        statuses.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    onStatusChange(st)
                                    statusMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Customer: ${order.customerName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Phone: ${order.phone} | WhatsApp: ${order.whatsAppNumber.ifBlank { "N/A" }}", fontSize = 12.sp)
            Text("Product: ${order.productName} (Qty: ${order.quantity})", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("Total Price: $${String.format("%.2f", order.totalPrice)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Address: ${order.address}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (order.customerMessage.isNotBlank()) {
                Text("Note: ${order.customerMessage}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(dateStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Contact Customer via WhatsApp
                    Button(
                        onClick = {
                            val cleanNum = (if (order.whatsAppNumber.isNotBlank()) order.whatsAppNumber else order.phone).replace(Regex("[^0-9+]"), "")
                            val msg = "Hello ${order.customerName}, regarding your order request #${order.id} for ${order.productName}:"
                            val encoded = URLEncoder.encode(msg, "UTF-8")
                            val uri = Uri.parse("https://wa.me/$cleanNum?text=$encoded")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 11.sp, color = Color.White)
                    }

                    // Call Customer
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.phone}"))
                            context.startActivity(intent)
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Outlined.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontSize = 11.sp)
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
