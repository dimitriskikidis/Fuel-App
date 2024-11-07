package com.dimitriskikidis.fuelapp.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View

object MarkerIconGenerator {

    private val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

    fun makeMarkerIcon(container: View): Bitmap {
        container.measure(measureSpec, measureSpec)
        val measuredWidth = container.measuredWidth
        val measuredHeight = container.measuredHeight
        container.layout(0, 0, measuredWidth, measuredHeight)
        val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.TRANSPARENT)
        val canvas = Canvas(bitmap)
        container.draw(canvas)
        return bitmap
    }
}