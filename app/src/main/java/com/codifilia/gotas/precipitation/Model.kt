package com.codifilia.gotas.precipitation

import java.util.*

data class Observation(val time: Date, val value: Precip?)

sealed class Precip(open val amount: Double) {
    class Rain(override val amount: Double) : Precip(amount)
    class Snow(override val amount: Double) : Precip(amount)
    class Mixed(override val amount: Double) : Precip(amount)
    object None : Precip(0.0)
}