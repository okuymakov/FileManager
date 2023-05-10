package com.example.vkinternshipapp.core

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.vkinternshipapp.R
import com.example.vkinternshipapp.filemanager.SortType
import com.example.vkinternshipapp.models.FileModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
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

fun AppCompatActivity.launchOnLifecycle(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) {
            block()
        }
    }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.showPopup(@MenuRes menuRes: Int, onClick: ((MenuItem) -> Boolean)? = null): PopupMenu {
    return PopupMenu(context, this).apply {
        setOnMenuItemClickListener(onClick)
        menuInflater.inflate(menuRes, menu)
        show()
    }
}

fun List<FileModel>.sort(
    sortType: SortType = SortType.BY_SIZE,
    isDescending: Boolean = false
): List<FileModel> {
    val comparable: (FileModel) -> Comparable<*> = {
        when (sortType) {
            SortType.BY_NAME -> it.name
            SortType.BY_SIZE -> it.size
            SortType.BY_DATE -> it.createdAt
            SortType.BY_TYPE -> it.type
        }
    }
    return sortedWith(compareBy<FileModel> { !it.isDirectory }.run {
        if (isDescending) thenByDescending(comparable) else thenBy(comparable)
    })
}

fun String.directoryName(context: Context): CharSequence {
    return if (this == Constants.ROOT_PATH) context.getString(R.string.root_dir_name)
    else substringAfterLast(File.separator)
}