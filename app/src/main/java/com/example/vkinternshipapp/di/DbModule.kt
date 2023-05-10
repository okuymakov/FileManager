package com.example.vkinternshipapp.di

import android.content.Context
import androidx.room.Room
import com.example.vkinternshipapp.data.AppDatabase
import com.example.vkinternshipapp.data.FileHashDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Provides
    fun provideDb(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "FileManager"
        ).build()
    }

    @Provides
    fun provideDao(db: AppDatabase): FileHashDao {
        return db.fileHashDao()
    }
}