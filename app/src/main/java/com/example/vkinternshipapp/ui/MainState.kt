package com.example.vkinternshipapp.ui

import com.example.vkinternshipapp.domain.filemanager.SortType
import com.example.vkinternshipapp.domain.models.FileModel

data class MainState(
    val files: List<FileModel> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val permissionsNotGranted: Boolean = true,
    val paths: List<String> = emptyList(),
    val isRoot: Boolean = true,
    val sortType: SortType = SortType.BY_NAME,
    val isDescending: Boolean = false
)