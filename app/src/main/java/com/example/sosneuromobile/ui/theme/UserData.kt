package com.example.sosneuromobile.ui.theme

data class UserData(
    val displayName: String,
    val dataNasc: String,
    val idade: String,
    val email: String,
    val telefone: String,
    val exames: List<ExameData> = emptyList()
)
