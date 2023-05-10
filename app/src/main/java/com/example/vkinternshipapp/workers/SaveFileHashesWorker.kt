package com.example.vkinternshipapp.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.vkinternshipapp.core.getHash
import com.example.vkinternshipapp.domain.repo.FileHashRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.util.*

@HiltWorker
class SaveFileHashesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repo: FileHashRepo
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val path = inputData.getString("path") ?: return Result.failure()
            save(path) {
                repo.insertAll(it)
            }
            Result.success()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Result.failure()
        }
    }

    private suspend fun save(path: String, insert: suspend (HashMap<String, String>) -> Unit) {
        val hashes = hashMapOf<String, String>()
        val stack = ArrayDeque<File>()
        stack.push(File(path))
        while (!stack.isEmpty()) {
            val file = stack.pop()
            if (file.isDirectory) {
                file.listFiles()?.forEach { stack.push(it) }
            } else {
                hashes += file.path to file.getHash()
                if (hashes.size % 20 == 0) {
                    insert(hashes)
                    hashes.clear()
                }
            }
        }
    }
}

