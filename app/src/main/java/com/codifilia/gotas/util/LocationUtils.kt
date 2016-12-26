package com.codifilia.gotas.util

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager

val latitudeKey = "latitude"
val longitudeKey = "longitude"

val Context.lastKnownLocation: Location?  get() {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    return locationManager?.let {
        it.getLastKnownLocation(it.getBestProvider(Criteria(), false))
    }
}