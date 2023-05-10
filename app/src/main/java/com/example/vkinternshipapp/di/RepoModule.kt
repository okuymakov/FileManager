package com.example.vkinternshipapp.di

import com.example.vkinternshipapp.data.FileHashRepoImpl
import com.example.vkinternshipapp.domain.repo.FileHashRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepoModule {
    @Binds
    fun provideFileHashRepo(repo: FileHashRepoImpl): FileHashRepo
}