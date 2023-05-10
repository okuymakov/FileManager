package com.example.vkinternshipapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.example.vkinternshipapp.core.Constants
import com.example.vkinternshipapp.workers.SaveFileHashesWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        val request = OneTimeWorkRequestBuilder<SaveFileHashesWorker>().setInputData(
            Data.Builder().putString("path", Constants.ROOT_PATH).build()
        ).build()
        WorkManager.initialize(this, workManagerConfiguration)
        WorkManager.getInstance(this).enqueue(request)
    }
}