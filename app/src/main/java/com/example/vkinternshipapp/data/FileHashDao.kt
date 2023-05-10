package com.example.vkinternshipapp.data

import androidx.room.*
import com.example.vkinternshipapp.domain.models.FileHash


@Dao
interface FileHashDao {

    @Query("SELECT * FROM FileHash WHERE path IN (:paths)")
    suspend fun get(paths: List<String>): List<FileHash>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg fileHashes: FileHash)

}