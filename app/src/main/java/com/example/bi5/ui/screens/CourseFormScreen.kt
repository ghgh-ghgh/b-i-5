package com.example.bi5.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bi5.data.model.Course
import com.example.bi5.ui.viewmodel.CourseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseFormScreen(
    courseId: String?, // null if adding, non-null if editing
    viewModel: CourseViewModel,
    onNavigateBack: () -> Unit
) {
    val courses by viewModel.courses.collectAsState()
    val categories by viewModel.categories.collectAsState()

    val existingCourse = remember(courseId, courses) {
        if (courseId != null) courses.find { it.id == courseId } else null
    }

    var title by remember { mutableStateOf(existingCourse?.title ?: "") }
    var description by remember { mutableStateOf(existingCourse?.description ?: "") }
    var categoryId by remember { mutableStateOf(existingCourse?.categoryId ?: (categories.firstOrNull()?.id ?: "")) }
    var instructor by remember { mutableStateOf(existingCourse?.instructor ?: "") }
    var price by remember { mutableStateOf(existingCourse?.price?.toString() ?: "") }
    var duration by remember { mutableStateOf(existingCourse?.duration?.toString() ?: "") }
    var thumbnail by remember { mutableStateOf(existingCourse?.thumbnail ?: "") }
    var trailerUrl by remember { mutableStateOf(existingCourse?.trailerUrl ?: "") }

    var expandedCategoryDropdown by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val selectedCategoryName = categories.find { it.id == categoryId }?.name ?: "Chọn danh mục"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (courseId == null) "Thêm khóa học" else "Sửa khóa học") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tên khóa học *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCategoryDropdown,
                onExpandedChange = { expandedCategoryDropdown = !expandedCategoryDropdown }
            ) {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Danh mục *") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoryDropdown,
                    onDismissRequest = { expandedCategoryDropdown = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                categoryId = cat.id
                                expandedCategoryDropdown = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = instructor,
                onValueChange = { instructor = it },
                label = { Text("Giảng viên *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Học phí (VNĐ) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Thời lượng (giờ) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = thumbnail,
                onValueChange = { thumbnail = it },
                label = { Text("URL Ảnh bìa (Thumbnail)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Thumbnail Preview
            if (thumbnail.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = thumbnail,
                        contentDescription = "Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            OutlinedTextField(
                value = trailerUrl,
                onValueChange = { trailerUrl = it },
                label = { Text("URL Video Trailer") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Mô tả chi tiết") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    // Validation
                    if (title.isBlank()) {
                        errorMessage = "Vui lòng nhập tên khóa học."
                        return@Button
                    }
                    if (categoryId.isBlank()) {
                        errorMessage = "Vui lòng chọn danh mục."
                        return@Button
                    }
                    if (instructor.isBlank()) {
                        errorMessage = "Vui lòng nhập tên giảng viên."
                        return@Button
                    }
                    val priceVal = price.toDoubleOrNull()
                    if (priceVal == null || priceVal < 0) {
                        errorMessage = "Học phí phải là số >= 0."
                        return@Button
                    }
                    val durationVal = duration.toIntOrNull()
                    if (durationVal == null || durationVal < 0) {
                        errorMessage = "Thời lượng phải là số nguyên >= 0."
                        return@Button
                    }

                    isSubmitting = true
                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val course = Course(
                        id = existingCourse?.id ?: "",
                        title = title.trim(),
                        description = description.trim(),
                        categoryId = categoryId,
                        instructor = instructor.trim(),
                        price = priceVal,
                        duration = durationVal,
                        thumbnail = thumbnail.trim(),
                        trailerUrl = trailerUrl.trim(),
                        createdAt = existingCourse?.createdAt.takeIf { !it.isNullOrEmpty() } ?: currentDate
                    )

                    if (courseId == null) {
                        viewModel.addCourse(course) { success ->
                            isSubmitting = false
                            if (success) onNavigateBack()
                        }
                    } else {
                        viewModel.updateCourse(courseId, course) { success ->
                            isSubmitting = false
                            if (success) onNavigateBack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (courseId == null) "Thêm khóa học" else "Lưu thay đổi", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
