package com.codifilia.gotas.precipitation

import com.codifilia.gotas.util.catchAll
import java.net.URL
import java.util.*

class Service {
    private val host = "gotas.codifilia2.com"
    private val port = 3000
    private val urlFormat = "http://$host:$port/precip?lat=%f&lon=%f"

    fun retrieve(lat: Double, lon: Double): List<Observation> {
        val url = URL(urlFormat.format(Locale.US, lat, lon))
        return catchAll { Parser.parse(url.readText()) } ?: listOf()
    }
}