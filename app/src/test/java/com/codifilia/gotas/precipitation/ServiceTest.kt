package com.codifilia.gotas.precipitation

import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceTest {

    @Test
    fun basicRetrieveTest() {
        val prov = Service()
        val response = prov.retrieve(37.3753501, -6.0250983)
        assertTrue(response.isNotEmpty())
    }
}