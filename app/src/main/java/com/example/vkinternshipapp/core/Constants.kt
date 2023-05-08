package com.example.vkinternshipapp.core

import android.os.Environment
import com.example.vkinternshipapp.BuildConfig

class Constants {
    companion object {
        val ROOT_PATH: String = Environment.getExternalStorageDirectory().path
        const val SETTINGS_URI = "package:${BuildConfig.APPLICATION_ID}"
    }
}