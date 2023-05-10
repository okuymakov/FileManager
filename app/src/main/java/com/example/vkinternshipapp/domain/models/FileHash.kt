package com.example.vkinternshipapp.domain.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["path"], unique = true)])
data class FileHash(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val path: String,
    val hash: String
)