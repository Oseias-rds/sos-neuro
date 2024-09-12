package com.example.sosneuromobile.ui.theme

data class UserData(
    val displayName: String,   // Nome de exibição do usuário
    val birthDate: String,     // Data de nascimento
    val age: String,           // Idade
    val email: String,         // Email
    val phone: String,         // Telefone
    val examResults: List<ExamResult> = emptyList()  // Lista de resultados de exames
)
