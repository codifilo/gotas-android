package com.codifilia.gotas.precipitation

import java.net.URL

class Service {
    private val host = "gotas.codifilia.com"
    private val port = 3000
    private val urlFormat = "http://$host:$port/precip?lat=%f&lon=%f"

    fun retrieve(lat: Double, lon: Double): List<Observation> {
        val url = URL(urlFormat.format(lat, lon))
        val response = url.readText()
        return Parser.parse(response)
    }
}