package com.example.sosneuromobile.ui.theme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val latestExam = remember {
        exams?.let {
            if (it.length() > 0) {
                it.getJSONObject(0)
            } else {
                null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "SOS Neuro", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0), // Azul
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFE3F2FD)), // Plano de fundo azul claro
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Olá, ${displayName.uppercase()}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFF1565C0) // Azul
                    )
                )
                Text(
                    text = "Bem-vindo a sua área restrita",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Seus dados pessoais:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF1565C0) // Azul
                    )
                )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = displayName.uppercase(),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )
                    Text(
                        text = "Data de Nascimento: $dataNasc",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )
                    Text(
                        text = "Email: $email",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )
                    Text(
                        text = "Telefone: $telefone",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )
                }

                TextButton(onClick = { /* Editar informações */ }) {
                    Text(text = "Editar informações", color = Color(0xFF1565C0), fontSize = 18.sp)
                }

                Text(
                    text = "Seu resultado mais recente:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF1565C0) // Azul
                    )
                )

                latestExam?.let {
                    val dataRealizacao = it.optString("data_realizacao", "N/A")
                    val urlExame = it.optString("url_exame", null)

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .background(Color(0xFFBBDEFB), shape = MaterialTheme.shapes.medium) // Fundo azul claro
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Data de Realização: $dataRealizacao",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewFile(context, urlExame) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "Visualizar Exame",
                                    tint = Color(0xFF1565C0) // Azul
                                )
                            }
                            IconButton(
                                onClick = { downloadFile(context, urlExame) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Baixar Exame",
                                    tint = Color(0xFF1565C0) // Azul
                                )
                            }
                        }
                    }
                } ?: run {
                    Text(text = "Nenhum exame encontrado", color = Color.Gray)
                }
            }
        }
    )
}

fun viewFile(context: Context, url: String?) {
    url?.let {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it)).apply {
            setDataAndType(Uri.parse(it), "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        }
        context.startActivity(browserIntent)
    }
}

fun downloadFile(context: Context, url: String?) {
    url?.let {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
        context.startActivity(browserIntent)
    }
}
