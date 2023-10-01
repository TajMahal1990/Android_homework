package ru.netology.nmedia.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.math.floor

fun clicksCount(count: Int): String = when (count) {
    in 0..999 -> count.toString()
    in 1000..1099 -> "1K"
    in 1100..9999 -> (floor((count / 100).toDouble()) / 10).toString() + "K"
    in 10_000..999_999 -> (count / 1000).toString() + "K"
    else -> (floor((count / 100000).toDouble()) / 10).toString() + "M"
}

object AndroidUtils {
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}