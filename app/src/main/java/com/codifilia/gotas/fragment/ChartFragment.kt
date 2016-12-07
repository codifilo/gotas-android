package com.codifilia.gotas.fragment

import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.codifilia.gotas.MainActivity
import com.codifilia.gotas.R
import com.codifilia.gotas.precipitation.Observation
import com.codifilia.gotas.precipitation.Service
import com.google.android.gms.location.LocationRequest
import com.patloew.rxlocation.RxLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ChartFragment : Fragment() {

    private val disposable = CompositeDisposable()
    private val service = Service()

    private val locationRequest: LocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_LOW_POWER)
            .setInterval(60000)

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onResume() {
        super.onResume()

        getRxLocation()
                ?.location()
                ?.updates(locationRequest)
                ?.observeOn(Schedulers.io())
                ?.map { it.observations() }
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { it.forEach { Log.d("Observation", it.toString()) } }
                ?.let { disposable.add(it) }
    }

    override fun onPause() {
        super.onPause()
        disposable.clear()
    }

    fun getRxLocation(): RxLocation? = activity
            ?.let { it as MainActivity }
            ?.let { it.rxLocation }


    private fun Location.observations(): List<Observation> = service.retrieve(latitude, longitude)
}
