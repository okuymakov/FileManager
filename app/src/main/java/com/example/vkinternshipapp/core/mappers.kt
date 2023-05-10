package com.example.vkinternshipapp.core

import com.example.vkinternshipapp.domain.models.FileModel
import java.io.File
import java.util.*

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