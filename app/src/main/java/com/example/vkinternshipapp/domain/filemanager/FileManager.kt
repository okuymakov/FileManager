package com.example.vkinternshipapp.domain.filemanager

import com.example.vkinternshipapp.core.Result
import com.example.vkinternshipapp.core.sort
import com.example.vkinternshipapp.core.toFileModel
import com.example.vkinternshipapp.domain.models.FileModel
import com.example.vkinternshipapp.domain.repo.FileHashRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class FileManager @AssistedInject constructor(
    @Assisted private val root: String,
    private val repo: FileHashRepo,
    @Assisted private val dispatcher: CoroutineDispatcher
) {
    private val stack =
        ArrayDeque<String>().apply { push(root) }

    val isRoot: Boolean get() = stack.size == 1
    val paths: List<String> get() = stack.toList()

    fun moveToDirectory(path: String) {
        stack.push(path)
    }

    fun moveBack(): Boolean {
        return if (stack.size == 1) {
            false
        } else {
            stack.pop()
            true
        }
    }

    fun moveBack(path: String): Boolean {
        if (!stack.contains(path)) return false
        while (stack.peek() != path) {
            stack.pop()
        }
        return true
    }

    suspend fun fetchFiles(
        sortType: SortType = SortType.BY_NAME,
        isDescending: Boolean = false,
        showHiddenFiles: Boolean = false
    ): Result<List<FileModel>> = withContext(dispatcher) {
        try {
            val path = stack.peek() ?: throw IllegalStateException("Stack is empty")
            val files =
                File(path).listFiles { file -> file.isHidden == showHiddenFiles } ?: emptyArray()
            val data = files.map { file -> async { file.toFileModel() } }.awaitAll()
                .sort(sortType, isDescending)
            Result.Success(data)
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    suspend fun fetchUpdated(
        sortType: SortType = SortType.BY_NAME,
        isDescending: Boolean = false,
    ): Result<List<FileModel>> = withContext(dispatcher) {
        try {
            val files = repo.getUpdated().map { File(it) }
            val data = files.map { file -> async { file.toFileModel() } }.awaitAll()
                .sort(sortType, isDescending)
            Result.Success(data)
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            root: String,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): FileManager
    }
}


