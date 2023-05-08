package com.example.vkinternshipapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vkinternshipapp.R
import com.example.vkinternshipapp.core.Constants
import com.example.vkinternshipapp.core.DynamicString
import com.example.vkinternshipapp.core.StringResource
import com.example.vkinternshipapp.filemanager.FileManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    private val fileManager: FileManager = FileManager(Constants.ROOT_PATH)
) : ViewModel() {

    private val _state = MutableStateFlow(MainState(isLoading = true))
    val state get() = _state.asStateFlow()

    private val _events: Channel<MainEvent> = Channel()
    val events = _events.receiveAsFlow()

    init {
        fetchFiles()
    }

    private fun moveToDirectory(dir: String) {
        fileManager.moveToDirectory(dir)
        fetchFiles()
    }

    private fun moveBack() {
        if (fileManager.moveBack()) {
            viewModelScope.launch {
                fetchFiles()
            }
        } else {
            viewModelScope.launch {
                _events.send(MainEvent.CloseApp)
            }
        }
    }

    private fun fetchFiles() {
        viewModelScope.launch {
            val dirs = fileManager.currentPath
                ?.replace(Constants.ROOT_PATH, "")?.split(File.separator)
                ?.filterNot { it.isBlank() }?.map { DynamicString(it) } ?: emptyList()
            _state.emit(
                _state.value.copy(
                    files = fileManager.fetchFiles(),
                    isRoot = fileManager.isRoot,
                    directories = listOf(StringResource(R.string.root_dir_name)) + dirs
                )
            )
        }
    }

    fun onAction(action: MainAction) {
        when (action) {
            MainAction.MoveBack -> moveBack()
            is MainAction.MoveToDirectory -> moveToDirectory(action.path)
        }
    }
}