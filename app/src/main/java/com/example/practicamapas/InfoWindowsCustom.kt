package com.example.gps

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.practicamapas.R
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker


class InfoWindowsCustom(private val inflater: LayoutInflater) : InfoWindowAdapter {
    override fun getInfoContents(m: Marker): View? {
        //Carga layout personalizado.
        val v: View = inflater.inflate(R.layout.info_windows, null)
        val info = m.title!!.split("&").toTypedArray()
        val url = m.snippet
        (v.findViewById(R.id.info_window_direccion) as TextView).text = "Av. Argentina 2083, La Paz"
        (v.findViewById(R.id.info_window_nombre) as TextView).text = "Universidad del Valle"
        (v.findViewById(R.id.info_window_telefono) as TextView).text = "Teléfono: 2 2001800"
        return v
    }

    override fun getInfoWindow(m: Marker): View? {
        return null
    }

    companion object {
        private const val TAG = "CustomInfoWindowAdapter"
    }
}