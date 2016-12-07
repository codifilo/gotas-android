package com.codifilia.gotas.precipitation

import java.util.*

data class Observation(val time: Date, val value: Precip?)

sealed class Precip(open val amount: Float) {
    class Rain(override val amount: Float) : Precip(amount)
    class Snow(override val amount: Float) : Precip(amount)
    class Mixed(override val amount: Float) : Precip(amount)
    object None : Precip(0.0f)
}