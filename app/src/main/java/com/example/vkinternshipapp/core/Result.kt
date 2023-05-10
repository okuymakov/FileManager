package com.example.vkinternshipapp.core

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Failure(val cause: Exception) : Result<Nothing>()
}