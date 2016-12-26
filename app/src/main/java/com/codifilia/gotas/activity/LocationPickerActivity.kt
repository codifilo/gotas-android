package com.codifilia.gotas.activity

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import com.codifilia.gotas.R
import com.codifilia.gotas.util.lastKnownLocation
import com.codifilia.gotas.util.latitudeKey
import com.codifilia.gotas.util.longitudeKey
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        val resultLatLngCode = 1
        val resultGpsCode = 2
    }

    private var selectLocationButton: Button? = null
    private var myLocationButton: ImageButton? = null
    private var map: GoogleMap? = null
    private val defaultLat = 40.4219642
    private val defaultLon = -3.7047157
    private val zoom = 10f
    private var initialCenter: LatLng = LatLng(defaultLat, defaultLon)

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        selectLocationButton = findViewById(R.id.selectLocationButton) as? Button
        selectLocationButton?.setOnClickListener {
            val result = Intent()

            if (isMyLocationSelected) {
                setResult(resultGpsCode, result)
                finish()
            }
            else {
                val latLng = map?.cameraPosition?.target
                result.putExtra(latitudeKey, latLng?.latitude)
                result.putExtra(longitudeKey, latLng?.longitude)
                setResult(resultLatLngCode, result)
                finish()
            }
        }
        myLocationButton = findViewById(R.id.myLocationButton) as? ImageButton
        myLocationButton?.setOnClickListener {
            lastKnownLocation?.let {
                map?.centerOn(it, zoom)
            }
        }

        intent.let {
            val lat = it.getDoubleExtra(latitudeKey, defaultLat)
            val lon = it.getDoubleExtra(longitudeKey, defaultLon)
            initialCenter = LatLng(lat, lon)
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        lastKnownLocation?.let {
            googleMap.centerOn(it, zoom)
        }
        map = googleMap
    }

    private fun GoogleMap.centerOn(location: Location, zoom: Float) {
        val cameraPosition = CameraPosition.Builder()
                .target(LatLng(location.latitude, location.longitude))
                .zoom(zoom)
                .build()
        animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private val isMyLocationSelected: Boolean get() {
        val latLng = map?.cameraPosition?.target
        return latLng?.let { it.samePos(lastKnownLocation) } ?: false
    }

    private fun LatLng.samePos(loc: Location?): Boolean =
            loc?.let { loc.latitude == latitude && loc.longitude == longitude } ?: false

}
