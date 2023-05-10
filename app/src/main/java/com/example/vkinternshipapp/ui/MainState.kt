package com.example.vkinternshipapp.ui

import com.example.vkinternshipapp.filemanager.SortType
import com.example.vkinternshipapp.models.FileModel

data class MainState(
    val files: List<FileModel> = emptyList(),
    val isLoading: Boolean = false,
    val paths: List<String> = emptyList(),
    val isRoot: Boolean = true,
    val sortType: SortType = SortType.BY_NAME,
    val isDescending: Boolean = false
)