package com.example.gps

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.practicamapas.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker


class InfoWindowsCustom(contexto: Context) : GoogleMap.InfoWindowAdapter {
    var mWindow = (contexto as Activity).layoutInflater.inflate(R.layout.info_windows, null)

    private fun rendowWindowText(marker: Marker, view: View) {
        val tvMsgPrincipal = view.findViewById<TextView>(R.id.info_window_textMarker)
        val tvCoordenadas = view.findViewById<TextView>(R.id.info_window_cordenadas)
        tvMsgPrincipal.text = marker.title
        tvCoordenadas.text = marker.snippet
    }

    override fun getInfoWindow(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoContents(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }
}