package com.example.sosneuromobile.ui.theme

data class UserData(
    val displayName: String,
    val birthDate: String,
    val age: String,
    val email: String,
    val phone: String,
    val examResults: List<ExamResult> = emptyList()
)
