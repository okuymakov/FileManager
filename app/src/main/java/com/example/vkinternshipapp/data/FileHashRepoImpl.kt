package com.example.vkinternshipapp.data

import android.util.Log
import com.example.vkinternshipapp.domain.models.FileHash
import com.example.vkinternshipapp.domain.repo.FileHashRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileHashRepoImpl @Inject constructor(private val db: FileHashDao) : FileHashRepo {
    private val updated = mutableListOf<String>()

    override suspend fun get(paths: List<String>): List<FileHash> {
        return db.get(paths)
    }

    override fun getUpdated(): List<String> {
        return updated.toMutableList()
    }

    override suspend fun insertAll(fileHashes: HashMap<String, String>) {
        val oldValues = get(fileHashes.keys.toList())
        val updatedValues = oldValues.filter { old -> old.hash != fileHashes[old.path] }
        if (updatedValues.size > 0) {
            Log.d("Updated values", "-------------${updatedValues}")
        }
        updated += updatedValues.map { it.path }
        db.insertAll(*fileHashes.map { FileHash(path = it.key, hash = it.value) }.toTypedArray())
    }
}

