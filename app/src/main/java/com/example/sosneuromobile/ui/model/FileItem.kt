package com.example.sosneuromobile.ui.model

data class FileItem(
    val name: String,
    val isFolder: Boolean,
    val children: List<FileItem>? = null
)
