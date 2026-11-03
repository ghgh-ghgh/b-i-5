package com.example.bi5.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.bi5.ui.viewmodel.CourseViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    viewModel: CourseViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val courses by viewModel.courses.collectAsState()
    val categories by viewModel.categories.collectAsState()

    val course = courses.find { it.id == courseId }
    val categoryName = categories.find { it.id == course?.categoryId }?.name ?: "Khác"

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showTrailerPlayer by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val exoPlayer = remember(course?.trailerUrl) {
        if (!course?.trailerUrl.isNullOrEmpty()) {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(Uri.parse(course.trailerUrl))
                setMediaItem(mediaItem)
                prepare()
            }
        } else {
            null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer?.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết khóa học") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    if (course != null) {
                        IconButton(onClick = { onNavigateToEdit(course.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Sửa")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Xóa")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (course == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy thông tin khóa học.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Trailer Player or Thumbnail
                if (showTrailerPlayer && course.trailerUrl.isNotEmpty() && exoPlayer != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
                                    player = exoPlayer
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = course.thumbnail.ifEmpty { "https://via.placeholder.com/300" },
                            contentDescription = course.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (course.trailerUrl.isNotEmpty()) {
                            Button(
                                onClick = { showTrailerPlayer = true },
                                modifier = Modifier.align(Alignment.Center),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Xem Trailer")
                            }
                        }
                    }
                }

                // Title & Category
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Text(
                            text = categoryName,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Price & Duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val vndFormat = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                    val priceStr = try { vndFormat.format(course.price) } catch (_: Exception) { "${course.price} VNĐ" }

                    Column {
                        Text("Học phí", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(priceStr, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Thời lượng", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${course.duration} giờ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider()

                // Instructor
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Giảng viên", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(course.instructor, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                }

                // Description
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Mô tả khóa học", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(course.description.ifEmpty { "Không có mô tả" }, style = MaterialTheme.typography.bodyMedium)
                }

                // Created date if available
                if (course.createdAt.isNotEmpty()) {
                    Text(
                        text = "Ngày tạo: ${course.createdAt}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa khóa học '${course?.title}' không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        if (course != null) {
                            viewModel.deleteCourse(course.id)
                            onNavigateBack()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}
