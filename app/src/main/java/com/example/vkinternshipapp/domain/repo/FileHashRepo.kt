package com.example.vkinternshipapp.domain.repo

import com.example.vkinternshipapp.domain.models.FileHash

interface FileHashRepo {
    suspend fun get(paths: List<String>): List<FileHash>
    fun getUpdated(): List<String>
    suspend fun insertAll(fileHashes: HashMap<String, String>)
}