package com.example.sosneuromobile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.sosneuromobile.ui.WelcomeScreen
import com.example.sosneuromobile.ui.theme.SosNeuroMobileTheme
import com.example.sosneuromobile.ui.theme.LoginScreen
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SosNeuroMobileTheme {
                AppNavigation()
            }
        }
    }

    fun buscarUsuario(URL: String) {
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, URL, null,
            { response ->
                var jsonObject: JSONObject?
                for (i in 0 until response.length()) {
                    try {
                        jsonObject = response.getJSONObject(i)
                        val userName = jsonObject.getString("name") // Substitua "name" pelo campo correto do JSON
                        Toast.makeText(applicationContext, "User: $userName", Toast.LENGTH_SHORT).show()
                    } catch (e: JSONException) {
                        Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            { error: VolleyError ->
                Toast.makeText(applicationContext, "ERROR DE CONEXÃO: $error", Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonArrayRequest)
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
}
