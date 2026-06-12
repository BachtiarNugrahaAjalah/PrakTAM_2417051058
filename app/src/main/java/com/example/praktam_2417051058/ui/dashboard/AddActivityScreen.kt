package com.example.praktam_2417051058.ui.dashboard

import com.example.praktam_2417051058.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.praktam_2417051058.data.remote.model.ActivityCategory
import com.example.praktam_2417051058.viewmodel.ActivityViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    viewModel: ActivityViewModel,
    onNavigateBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var selectedCategory by remember { mutableStateOf<ActivityCategory?>(null) }
    var duration by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ActivityViewModel.UiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is ActivityViewModel.UiEvent.NavigateBack -> {
                    onNavigateBack()
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Catat Aktivitas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Pilih Kategori", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.SemiBold
            )
            
            if (categories.isEmpty()) {
                Text("Memuat kategori...", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategorySelectItem(
                            category = category,
                            isSelected = selectedCategory?.categoryId == category.categoryId,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Durasi (menit)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Catatan (Opsional)") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.saveActivity(
                        categoryId = selectedCategory?.categoryId,
                        durationStr = duration,
                        notes = notes
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) {
                Text("Simpan Aktivitas", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun CategorySelectItem(
    category: ActivityCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Indigo600.copy(alpha = 0.1f) else Color.White
    val borderColor = if (isSelected) Indigo600 else Color(0xFFE2E8F0)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = getIconByName(category.icon), 
            contentDescription = null,
            tint = if (isSelected) Indigo600 else TextSecondary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = category.categoryName,
            color = if (isSelected) TextPrimary else Slate600,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
