package com.example.praktam_2417051058

import Model.Kegiatan
import Model.KegiatanSource
import android.R
import com.example.praktam_2417051058.ui.theme.PurpleGrey40
import android.os.Bundle
import android.widget.Button
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.praktam_2417051058.ui.theme.PrakTAM_2417051058Theme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2417051058Theme {
//                DaftarKegiatanScreen()
                DashboardSection()
            }
        }
    }
}

@Composable
fun HeaderSection(){
    Column(modifier = Modifier.fillMaxWidth().background(PurpleGrey40, shape = RoundedCornerShape(5.dp) ).padding(20.dp)) {
        Text(
            text = "Hai, Pengguna",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text("Selamat Datang di LifePattern", color = Color.White)
    }
}

@Composable
fun DashboardSection(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ){
        DaftarKegiatanScreen()
    }
}

@Composable
fun DaftarKegiatanScreen() {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        HeaderSection()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Text(
                    text = "Riwayat Hari Ini",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(KegiatanSource.dummyKegiatan) {
                            kegiatan -> KegiatanRowItem(kegiatan = kegiatan)
                    }
                }

                Spacer(modifier = Modifier.height(45.dp))

                Text(
                    text = "Kegiatan Hari Ini",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            items(KegiatanSource.dummyKegiatan) {
                    kegiatan -> DetailScreen(kegiatan = kegiatan)
            }
        }
    }
}

@Composable
fun DetailScreen(kegiatan: Kegiatan){
    var isDoing by remember {mutableStateOf(false)}
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier){
                Image(
                    painter = painterResource(id = kegiatan.ImageRes),
                    contentDescription = kegiatan.namaKegiatan,
                    modifier = Modifier
                        .height(80.dp)
                        .width(80.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column() {
                Text(
                    text = kegiatan.namaKegiatan,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                
                Text(
                    text = "Waktu : ${kegiatan.waktu}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 14.sp
                )

                Button(
                    onClick = { },
                    modifier = Modifier
                ) {
                    Text("Detail Kegiatan")
                }
            }

            Column(modifier = Modifier.fillMaxHeight()) {
                IconButton(
                    onClick = {isDoing = !isDoing},
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = if(isDoing) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = "Doing icon",
                        tint = if (isDoing) Color.Green else Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun KegiatanRowItem(kegiatan: Kegiatan){
    Card(
        modifier = Modifier.width(200.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = kegiatan.ImageRes),
                contentDescription = kegiatan.namaKegiatan,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)){
                Text(
                    text = kegiatan.namaKegiatan,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = kegiatan.deskripsi,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrakTAM_2417051058Theme {
//        DaftarKegiatanScreen()
        DashboardSection()
    }
}