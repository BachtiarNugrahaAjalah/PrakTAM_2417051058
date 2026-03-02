package com.example.praktam_2417051058

import Model.KegiatanSource
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2417051058Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().padding(all = 30.dp).verticalScroll(scrollState)) {
        val scrollState = rememberScrollState()

        KegiatanSource.dummyKegiatan.forEach { kegiatan ->
            Image(
                painter = painterResource(id = kegiatan.ImageRes),
                contentDescription = kegiatan.namaKegiatan,
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Crop
            )

            Text(text = "Nama Kegiatan : ${kegiatan.namaKegiatan}")
            Text(text = "Deskripsi Kegiatan : ${kegiatan.deskripsi}")
            Text(text = "Waktu Kegiatan : ${kegiatan.waktu}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrakTAM_2417051058Theme {
        Greeting()
    }
}