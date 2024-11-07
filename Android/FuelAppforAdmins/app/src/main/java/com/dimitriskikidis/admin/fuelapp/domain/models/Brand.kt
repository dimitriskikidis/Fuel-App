package com.dimitriskikidis.admin.fuelapp.domain.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.Base64

data class Brand(
    val id: Int,
    val name: String,
    val iconBytes: String
) {

    val iconBitmap: Bitmap

    init {
        val iconByteArray = Base64.getMimeDecoder().decode(iconBytes)
        iconBitmap = BitmapFactory.decodeByteArray(iconByteArray, 0, iconByteArray.size)
    }
}
