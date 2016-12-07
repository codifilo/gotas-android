package com.codifilia.gotas.precipitation

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.text.SimpleDateFormat
import java.util.*


object Parser {
    private val observationTimeKey = "time"
    private val timeFormat = "yyy-MM-dd'T'HH:mm:ss'Z'"
    private val timeZone = "UTC"
    private val observationValueKey = "value"
    private val precipTypeKey = "tag"
    private val precipAmountKey = "contents"
    private val precipRainValue = "Rain"
    private val precipSnowValue = "Snow"
    private val precipMixedValue = "Mixed"

    private val timeParser: SimpleDateFormat = {
        val p = SimpleDateFormat(timeFormat)
        p.timeZone = TimeZone.getTimeZone(timeZone)
        p
    }.invoke()

    fun parse(text: String): List<Observation> = gson.fromJson<List<Observation>>(text)

    private val gson = GsonBuilder()
            .registerTypeAdapter<Observation> {
                deserialize { it.json.safeObject()?.toObservation() }
            }.create()

    private fun JsonObject.toObservation(): Observation? =
            get(observationTimeKey)
                    .safePrimitive()
                    ?.let { timeParser.parse(it.asString) }
                    ?.let { time -> get(observationValueKey).safeObject()
                            ?.let { Observation(time, it.toPrecip()) }
                    }

    private fun JsonObject.toPrecip(): Precip? =
            get(precipTypeKey)
                    .safePrimitive()
                    ?.safeString()
                    ?.let {
                        val amount = get(precipAmountKey)
                                .safePrimitive()
                                ?.safeFloat()

                        when (it) {
                            precipRainValue -> amount?.let { Precip.Rain(it) }
                            precipSnowValue -> amount?.let { Precip.Snow(it) }
                            precipMixedValue -> amount?.let { Precip.Mixed(it) }
                            else -> Precip.None
                        }
                    }

    private fun JsonElement.safeObject(): JsonObject? = if (isJsonObject) asJsonObject else null
    private fun JsonElement.safePrimitive(): JsonPrimitive? = if (isJsonPrimitive) asJsonPrimitive else null
    private fun JsonPrimitive.safeString(): String? = if (isString) asString else null
    private fun JsonPrimitive.safeFloat(): Float? = if (isNumber) asFloat else null
}