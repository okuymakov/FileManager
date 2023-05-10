package com.example.vkinternshipapp.domain.models

import java.util.*

data class FileModel(
    val name: String,
    val type: String,
    val size: Long,
    val createdAt: Date,
    val path: String,
    val itemsCount: Int,
    val isDirectory: Boolean = false,
    val isRoot: Boolean = false
)