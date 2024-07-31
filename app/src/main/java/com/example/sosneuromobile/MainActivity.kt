package com.example.sosneuromobile

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.sosneuromobile.ui.theme.SosNeuroMobileTheme
import com.example.sosneuromobile.ui.theme.LoginScreen
import com.example.sosneuromobile.ui.theme.UserDataScreen
import org.json.JSONObject

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
    fun buscarUsuario(URL: String, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, URL, null,
            { response ->
                if (response.has("success") && response.getBoolean("success")) {
                    val data = response.optJSONObject("data")
                    data?.let {
                        onSuccess(it)
                    } ?: onError("Dados do usuário não encontrados")
                } else {
                    onError(response.optString("message", "Usuário ou senha incorretos"))
                }
            },
            { error: VolleyError ->
                onError("ERROR DE CONEXÃO: ${error.message}")
            }
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(onLoginSuccess = { user_login, user_pass ->
                    val url = "http://192.168.18.1/buscar_usuario.php?user_login=$user_login&user_pass=$user_pass"
                    buscarUsuario(url,
                        onSuccess = { jsonObject ->
                            val userData = jsonObject.toString()
                            navController.navigate("user_data?userData=$userData")
                        },
                        onError = {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        })
                })
            }
            composable("user_data?userData={userData}") { backStackEntry ->
                val userData = backStackEntry.arguments?.getString("userData")
                userData?.let {
                    UserDataScreen(userData = it, onLogout = {
                        navController.popBackStack()
                    })
                }
            }
        }
    }
}
