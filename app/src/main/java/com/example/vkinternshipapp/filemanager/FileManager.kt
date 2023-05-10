package com.example.vkinternshipapp.filemanager

import com.example.vkinternshipapp.core.sort
import com.example.vkinternshipapp.models.FileModel
import com.example.vkinternshipapp.core.Result
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class FileManager(
    private val root: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
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
            val data = files.map { file ->
                async {
                    FileModel(
                        name = if (file.isDirectory) file.name else file.nameWithoutExtension,
                        size = file.length(),
                        createdAt = Date(file.lastModified()),
                        isDirectory = file.isDirectory,
                        type = if (file.isDirectory) "" else file.extension,
                        path = file.path,
                        itemsCount = file.listFiles()?.size ?: 0
                    )
                }
            }.awaitAll().sort(sortType, isDescending)
            Result.Success(data)
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }
}


