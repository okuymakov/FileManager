package com.example.vkinternshipapp.ui

import com.example.vkinternshipapp.domain.filemanager.SortType

sealed class MainAction {
    data class MoveToDirectory(val path: String) : MainAction()
    data class MoveBack(val path: String? = null) : MainAction()
    object ChangeSortDirection : MainAction()
    data class SortBy(val sortType: SortType) : MainAction()
    object FetchFiles : MainAction()
    object FetchUpdated : MainAction()
    object GrantPermissions : MainAction()
}