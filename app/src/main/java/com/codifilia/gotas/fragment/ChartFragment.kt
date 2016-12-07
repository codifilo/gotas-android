package com.codifilia.gotas.fragment

import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codifilia.gotas.MainActivity
import com.codifilia.gotas.R
import com.codifilia.gotas.precipitation.Observation
import com.codifilia.gotas.precipitation.Service
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.patloew.rxlocation.RxLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class ChartFragment : Fragment() {

    private val disposable = CompositeDisposable()
    private val service = Service()
    private var chart: LineChart? = null

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val rootView: View? = inflater?.inflate(R.layout.fragment_chart, container, false)
        initChart(rootView)
        return rootView
    }

    override fun onResume() {
        super.onResume()

        getRxLocation()
                ?.location()
                ?.lastLocation()
                ?.observeOn(Schedulers.io())
                ?.map { it.observations() }
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { fillChart(it) }
                ?.let { disposable.add(it) }
    }

    override fun onPause() {
        super.onPause()
        disposable.clear()
    }

    fun getRxLocation(): RxLocation? = activity
            ?.let { it as MainActivity }
            ?.let { it.rxLocation }

    private fun initChart(view: View?) {
        chart = view?.findViewById(R.id.chart) as? LineChart

        val xAxis = chart?.xAxis
        xAxis?.setDrawLabels(true)
        xAxis?.granularity = 0.25f
        xAxis?.valueFormatter = IAxisValueFormatter { value, axis ->
            val formatter = SimpleDateFormat("HH:mm")
            formatter.format(Date(value.toLong()))
        }
        xAxis?.setDrawGridLines(false)
        xAxis?.setDrawAxisLine(true)

        val yAxis = chart?.axisLeft
        yAxis?.axisMinimum = 0f
        chart?.axisRight?.isEnabled = false
        yAxis?.setDrawAxisLine(true)
        yAxis?.setDrawGridLines(false)
    }

    fun fillChart(observations: List<Observation>) {
        val entries: List<Entry> = observations.flatMap { o ->
            o.value?.let { listOf(Entry(o.time.time.toFloat(), it.amount))} ?: listOf()
        }
        val dataSet = LineDataSet(entries, "Precip")
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        val lineData = LineData(dataSet)
        chart?.data = lineData
        chart?.invalidate()
    }

    private fun Location.observations(): List<Observation> = service.retrieve(latitude, longitude)
}
