package com.example.vkinternshipapp.filemanager

import com.example.vkinternshipapp.models.FileModel
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class FileManager(
    private val root: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val stack =
        ArrayDeque<String>().apply { push(root) }

    val currentPath: String? get() = stack.peek()
    val isRoot: Boolean get() = stack.size == 1

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

    suspend fun fetchFiles(): List<FileModel> = withContext(dispatcher) {
        val dirPath = stack.peek()
        dirPath?.let { File(it) }?.listFiles()?.map { file ->
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
        }?.awaitAll()?.sortedWith(compareBy({ !it.isDirectory }, { it.name })) ?: emptyList()
    }
}