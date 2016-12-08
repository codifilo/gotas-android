package com.codifilia.gotas.fragment

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.codifilia.gotas.MainActivity
import com.codifilia.gotas.R
import com.codifilia.gotas.precipitation.Observation
import com.codifilia.gotas.precipitation.Service
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
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


        chart?.onChartGestureListener = onChartGestureListener
        chart?.setOnChartValueSelectedListener(onChartValueSelectedListener)
        chart?.setDrawGridBackground(false)

        // no description text
        chart?.description?.isEnabled = false

        // enable touch gestures
        chart?.setTouchEnabled(true)

        // enable scaling and dragging
        chart?.isDragEnabled = true
        chart?.setScaleEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately
        chart?.setPinchZoom(true)

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        // todo!


        // configure x-axis
        val xAxis = chart?.xAxis
        xAxis?.position = XAxis.XAxisPosition.BOTTOM
        xAxis?.granularity = 0.5f
        xAxis?.valueFormatter = IAxisValueFormatter { value, axis ->
            val formatter = SimpleDateFormat("HH:mm")
            formatter.format(Date(value.toLong()))
        }
        xAxis?.setDrawGridLines(false)

        // limit lines
        val lightLimit = LimitLine(0.5f, getString(R.string.light_rain_intensity))
        lightLimit.lineWidth = 2f
        lightLimit.enableDashedLine(10f, 10f, 0f)
        lightLimit.labelPosition = LimitLabelPosition.RIGHT_TOP
        lightLimit.textSize = 10f
        lightLimit.lineColor = Color.BLUE

        val moderateLimit = LimitLine(4f, getString(R.string.moderate_rain_intensity))
        moderateLimit.lineWidth = 2f
        moderateLimit.enableDashedLine(10f, 10f, 0f)
        moderateLimit.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        moderateLimit.textSize = 10f
        moderateLimit.lineColor = Color.YELLOW

        val heavyLimit = LimitLine(16f, getString(R.string.heavy_rain_intensity))
        heavyLimit.lineWidth = 2f
        heavyLimit.enableDashedLine(10f, 10f, 0f)
        heavyLimit.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        heavyLimit.textSize = 10f
        heavyLimit.lineColor = Color.RED

        val veryHeavyLimit = LimitLine(50f, getString(R.string.very_heavy_rain_intensity))
        veryHeavyLimit.lineWidth = 2f
        veryHeavyLimit.enableDashedLine(10f, 10f, 0f)
        veryHeavyLimit.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        veryHeavyLimit.textSize = 10f
        veryHeavyLimit.lineColor = Color.BLACK

        // configure y-axis
        val leftAxis = chart?.axisLeft
        leftAxis?.setDrawGridLines(false)
        leftAxis?.axisMinimum = 0f
        leftAxis?.addLimitLine(lightLimit)
        leftAxis?.addLimitLine(moderateLimit)
        leftAxis?.addLimitLine(heavyLimit)
        leftAxis?.addLimitLine(veryHeavyLimit)
        leftAxis?.setDrawZeroLine(true)
        leftAxis?.enableAxisLineDashedLine(10f, 10f, 10f)

        val rightAxis = chart?.axisRight
        rightAxis?.isEnabled = false
    }

    fun fillChart(observations: List<Observation>) {
        val entries: List<Entry> = observations.flatMap { o ->
            o.value?.let { listOf(Entry(o.time.time.toFloat(), it.amount))} ?: listOf()
        }

        val dataSet = LineDataSet(entries, "mm/h")
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        //dataSet.color = Color.BLUE
        dataSet.lineWidth = 2f
        dataSet.setDrawFilled(true)

        val lineData = LineData(dataSet)
        chart?.data = lineData
        chart?.invalidate()
    }

    private fun Location.observations(): List<Observation> = service.retrieve(latitude, longitude)

    private val onChartValueSelectedListener = object : OnChartValueSelectedListener {

        override fun onValueSelected(e: Entry, h: Highlight) {
        }

        override fun onNothingSelected() {
        }
    }

    private val onChartGestureListener = object : OnChartGestureListener {

        override fun onChartGestureEnd(me: MotionEvent?,
                                       lastPerformedGesture: ChartTouchListener.ChartGesture?) {
        }

        override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
        }

        override fun onChartLongPressed(me: MotionEvent?) {
        }

        override fun onChartDoubleTapped(me: MotionEvent?) {
        }

        override fun onChartFling(me1: MotionEvent?,
                                  me2: MotionEvent?,
                                  velocityX: Float,
                                  velocityY: Float) {
        }

        override fun onChartSingleTapped(me: MotionEvent?) {
        }

        override fun onChartGestureStart(me: MotionEvent?,
                                         lastPerformedGesture: ChartTouchListener.ChartGesture?) {
        }

        override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
        }
    }
}
