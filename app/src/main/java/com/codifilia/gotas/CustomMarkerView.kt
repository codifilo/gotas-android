package com.codifilia.gotas

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val timeFormatter = SimpleDateFormat("HH:mm")
    private val textFormat = "%s - %s mm/h"
    private val textView = findViewById(R.id.tvContent) as? TextView

    override fun refreshContent(e: Entry?, highlight: Highlight?) {

        e?.let {
            val time = timeFormatter.format(Date(e.x.toLong()))
            textView?.text = textFormat.format(time, it.y.toInt())
        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF = MPPointF((-(width / 2)).toFloat(), -height.toFloat())
}