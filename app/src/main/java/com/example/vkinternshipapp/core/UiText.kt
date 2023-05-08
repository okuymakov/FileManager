package com.example.vkinternshipapp.core

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText

data class DynamicString(val value: String) : UiText()

class StringResource(@StringRes val resId: Int, vararg val args: Any) : UiText()

fun UiText.toCharSequence(context: Context): CharSequence {
    return when (this) {
        is DynamicString -> value
        is StringResource -> context.resources.getString(resId, args)
    }
}
