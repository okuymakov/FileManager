package com.example.vkinternshipapp.ui

import com.example.vkinternshipapp.filemanager.SortType

sealed class MainAction {
    data class MoveToDirectory(val path: String): MainAction()
    object MoveBack: MainAction()
    object ChangeSortDirection: MainAction()
    data class SortBy(val sortType: SortType) : MainAction()
}