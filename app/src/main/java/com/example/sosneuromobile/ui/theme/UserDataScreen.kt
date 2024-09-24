package com.example.sosneuromobile.ui.theme

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
fun UserDataScreen(userData: UserData, results: List<ExamResult>, onLogout: () -> Unit) {
    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "SOS Neuro",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary, // Cor primária
            titleContentColor = MaterialTheme.colorScheme.onPrimary // Cor do conteúdo na cor primária
        ), actions = {
            TextButton(onClick = onLogout) {
                Text(text = "Encerrar sessão", color = MaterialTheme.colorScheme.onPrimary)
            }
        })
    }, content = { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background), // Cor de fundo do tema
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Olá, ${userData.displayName.uppercase()}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary // Usando a cor primária do tema
                )
            )
            Text(
                text = "Bem-vindo a sua área restrita",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground // Cor do texto padrão
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Seus dados pessoais:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Nome: ${userData.displayName.uppercase()}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                )
                Text(
                    text = "Data de Nascimento: ${userData.birthDate}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                )
                Text(
                    text = "Email: ${userData.email}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                )
                Text(
                    text = "Telefone: ${userData.phone}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                )
            }

            Text(
                text = "Seus resultados:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            results.forEach { result ->
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium) // Usando a cor de superfície
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = result.examDate,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )
                    Text(
                        text = result.examType,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewFile(context, result.downloadLink) }) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Visualizar Exame",
                                tint = MaterialTheme.colorScheme.primary // Usando a cor primária
                            )
                        }
                        IconButton(onClick = { downloadFile(context, result.downloadLink) }) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Baixar Exame",
                                tint = MaterialTheme.colorScheme.primary // Usando a cor primária
                            )
                        }
                    }
                }
            }

            if (results.isEmpty()) {
                Text(text = "Nenhum exame encontrado", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    })
}

fun viewFile(context: Context, url: String) {
    if (url.isNotEmpty()) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context, "Nenhum aplicativo para abrir este link encontrado", Toast.LENGTH_SHORT
            ).show()
        }
    }
}

fun downloadFile(context: Context, url: String?) {
    url?.let {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
        context.startActivity(browserIntent)
    }
}
