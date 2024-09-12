package com.example.sosneuromobile

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.sosneuromobile.ui.theme.SosNeuroMobileTheme
import com.example.sosneuromobile.ui.theme.LoginScreen
import com.example.sosneuromobile.ui.theme.ResultadoExame
import com.example.sosneuromobile.ui.theme.UserData
import com.example.sosneuromobile.ui.theme.UserDataScreen
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

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

    fun getWpnonce(
        context: Context,
        url: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val doc: Document = Jsoup.parse(response)
                val nonceElement = doc.selectFirst("input[name=_wpnonce]")

                if (nonceElement != null) {
                    val wpnonce = nonceElement.attr("value")
                    onSuccess(wpnonce)
                } else {
                    onError("Nonce não encontrado na página.")
                }
            },
            { error ->
                onError("Erro na conexão: ${error.localizedMessage}")
            }
        )

        queue.add(stringRequest)
    }

    fun buscarUsuario(
        context: Context,
        url: String,
        login: String,
        senha: String,
        loginPaciente: String,
        onSuccess: (Boolean, UserData) -> Unit,
        onError: (String) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    val doc: Document = Jsoup.parse(response)

                    val flashMessage = doc.select(".alert.alert-info").text()
                    if (flashMessage.contains("Usuário ou senha em branco!") ||
                        flashMessage.contains("Usuário ou senha inválidos")
                    ) {
                        onError(flashMessage)
                        return@Listener
                    }

                    val infoPacienteDiv = doc.select(".info-paciente").first()
                    if (infoPacienteDiv == null) {
                        onError("Credenciais inválidas ou problema na autenticação.")
                        return@Listener
                    }

                    val infoPacienteHtml =
                        infoPacienteDiv.html().replace("</p>", "").replace("<p>", "")
                    val lines = infoPacienteHtml.split("<br>").map { it.trim() }

                    val displayName =
                        lines.getOrElse(0) { "Nome não disponível" }.substringBefore(",").trim()
                    val dataNasc =
                        lines.getOrElse(1) { "Data não disponível" }.substringBefore(",").trim()
                    val idade =
                        lines.getOrElse(1) { "Idade não disponível" }.substringAfter(",").trim()
                    val email =
                        lines.getOrElse(2) { "Email não disponível" }.substringBefore(",").trim()
                    val telefone =
                        lines.getOrElse(3) { "Telefone não disponível" }.substringBefore(",").trim()

                    val userData = UserData(
                        displayName = displayName,
                        dataNasc = dataNasc,
                        email = email,
                        telefone = telefone,
                        idade = idade
                    )

                    onSuccess(true, userData)
                } catch (e: Exception) {
                    onError("Erro ao processar a resposta: ${e.localizedMessage}")
                }
            },
            Response.ErrorListener { error ->
                val errorResponse = error.networkResponse?.data?.let {
                    String(it, Charsets.UTF_8)
                } ?: "Resposta do servidor não disponível"
                onError("Erro na conexão: ${error.localizedMessage}\nResposta do servidor: $errorResponse")
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "login" to login,
                    "senha" to senha,
                    "_wpnonce" to loginPaciente
                )
            }
        }

        queue.add(stringRequest)
    }

    fun parseResultadoExame(elemento: Element): ResultadoExame {
        val dataRealizacao = elemento.select("li:eq(0) p").text().trim()
        val tipoExame = elemento.select("li:eq(1) p").text().trim()
        val linkBaixar = elemento.select("li:eq(2) a").attr("href").trim()
        return ResultadoExame(dataRealizacao, tipoExame, linkBaixar)
    }

    fun buscarResultados(
        context: Context,
        url: String,
        login: String,
        senha: String,
        loginPaciente: String,
        onSuccess: (List<ResultadoExame>) -> Unit,
        onError: (String) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    val doc: Document = Jsoup.parse(response)

                    val flashMessage = doc.select(".alert.alert-info").text()
                    if (flashMessage.contains("Usuário ou senha em branco!") ||
                        flashMessage.contains("Usuário ou senha inválidos")
                    ) {
                        onError(flashMessage)
                        return@Listener
                    }

                    val resultados = doc.select("ul.linha-resultados-exames").map { elemento ->
                        parseResultadoExame(elemento)
                    }

                    if (resultados.isEmpty()) {
                        onError("Nenhum exame encontrado para o usuário.")
                    } else {
                        onSuccess(resultados)
                    }
                } catch (e: Exception) {
                    onError("Erro ao processar a resposta: ${e.localizedMessage}")
                }
            },
            Response.ErrorListener { error ->
                val errorResponse = error.networkResponse?.data?.let {
                    String(it, Charsets.UTF_8)
                } ?: "Resposta do servidor não disponível"
                onError("Erro na conexão: ${error.localizedMessage}\nResposta do servidor: $errorResponse")
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "login" to login,
                    "senha" to senha,
                    "_wpnonce" to loginPaciente
                )
            }
        }

        queue.add(stringRequest)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen { user_login, user_pass, userData, resultados ->
                    val userDataJson = Uri.encode(Gson().toJson(userData))
                    navController.navigate("user_data?userData=$userDataJson&login=$user_login&senha=$user_pass&resultados=${Uri.encode(Gson().toJson(resultados))}")
                }
            }
            composable("user_data?userData={userData}&login={login}&senha={senha}&resultados={resultados}") { backStackEntry ->
                val userDataJson = backStackEntry.arguments?.getString("userData")
                val resultadosJson = backStackEntry.arguments?.getString("resultados")

                val userData: UserData? = try {
                    Gson().fromJson(userDataJson, UserData::class.java)
                } catch (e: JsonSyntaxException) {
                    null
                }

                val resultados: List<ResultadoExame> = try {
                    Gson().fromJson(resultadosJson, Array<ResultadoExame>::class.java).toList()
                } catch (e: JsonSyntaxException) {
                    emptyList()
                }

                if (userData != null) {
                    UserDataScreen(
                        userData = userData,
                        resultados = resultados,
                        onLogout = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
