package com.codifilia.gotas.util

import android.util.Log

fun <T> catchAll(f: () -> T): T? {
    var result: T? = null
    try {
        result = f()
    }
    catch (e: Exception) {
        Log.e("catchAll", e.message)
    }
    return result
}