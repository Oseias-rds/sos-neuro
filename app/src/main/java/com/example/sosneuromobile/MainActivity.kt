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
import com.example.sosneuromobile.ui.theme.ExamResult
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

    fun getWpNonce(
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

    fun fetchUser(
        context: Context,
        url: String,
        login: String,
        password: String,
        userNonce: String,
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

                    val userInfoDiv = doc.select(".info-paciente").first()
                    if (userInfoDiv == null) {
                        onError("Credenciais inválidas ou problema na autenticação.")
                        return@Listener
                    }

                    val userInfoHtml =
                        userInfoDiv.html().replace("</p>", "").replace("<p>", "")
                    val lines = userInfoHtml.split("<br>").map { it.trim() }

                    val displayName =
                        lines.getOrElse(0) { "Nome não disponível" }.substringBefore(",").trim()
                    val birthDate =
                        lines.getOrElse(1) { "Data não disponível" }.substringBefore(",").trim()
                    val age =
                        lines.getOrElse(1) { "Idade não disponível" }.substringAfter(",").trim()
                    val email =
                        lines.getOrElse(2) { "Email não disponível" }.substringBefore(",").trim()
                    val phone =
                        lines.getOrElse(3) { "Telefone não disponível" }.substringBefore(",").trim()

                    val userData = UserData(
                        displayName = displayName,
                        birthDate = birthDate,
                        email = email,
                        phone = phone,
                        age = age
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
                    "senha" to password,
                    "_wpnonce" to userNonce
                )
            }
        }

        queue.add(stringRequest)
    }

    fun parseExamResult(element: Element): ExamResult {
        val examDate = element.select("li:eq(0) p").text().trim()
        val examType = element.select("li:eq(1) p").text().trim()
        val downloadLink = element.select("li:eq(2) a").attr("href").trim()
        return ExamResult(examDate, examType, downloadLink)
    }

    fun fetchExamResults(
        context: Context,
        url: String,
        login: String,
        password: String,
        userNonce: String,
        onSuccess: (List<ExamResult>) -> Unit,
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

                    val results = doc.select("ul.linha-resultados-exames").map { element ->
                        parseExamResult(element)
                    }

                    if (results.isEmpty()) {
                        onError("Nenhum exame encontrado para o usuário.")
                    } else {
                        onSuccess(results)
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
                    "senha" to password,
                    "_wpnonce" to userNonce
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
                LoginScreen { userLogin, userPassword, userData, results ->
                    val userDataJson = Uri.encode(Gson().toJson(userData))
                    navController.navigate("user_data?userData=$userDataJson&login=$userLogin&senha=$userPassword&resultados=${Uri.encode(Gson().toJson(results))}")
                }
            }
            composable("user_data?userData={userData}&login={login}&senha={senha}&resultados={resultados}") { backStackEntry ->
                val userDataJson = backStackEntry.arguments?.getString("userData")
                val resultsJson = backStackEntry.arguments?.getString("resultados")

                val userData: UserData? = try {
                    Gson().fromJson(userDataJson, UserData::class.java)
                } catch (e: JsonSyntaxException) {
                    null
                }

                val results: List<ExamResult> = try {
                    Gson().fromJson(resultsJson, Array<ExamResult>::class.java).toList()
                } catch (e: JsonSyntaxException) {
                    emptyList()
                }

                if (userData != null) {
                    UserDataScreen(
                        userData = userData,
                        results = results,
                        onLogout = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}