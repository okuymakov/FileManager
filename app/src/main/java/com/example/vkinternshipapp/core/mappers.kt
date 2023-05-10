package com.example.vkinternshipapp.core

import com.example.vkinternshipapp.R
import com.example.vkinternshipapp.domain.models.FileModel
import java.io.File
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


fun File.toFileModel(): FileModel {
    return FileModel(
        name = if (isDirectory) name else nameWithoutExtension,
        size = length(),
        createdAt = Date(lastModified()),
        isDirectory = isDirectory,
        type = if (isDirectory) "" else extension,
        path = path,
        itemsCount = listFiles()?.size ?: 0
    )
}