package com.example.vkinternshipapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vkinternshipapp.core.Constants
import com.example.vkinternshipapp.core.Result
import com.example.vkinternshipapp.filemanager.FileManager
import com.example.vkinternshipapp.filemanager.SortType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val fileManager: FileManager = FileManager(Constants.ROOT_PATH)
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state get() = _state.asStateFlow()

    private val _events: Channel<MainEvent> = Channel()
    val events = _events.receiveAsFlow()

    private fun moveToDirectory(dir: String) {
        viewModelScope.launch {
            fileManager.moveToDirectory(dir)
            fetchFiles()
        }
    }

    private fun moveBack(path: String?) {
        viewModelScope.launch {
            if (path?.let { fileManager.moveBack(it) } ?: fileManager.moveBack()) {
                fetchFiles()
            } else {
                _events.send(MainEvent.CloseApp)

            }
        }
    }

    private fun fetchFiles(
        sortType: SortType = _state.value.sortType,
        isDescending: Boolean = _state.value.isDescending
    ) {
        viewModelScope.launch {
            _state.emit(_state.value.copy(isLoading = true, permissionsNotGranted = false))
            _state.emit(
                when (val res = fileManager.fetchFiles(sortType, isDescending)) {
                    is Result.Success -> _state.value.copy(
                        files = res.data,
                        isRoot = fileManager.isRoot,
                        paths = fileManager.paths,
                        isDescending = isDescending,
                        sortType = sortType,
                        isError = false,
                        isLoading = false
                    )
                    is Result.Failure -> _state.value.copy(isError = true, isLoading = false)
                }
            )
        }
    }

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.MoveBack -> moveBack(action.path)
            is MainAction.MoveToDirectory -> moveToDirectory(action.path)
            MainAction.ChangeSortDirection -> fetchFiles(isDescending = !_state.value.isDescending)
            is MainAction.SortBy -> fetchFiles(action.sortType)
            MainAction.FetchFiles -> fetchFiles()
        }
    }
}