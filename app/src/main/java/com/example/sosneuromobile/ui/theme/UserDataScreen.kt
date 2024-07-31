package com.example.sosneuromobile.ui.theme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDataScreen(userData: String, onLogout: () -> Unit) {
    val context = LocalContext.current
    val json = remember { JSONObject(userData) }
    val displayName = json.optString("display_name", "Usuário")
    val dataNasc = json.optString("data_nasc", "N/A")
    val email = json.optString("user_email", "N/A")
    val telefone = json.optString("telefone", "N/A")
    val exams = json.optJSONArray("exames")

    var isLoading by remember { mutableStateOf(true) }

    // Simulando o carregamento de tipos de exames
    LaunchedEffect(Unit) {
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "SOS Neuro") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                ),
                actions = {
                    TextButton(onClick = onLogout) {
                        Text(text = "Encerrar sessão", color = Color.White)
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Olá, ${displayName.uppercase()}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue
                )
                Text(text = "Bem-vindo a sua área restrita")
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Seus dados pessoais:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Blue
                )

                Text(
                    text = displayName.uppercase(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = dataNasc,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = telefone,
                    style = MaterialTheme.typography.bodyLarge
                )

                TextButton(onClick = { /* Editar informações */ }) {
                    Text(text = "Editar informações", color = Color.Blue)
                }

                Text(
                    text = "Seus resultados:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Blue
                )

                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    exams?.let {
                        LazyColumn {
                            items(it.length()) { index ->
                                val exam = it.getJSONObject(index)
                                val dataRealizacao = exam.optString("data_realizacao", "N/A")
                                val urlExame = exam.optString("url_exame", null)

                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = "Data Realização: $dataRealizacao")
                                    urlExame?.let {
                                        Text(
                                            text = "Clique aqui para visualizar o exame.",
                                            modifier = Modifier.clickable {
                                                openPdfInBrowser(context, it)
                                            },
                                            color = Color.Blue
                                        )
                                    }
                                    Divider(color = Color.Gray, thickness = 1.dp)
                                }
                            }
                        }
                    } ?: run {
                        Text(text = "Nenhum exame encontrado", color = Color.Gray)
                    }
                }
            }
        }
    )
}

fun openPdfInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(Uri.parse(url), "application/pdf")
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY
    }
    context.startActivity(Intent.createChooser(intent, "Open PDF"))
}
