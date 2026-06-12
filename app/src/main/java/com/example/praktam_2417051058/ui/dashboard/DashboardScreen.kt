package com.example.praktam_2417051058.ui.dashboard

import com.example.praktam_2417051058.ui.theme.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2417051058.data.local.entity.ActivityRecordEntity
import com.example.praktam_2417051058.data.remote.model.ActivityCategory
import com.example.praktam_2417051058.ui.navigation.Screen
import com.example.praktam_2417051058.viewmodel.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

fun getIconByName(name: String): ImageVector {
    return when (name) {
        "bed" -> Icons.Rounded.Bed
        "fitness_center" -> Icons.Rounded.FitnessCenter
        "work" -> Icons.Rounded.Work
        "menu_book" -> Icons.Rounded.MenuBook
        "people" -> Icons.Rounded.People
        else -> Icons.Rounded.Star
    }
}

fun formatDuration(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun LifePatternDashboard(
    navController: NavController,
    viewModel: ActivityViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val records by viewModel.activityRecords.collectAsState()
    val streak by viewModel.streak.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ActivityViewModel.UiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is ActivityViewModel.UiEvent.NavigateBack -> {
                    snackbarHostState.showSnackbar(event.successMessage)
                }
            }
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddActivity.route) },
                containerColor = Indigo600,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Tambah")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                HeaderSection(
                    onSummaryClick = { navController.navigate(Screen.Summary.route) },
                    onRecommendationClick = { navController.navigate(Screen.Recommendation.route) }
                )
            }
            
            item {
                var isNotificationsEnabled by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notifikasi Harian", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Switch(
                        checked = isNotificationsEnabled,
                        onCheckedChange = {
                            isNotificationsEnabled = it
                            val msg = if (it) "Notifikasi diaktifkan" else "Notifikasi dinonaktifkan"
                            scope.launch {
                                snackbarHostState.showSnackbar(msg)
                            }
                        }
                    )
                }
            }

            item {
                DailySummaryCard(streak = streak)
            }
            
            item {
                Text(
                    text = "Kategori Aktivitas",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (categories.isEmpty()) {
                    Text("Memuat data...", color = Color.Gray)
                } else {
                    CategoryRow(
                        categories = categories,
                        onCategoryClick = { categoryId ->
                            navController.navigate(Screen.CategoryDetail.createRoute(categoryId))
                        }
                    )
                }
            }
            
            item {
                Text(
                    text = "Riwayat Hari Ini",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (records.isEmpty()) {
                item {
                    Text("Belum ada aktivitas hari ini.", color = Color.Gray)
                }
            } else {
                items(records, key = { it.recordId }) { record ->
                    val category = categories.find { it.categoryId == record.categoryId }
                    ActivityHistoryCard(
                        record = record,
                        category = category
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun HeaderSection(
    onSummaryClick: () -> Unit,
    onRecommendationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Halo, Pengguna! ✨",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                ),
                maxLines = 1
            )
            Text(
                text = "Siap merancang pola hidup sehat?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextSecondary
                )
            )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Tombol Ringkasan
            IconButton(
                onClick = onSummaryClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Indigo50)
            ) {
                Icon(Icons.Rounded.BarChart, contentDescription = "Ringkasan", tint = Indigo600, modifier = Modifier.size(20.dp))
            }

            // Tombol Rekomendasi (AI)
            IconButton(
                onClick = onRecommendationClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SuccessLight)
            ) {
                Icon(Icons.Rounded.Lightbulb, contentDescription = "Rekomendasi", tint = SuccessDark, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun DailySummaryCard(streak: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(TextPrimary, Slate900)
                )
            )
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Skor Pola Hidup",
                    color = TextTertiary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sangat Baik",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.LocalFireDepartment, contentDescription = "Streak", tint = WarningOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$streak Hari Streak", color = WarningOrange, fontWeight = FontWeight.Bold)
                }
        }
    }
}
}


@Composable
fun CategoryRow(categories: List<ActivityCategory>, onCategoryClick: (Int) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categories) { category ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onCategoryClick(category.categoryId) }
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Indigo600.copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getIconByName(category.icon),
                        contentDescription = category.categoryName,
                        tint = Indigo600,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.categoryName,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Slate600
                    )
                )
            }
        }
    }
}

@Composable
fun ActivityHistoryCard(record: ActivityRecordEntity, category: ActivityCategory?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Indigo600.copy(alpha = 0.1f))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Indigo50),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getIconByName(category?.icon ?: ""),
                contentDescription = null,
                tint = Indigo600
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = record.notes ?: category?.categoryName ?: "Aktivitas",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Schedule, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${formatTime(record.date)} • ${formatDuration(record.duration)}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
        }
    }
}
