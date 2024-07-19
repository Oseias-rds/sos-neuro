package com.example.sosneuromobile.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sosneuromobile.ui.model.FileItem

@Composable
fun FileExplorerScreen(fileItems: List<FileItem>, onFileClick: (FileItem) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        fileItems.forEach { fileItem ->
            Text(
                text = if (fileItem.isFolder) "📁 ${fileItem.name}" else "📄 ${fileItem.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFileClick(fileItem) }
                    .padding(8.dp)
            )
        }
    }
}
