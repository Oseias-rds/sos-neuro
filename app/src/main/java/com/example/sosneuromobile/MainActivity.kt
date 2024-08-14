package com.example.sosneuromobile

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.sosneuromobile.ui.theme.SosNeuroMobileTheme
import com.example.sosneuromobile.ui.theme.LoginScreen
import com.example.sosneuromobile.ui.theme.ResultadoExame
import com.example.sosneuromobile.ui.theme.UserDataScreen
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SosNeuroMobileTheme {
                AppNavigation()
            }
        }
    }

    fun buscarUsuario(
        URL: String,
        onSuccess: (Boolean, String) -> Unit, // Adicionado parâmetro para dados do usuário
        onError: (String) -> Unit
    ) {
        val stringRequest = StringRequest(
            Request.Method.GET, URL,
            { response ->
                try {
                    val document: Document = Jsoup.parse(response)
                    val usuarioValido = document.select(".header-info .texto").isNotEmpty()
                    val userData = document.select(".header-info .texto").text() // Substitua com o seletor correto
                    onSuccess(usuarioValido, userData)
                } catch (e: Exception) {
                    onError("Erro ao processar a resposta HTML: ${e.message}\nResposta HTML: $response")
                }
            },
            { error: VolleyError ->
                val errorResponse = error.networkResponse?.data?.let {
                    String(it, Charsets.UTF_8)
                } ?: "Resposta do servidor não disponível"
                onError("ERROR DE CONEXÃO: ${error.message}\nResposta do servidor: $errorResponse")
            }
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun buscarResultados(
        URL: String,
        onSuccess: (List<ResultadoExame>) -> Unit,
        onError: (String) -> Unit
    ) {
        val stringRequest = StringRequest(
            Request.Method.GET, URL,
            { response ->
                try {
                    val document: Document = Jsoup.parse(response)
                    val resultados = mutableListOf<ResultadoExame>()

                    val elementos: Elements = document.select("ul.linha-resultados-exames")

                    for (elemento in elementos) {
                        val data = elemento.select("li:eq(0) p").text()
                        val tipoExame = elemento.select("li:eq(1) p").text()
                        val linkBaixar = elemento.select("li:eq(2) a").attr("href")

                        resultados.add(ResultadoExame(data, tipoExame, linkBaixar))
                    }

                    onSuccess(resultados)
                } catch (e: Exception) {
                    onError("Erro ao processar os resultados: ${e.message}\nResposta HTML: $response")
                }
            },
            { error: VolleyError ->
                // Lidar com erros de conexão
                val errorResponse = error.networkResponse?.data?.let {
                    String(it, Charsets.UTF_8)
                } ?: "Resposta do servidor não disponível"
                onError("ERROR DE CONEXÃO: ${error.message}\nResposta do servidor: $errorResponse")
            }
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(onLoginSuccess = { user_login, user_pass ->
                    val url = "https://sosneuro.com.br/index.php/entrega-de-exames?user_login=$user_login&user_pass=$user_pass"

                    buscarUsuario(url,
                        onSuccess = { usuarioValido, userData ->
                            if (usuarioValido) {
                                val resultadoUrl = "https://sosneuro.com.br/index.php/entrega-de-exames"
                                buscarResultados(resultadoUrl,
                                    onSuccess = {
                                        navController.navigate("user_data?userData=$userData")
                                    },
                                    onError = { error ->
                                        Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                Toast.makeText(applicationContext, "Usuário inválido", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onError = {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                })
            }
            composable("user_data?userData={userData}") { backStackEntry ->
                val userData = backStackEntry.arguments?.getString("userData")
                var resultados by remember { mutableStateOf(emptyList<ResultadoExame>()) }
                var loading by remember { mutableStateOf(true) }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(userData) {
                    val url = "https://sosneuro.com.br/index.php/entrega-de-exames"
                    buscarResultados(url,
                        onSuccess = { fetchedResultados ->
                            resultados = fetchedResultados
                            loading = false
                        },
                        onError = { error ->
                            errorMessage = error
                            loading = false
                        }
                    )
                }

                if (loading) {
                    if (errorMessage != null) {
                        Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        Text("Carregando...")
                    }
                } else {
                    UserDataScreen(userData = userData ?: "", resultados = resultados, onLogout = {
                        navController.popBackStack()
                    })
                }
            }
        }
    }
}
