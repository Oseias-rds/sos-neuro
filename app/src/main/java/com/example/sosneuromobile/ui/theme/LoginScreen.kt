package com.example.sosneuromobile.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sosneuromobile.MainActivity
import com.example.sosneuromobile.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: (String, String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "SOS Neuro") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Logo",
                    modifier = Modifier.size(128.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                LoginFields(onLoginSuccess)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Entrega de Resultados", color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(getMenuItems()) { menuItem ->
                        Text(
                            text = menuItem.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Handle click */ }
                                .padding(16.dp)
                        )
                    }
                }
                // Utilize a função Footer
                Footer(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    )
}


@Composable
fun LoginFields(onLoginSuccess: (String, String) -> Unit) {
    val userLoginState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val loginErrorState = remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current as MainActivity

    fun authenticate(user_login: String, user_pass: String) {
        val url = "https://sosneuro.com.br/index.php/entrega-de-exames?user_login=$user_login&user_pass=$user_pass"
        context.buscarUsuario(url,
            onSuccess = { jsonResponse ->
                onLoginSuccess(user_login, user_pass)
            },
            onError = {
                loginErrorState.value = it
            })
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = userLoginState.value,
            onValueChange = { userLoginState.value = it },
            label = { Text("Login") },
            leadingIcon = { Icon(Icons.Filled.CheckCircle, contentDescription = "Login Icon") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Senha") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Lock Icon") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                authenticate(userLoginState.value, passwordState.value)
            },
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        loginErrorState.value?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Esqueceu a senha?",
            color = Color.Blue,
            modifier = Modifier.clickable { /* Handle forgot password */ }
        )
    }
}

data class MenuItem(val title: String)

fun getMenuItems(): List<MenuItem> {
    return listOf()
}
