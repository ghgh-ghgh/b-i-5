package com.example.bi5.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bi5.data.model.Category
import com.example.bi5.ui.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    viewModel: CourseViewModel,
    onNavigateBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val courses by viewModel.courses.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var nameInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }
    var dialogError by remember { mutableStateOf<String?>(null) }

    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    var deleteErrorSnackbar by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(deleteErrorSnackbar) {
        deleteErrorSnackbar?.let {
            snackbarHostState.showSnackbar(it)
            deleteErrorSnackbar = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý danh mục") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingCategory = null
                    nameInput = ""
                    descInput = ""
                    dialogError = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm danh mục")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (categories.isEmpty()) {
                Text(
                    text = "Chưa có danh mục nào.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(categories, key = { it.id }) { category ->
                        val courseCount = courses.count { it.categoryId == category.id }
                        CategoryItemCard(
                            category = category,
                            courseCount = courseCount,
                            onEdit = {
                                editingCategory = category
                                nameInput = category.name
                                descInput = category.description
                                dialogError = null
                                showDialog = true
                            },
                            onDelete = {
                                categoryToDelete = category
                            }
                        )
                    }
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editingCategory == null) "Thêm danh mục" else "Sửa danh mục") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (dialogError != null) {
                        Text(dialogError ?: "", color = MaterialTheme.colorScheme.error)
                    }
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Tên danh mục *") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("Mô tả") },
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nameInput.isBlank()) {
                            dialogError = "Tên danh mục không được để trống."
                            return@TextButton
                        }
                        if (editingCategory == null) {
                            viewModel.addCategory(Category(name = nameInput.trim(), description = descInput.trim())) { success ->
                                if (success) showDialog = false
                            }
                        } else {
                            viewModel.updateCategory(editingCategory!!.id, Category(id = editingCategory!!.id, name = nameInput.trim(), description = descInput.trim())) { success ->
                                if (success) showDialog = false
                            }
                        }
                    }
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Xác nhận xóa danh mục") },
            text = { Text("Bạn có muốn xóa danh mục '${categoryToDelete?.name}' không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cat = categoryToDelete!!
                        categoryToDelete = null
                        viewModel.deleteCategory(cat.id) { success, errorMsg ->
                            if (!success && errorMsg != null) {
                                deleteErrorSnackbar = errorMsg
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun CategoryItemCard(
    category: Category,
    courseCount: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (category.description.isNotBlank()) {
                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Số lượng khóa học: $courseCount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Sửa")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
