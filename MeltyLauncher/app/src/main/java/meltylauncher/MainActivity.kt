package com.example.meltylauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF6200EE),
                    background = Color(0xFF121212),
                    surface = Color(0xFF1E1E1E)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LauncherMainScreen(filesDir.absolutePath)
                }
            }
        }
    }
}

@Composable
fun LauncherMainScreen(filesDirPath: String) {
    var installState by remember { mutableStateOf("NOT_INSTALLED") }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var statusText by remember { mutableStateOf("Listo para instalar") }
    val coroutineScope = rememberCoroutineScope()

    val playTime = "48.5 horas"
    val achievements = "32 / 35 Logros"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MBAACC DEDICATED LAUNCHER",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Melty Blood: Actress Again Current Code",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Steam Stats: $playTime | $achievements",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (installState == "DOWNLOADING") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { downloadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFBB86FC),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$statusText ${(downloadProgress * 100).toInt()}%",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(
                onClick = {
                    when (installState) {
                        "NOT_INSTALLED" -> {
                            installState = "DOWNLOADING"
                            coroutineScope.launch {
                                statusText = "Preparando directorios y Box64..."
                                delay(1000)
                                
                                for (i in 1..50) {
                                    downloadProgress = i / 100f
                                    statusText = "Descargando archivos base del entorno..."
                                    delay(30)
                                }

                                statusText = "Configurando Wine Prefix..."
                                delay(800)

                                for (i in 51..100) {
                                    downloadProgress = i / 100f
                                    statusText = "Sincronizando archivos del juego..."
                                    delay(30)
                                }

                                val gameDir = File(filesDirPath, "mbaacc_game")
                                if (!gameDir.exists()) gameDir.mkdirs()

                                installState = "INSTALLED"
                            }
                        }
                        "INSTALLED" -> {
                            try {
                                val gameDir = File(filesDirPath, "mbaacc_game")
                                val processBuilder = ProcessBuilder(
                                    "sh", "-c",
                                    "export BOX64_DYNAREC=1 && " +
                                    "export WINEPREFIX=${filesDirPath}/wineprefix && " +
                                    "echo 'Iniciando simulación de ejecución nativa...'"
                                )
                                processBuilder.directory(gameDir)
                                processBuilder.redirectErrorStream(true)
                                processBuilder.start()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (installState == "INSTALLED") Color(0xFF03DAC6) else Color(0xFF6200EE)
                ),
                enabled = installState != "DOWNLOADING"
            ) {
                Text(
                    text = when (installState) {
                        "NOT_INSTALLED" -> "DESCARGAR E INSTALAR ENTORNO"
                        "DOWNLOADING" -> "CONFIGURANDO..."
                        else -> "JUGAR"
                    },
                    color = if (installState == "INSTALLED") Color.Black else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}