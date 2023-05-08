package com.example.vkinternshipapp.core

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.vkinternshipapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

fun String.toIconRes() = when (substring(lastIndexOf('.') + 1)) {
    "jpg" -> R.drawable.ic_file_jpg
    "mp3" -> R.drawable.ic_file_mp3
    "mp4" -> R.drawable.ic_file_mp4
    "txt" -> R.drawable.ic_file_txt
    "pdf" -> R.drawable.ic_file_pdf
    "docx" -> R.drawable.ic_file_docx
    else -> R.drawable.ic_file_txt
}

fun Date.format(pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun Long.formatFileSize(context: Context): String {
    var size = this.toDouble()
    return if (size < 1024) {
        context.resources.getString(R.string.file_size_b, size)
    } else {
        size /= 1024
        if (size < 1024) {
            context.resources.getString(R.string.file_size_kb, size)
        } else {
            size /= 1024
            if (size < 1024) {
                context.resources.getString(R.string.file_size_mb, size)
            } else {
                context.resources.getString(R.string.file_size_gb, size / 1024)
            }
        }
    }
}

fun AppCompatActivity.launchOnLifecycle(state: Lifecycle.State, block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) {
            block()
        }
    }
}