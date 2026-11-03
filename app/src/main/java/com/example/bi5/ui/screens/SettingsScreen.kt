package com.example.bi5.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bi5.ui.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CourseViewModel,
    onNavigateBack: () -> Unit
) {
    val currentBaseUrl by viewModel.baseUrl.collectAsState()
    var urlInput by remember { mutableStateOf(currentBaseUrl) }
    var successMessage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cấu hình MockAPI Base URL") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nhập đường dẫn MockAPI Base URL của bạn (VD: https://67879155c4ada42c916d2b51.mockapi.io/api/v1/):",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                label = { Text("Base URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (successMessage) {
                Text(
                    text = "Đã cập nhật Base URL và tải lại dữ liệu thành công!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    if (urlInput.isNotBlank()) {
                        viewModel.updateBaseUrl(urlInput.trim())
                        successMessage = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Lưu và Tải lại dữ liệu", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
