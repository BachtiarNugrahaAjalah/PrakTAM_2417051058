package com.example.praktam_2417051058

import Model.Kegiatan
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.praktam_2417051058.Network.RetrofitClient
import com.example.praktam_2417051058.ui.theme.PrakTAM_2417051058Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2417051058Theme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    var kegiatans by remember { mutableStateOf<List<Kegiatan>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    // Fetch data terpusat di sini
    LaunchedEffect(Unit) {
        try {
            kegiatans = RetrofitClient.instance.getKegiatan()
            isLoading = false
            isError = false
        } catch (e: Exception) {
            isLoading = false
            isError = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            DashboardSection(navController, kegiatans, isLoading, isError)
        }
        composable("detail/{nama}") { backStackEntry ->
            val nama = backStackEntry.arguments?.getString("nama")
            val kegiatan = kegiatans.find { it.namaKegiatan == nama }

            if (kegiatan != null) {
                DetailScreen(kegiatan = kegiatan, navController = navController, isFullScreen = true)
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "Hai, Pengguna",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "Selamat Datang di LifePattern",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun DashboardSection(navController: NavController, kegiatans: List<Kegiatan>, isLoading: Boolean, isError: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (isError || kegiatans.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Gagal Memuat Data", color = Color.Red, style = MaterialTheme.typography.titleLarge)
                    Text("Pastikan koneksi internet menyala.", textAlign = TextAlign.Center)
                }
            }
        } else {
            DaftarKegiatanScreen(navController = navController, kegiatans = kegiatans)
        }
    }
}

@Composable
fun DaftarKegiatanScreen(navController: NavController, kegiatans: List<Kegiatan>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeaderSection()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Riwayat Hari Ini",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(kegiatans) { kegiatan ->
                    KegiatanRowItem(kegiatan = kegiatan, navController = navController)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Kegiatan Hari Ini",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(kegiatans) { kegiatan ->
            DetailScreen(kegiatan = kegiatan, navController = navController)
        }
    }
}

@Composable
fun DetailScreen(kegiatan: Kegiatan, navController: NavController, isFullScreen: Boolean = false) {
    var isDoing by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Gambar diletakkan di atas agar layout rapi
                AsyncImage(
                    model = kegiatan.imageUrl,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.olahraga),
                    error = painterResource(id = R.drawable.tidur),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isFullScreen) 200.dp else 120.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = kegiatan.namaKegiatan,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "Waktu: ${kegiatan.waktu}", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { isDoing = !isDoing }) {
                        Icon(
                            imageVector = if (isDoing) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = if (isDoing) Color.Green else Color.Gray
                        )
                    }
                }

                if (isFullScreen) {
                    Text(
                        text = kegiatan.deskripsi,
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = {
                        if (isFullScreen) {
                            navController.popBackStack()
                        } else {
                            coroutineScope.launch {
                                isProcessing = true
                                delay(2000)
                                snackbarHostState.showSnackbar("Kegiatan ${kegiatan.namaKegiatan} selesai!")
                                isProcessing = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text(if (isFullScreen) "Kembali" else "Lakukan")
                    }
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun KegiatanRowItem(kegiatan: Kegiatan, navController: NavController) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { navController.navigate("detail/${kegiatan.namaKegiatan}") },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = kegiatan.imageUrl,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.olahraga),
                error = painterResource(id = R.drawable.tidur),
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = kegiatan.namaKegiatan,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = kegiatan.deskripsi,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PrakTAM_2417051058Theme {
        val navController = rememberNavController()
        AppNavigation(navController)
    }
}