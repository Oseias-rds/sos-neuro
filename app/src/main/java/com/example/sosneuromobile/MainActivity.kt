package com.example.sosneuromobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sosneuromobile.ui.theme.SosNeuroMobileTheme
import com.example.sosneuromobile.ui.WelcomeScreen
import sosneuromobile.ui.LoginScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SosNeuroMobileTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("welcome")
            })
        }
        composable("welcome") {
            WelcomeScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {
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
                LoginFields()
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
                Footer(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    )
}

@Composable
fun LoginFields() {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email Icon") },
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
            onClick = { /* Handle login */ },
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Esqueceu a senha?",
            color = Color.Blue,
            modifier = Modifier.clickable { /* Handle forgot password */ }
        )
    }
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "© 2024 SOS NEURO. Todos os direitos reservados.")
        Text(text = "Designed by MENCOL TECNOLOGIA")
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Política de Privacidade",
                color = Color.Blue,
                modifier = Modifier.clickable { /* Handle Privacy Policy */ }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Termos de Serviço",
                color = Color.Blue,
                modifier = Modifier.clickable { /* Handle Terms of Service */ }
            )
        }
    }
}

data class MenuItem(val title: String)

fun getMenuItems(): List<MenuItem> {
    return listOf(
        // Add your menu items here
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SosNeuroMobileTheme {
        AppContent()
    }
}
