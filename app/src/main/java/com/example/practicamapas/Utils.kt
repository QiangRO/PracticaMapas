package com.example.practicamapas

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.viewbinding.ViewBinding

object Utils {
    var binding: ViewBinding? = null

    fun densityPixel(pixeles: Int): Int {
        if (binding == null) return 0
        val escala = binding!!.root.context.resources.displayMetrics.density
        return (pixeles * escala + 0.5f).toInt()
    }
    fun getBitMapFromVector(context: Context, resId: Int): Bitmap?
    {
        return AppCompatResources.getDrawable(context, resId)?.toBitmap()
    }
}