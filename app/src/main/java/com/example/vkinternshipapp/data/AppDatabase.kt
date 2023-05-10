package com.example.vkinternshipapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vkinternshipapp.domain.models.FileHash

@Database(entities = [FileHash::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fileHashDao(): FileHashDao
}