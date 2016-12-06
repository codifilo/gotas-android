package com.codifilia.gotas.precipitation

import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceTest {

    @Test
    fun basicRetrieveTest() {
        val prov = Service()
        val response = prov.retrieve(38.7616023, -0.9452381)
        assertTrue(response.isNotEmpty())
    }
}