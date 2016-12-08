package com.codifilia.gotas.precipitation

import com.codifilia.gotas.util.catchAll
import java.net.URL
import java.util.*

class Service {
    private val host = "gotas.codifilia.com"
    private val port = 3000
    private val urlFormat = "http://$host:$port/precip?lat=%f&lon=%f"

    fun retrieve2(lat: Double, lon: Double): List<Observation> {
        val url = URL(urlFormat.format(Locale.US, lat, lon))
        return catchAll { Parser.parse(url.readText()) } ?: listOf()
    }

    val testString = "[{\"time\":\"2016-12-05T16:15:00Z\",\"value\":{\"tag\":\"Snow\",\"contents\":3.3}},{\"time\":\"2016-12-05T16:30:00Z\",\"value\":{\"tag\":\"Mixed\",\"contents\":49}},{\"time\":\"2016-12-05T16:45:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":25}},{\"time\":\"2016-12-05T17:00:00Z\",\"value\":{\"tag\":\"Snow\",\"contents\":3.3}},{\"time\":\"2016-12-05T17:15:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":2.4}},{\"time\":\"2016-12-05T17:30:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":1.5}},{\"time\":\"2016-12-05T17:45:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":0.5}},{\"time\":\"2016-12-05T18:00:00Z\",\"value\":{\"tag\":\"None\",\"contents\":[]}},{\"time\":\"2016-12-05T18:15:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":55}},{\"time\":\"2016-12-05T18:30:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":25}},{\"time\":\"2016-12-05T18:45:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":0.1}},{\"time\":\"2016-12-05T19:00:00Z\",\"value\":{\"tag\":\"Snow\",\"contents\":3.3}},{\"time\":\"2016-12-05T19:15:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":0.5}},{\"time\":\"2016-12-05T19:30:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":0.3}},{\"time\":\"2016-12-05T19:45:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":25}},{\"time\":\"2016-12-05T20:00:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":25}},{\"time\":\"2016-12-05T20:15:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":55}},{\"time\":\"2016-12-05T20:30:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":55}},{\"time\":\"2016-12-05T20:45:00Z\",\"value\":{\"tag\":\"Snow\",\"contents\":2}},{\"time\":\"2016-12-05T21:00:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":55}},{\"time\":\"2016-12-05T21:15:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":25}},{\"time\":\"2016-12-05T21:30:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":55}},{\"time\":\"2016-12-05T21:45:00Z\",\"value\":{\"tag\":\"Rain\",\"contents\":55}}]"

    fun retrieve(lat: Double, lon: Double): List<Observation> {
        return catchAll { Parser.parse(testString) } ?: listOf()
    }
}