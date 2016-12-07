package com.codifilia.gotas.util

fun <T> catchAll(f: () -> T): T? {
    var result: T? = null
    try {
        result = f()
    }
    catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}