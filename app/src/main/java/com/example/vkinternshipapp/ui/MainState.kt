package com.example.vkinternshipapp.ui

import com.example.vkinternshipapp.R
import com.example.vkinternshipapp.core.StringResource
import com.example.vkinternshipapp.core.UiText
import com.example.vkinternshipapp.models.FileModel

data class MainState(
    val files: List<FileModel> = emptyList(),
    val isLoading: Boolean = false,
    val directories: List<UiText> = listOf(StringResource(R.string.root_dir_name)),
    val isRoot: Boolean = true
)