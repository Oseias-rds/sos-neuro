package com.example.sosneuromobile.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONException

@Composable
fun UserDataScreen(userData: String) {
    val jsonArray = remember {
        try {
            JSONArray(userData)
        } catch (e: JSONException) {
            JSONArray()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Dados do Usuário", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (jsonArray.length() > 0) {
            val user = jsonArray.getJSONObject(0)

            Text(text = "ID: ${user.optString("ID")}")
            Text(text = "Login: ${user.optString("user_login")}")
            Text(text = "Email: ${user.optString("user_email")}")
            Text(text = "Nome: ${user.optString("display_name")}")
        } else {
            Text(text = "Nenhum dado encontrado.")
        }
    }
}
