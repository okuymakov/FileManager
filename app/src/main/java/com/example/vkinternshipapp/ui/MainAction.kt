package com.example.vkinternshipapp.ui

sealed class MainAction {
    data class MoveToDirectory(val path: String): MainAction()
    object MoveBack: MainAction()
}