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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    adminViewModel: AdminViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSettings by adminViewModel.storeSettings.collectAsStateWithLifecycle()

    var storeName by remember(currentSettings) { mutableStateOf(currentSettings.storeName) }
    var storeSubtitle by remember(currentSettings) { mutableStateOf(currentSettings.storeSubtitle) }
    var storeDescription by remember(currentSettings) { mutableStateOf(currentSettings.storeDescription) }
    var whatsAppNumber by remember(currentSettings) { mutableStateOf(currentSettings.whatsAppNumber) }
    var phoneNumber by remember(currentSettings) { mutableStateOf(currentSettings.phoneNumber) }
    var email by remember(currentSettings) { mutableStateOf(currentSettings.email) }
    var address by remember(currentSettings) { mutableStateOf(currentSettings.address) }
    var businessHours by remember(currentSettings) { mutableStateOf(currentSettings.businessHours) }
    var currency by remember(currentSettings) { mutableStateOf(currentSettings.currency) }
    var deliveryInfo by remember(currentSettings) { mutableStateOf(currentSettings.deliveryInfo) }
    var returnPolicy by remember(currentSettings) { mutableStateOf(currentSettings.returnPolicy) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Store & App Settings", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.testTag("admin_settings_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Store Identity", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = storeName,
                onValueChange = { storeName = it },
                label = { Text("Store Name *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("store_name_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = storeSubtitle,
                onValueChange = { storeSubtitle = it },
                label = { Text("Store Subtitle / Tagline") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = storeDescription,
                onValueChange = { storeDescription = it },
                label = { Text("Store About Description") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Contact Methods (WhatsApp / Call)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = whatsAppNumber,
                onValueChange = { whatsAppNumber = it },
                label = { Text("WhatsApp Number (Include country code e.g. +1234567890) *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("store_whatsapp_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number for Calls *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Store Email Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Physical Store Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Regional & Business Details", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = { Text("Currency Symbol ($)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = businessHours,
                    onValueChange = { businessHours = it },
                    label = { Text("Business Hours") },
                    singleLine = true,
                    modifier = Modifier.weight(2f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = deliveryInfo,
                onValueChange = { deliveryInfo = it },
                label = { Text("Delivery Information") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = returnPolicy,
                onValueChange = { returnPolicy = it },
                label = { Text("Return Policy") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(errorMessage ?: "", color = Color.Red, fontSize = 12.sp)
            }

            if (successMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(successMessage ?: "", color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    errorMessage = null
                    successMessage = null

                    val updated = currentSettings.copy(
                        storeName = storeName,
                        storeSubtitle = storeSubtitle,
                        storeDescription = storeDescription,
                        whatsAppNumber = whatsAppNumber,
                        phoneNumber = phoneNumber,
                        email = email,
                        address = address,
                        businessHours = businessHours,
                        currency = currency,
                        deliveryInfo = deliveryInfo,
                        returnPolicy = returnPolicy
                    )

                    adminViewModel.saveSettings(
                        settings = updated,
                        onSuccess = { successMessage = "Store Settings updated successfully!" },
                        onError = { err -> errorMessage = err }
                    )
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_settings_btn")
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Store Settings", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
