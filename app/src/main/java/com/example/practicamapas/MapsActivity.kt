package com.example.practicamapas

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.gps.InfoWindowsCustom
import com.example.practicamapas.Constantes.INTERVAL_TIME
import com.example.practicamapas.Constantes.lapaz

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.practicamapas.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    companion object {
        val REQUIERED_PERMISSION_GPS = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        )
    }

    private var isGPSEnabled = false
    private val PERMISSION_ID = 42
    private lateinit var fusedLocation : FusedLocationProviderClient
    private var contador: Int = 0
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    val misRutas = mutableListOf<LatLng>()
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.fabHabilitarGPS.setOnClickListener {
            enableGPSServices()
        }
        binding.fabCoordenadas.setOnClickListener {
            manageLocation()
        }
        setupToggleButtons()
        binding.fabMostrarRecorrido.setOnClickListener {
            if(contador > 0) {
                try {
                    setupPolyline(misRutas)
                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
            }
            else Toast.makeText(this, "Debes tener al menos m??s de una posici??n registrada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        mMap.setInfoWindowAdapter(InfoWindowsCustom(this))

        mMap.setMinZoomPreference(13f)

        //Marcador
        //Tachuela roja que se posiciona en el mapa donde quieren ubicarse
        /*mMap.addMarker(MarkerOptions()
            .title("Salar de Uyuni")
            .snippet("${salarUyuni.latitude},${salarUyuni.longitude}") //Contenido extra
            .position(salarUyuni)
        )*/

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true // Botones + - zoom in zoom out
            isCompassEnabled = true // la br??jula de orientaci??n del mapa
            isMapToolbarEnabled = true // habilito para un marcador la opci??n de ir a ver una ruta a verlo en la app Mapa Google
            isRotateGesturesEnabled = true // deshabilitar la opci??n de rotaci??n del mapa
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lapaz,10f))

        mMap.setPadding(0,0,0,Utils.densityPixel(64))// densidad de pixeles en pantalla

        /**
         * Estilo personalizado de mapa
         */

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.my_map_style))

        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener {
            //it es la posici??n donde haces click con tu dedo
            mMap.addMarker(MarkerOptions()
                .title("Nueva ubicaci??n Random")
                .snippet("${it.latitude},\n${it.longitude}")
                .position(it)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
            )
        }
    }
    override fun onMarkerClick(marker: Marker): Boolean {
        //marker es el marcador al que le estas haciendo click
        Toast.makeText(this, "${marker.position.latitude}, ${marker.position.longitude}", Toast.LENGTH_LONG).show()
        return false
    }


    /**
     * PERMISOS*/


    private fun enableGPSServices() {
        if(!hasGPSEnabed()){
            AlertDialog.Builder(this)
                .setTitle(R.string.alert_dialog_title)
                .setMessage(R.string.alert_dialog_description)
                .setPositiveButton(
                    R.string.alert_dialog_button_accept,
                    DialogInterface.OnClickListener{
                            dialog, wich -> goToEnableGPS()
                    })
                .setNegativeButton(R.string.alert_dialog_button_denny) {
                        dialog, wich -> isGPSEnabled = false
                }
                .setCancelable(true)
                .show()
        } else
            Toast.makeText(this,"Ya tienes el GPS habilitado", Toast.LENGTH_SHORT).show()
    }

    private fun hasGPSEnabed(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun goToEnableGPS() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    //Seccion: tratamiento de permisos para el uso del GPS
    private fun allPermissionsGrantedGPS(): Boolean {
        return REQUIERED_PERMISSION_GPS.all {
            ContextCompat.checkSelfPermission( baseContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }


    /**
     * COORDENADAS*/


    @SuppressLint("MissingPermission")
    private fun manageLocation() {
        if (hasGPSEnabed()){
            if (allPermissionsGrantedGPS()) {
                //solo puede ser tratado si el usuario dio permisos
                fusedLocation = LocationServices.getFusedLocationProviderClient(this)
                //Estan configurando un evento que escuche cuando
                // del sensor GPS se captura datos correctamente
                fusedLocation.lastLocation.addOnSuccessListener {
                        location -> requestNewLocationData()
                }
            }else{
                requestPermissionsLocation()
            }
        }else{
            goToEnableGPS()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var myLocationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            INTERVAL_TIME//10 segundos = 10000 milisegundos
        ).setMaxUpdates(2)
            .build()
        fusedLocation.requestLocationUpdates(
            myLocationRequest,
            myLocationCallback,
            Looper.myLooper())
    }

    private fun requestPermissionsLocation() {
        ActivityCompat.requestPermissions(this, REQUIERED_PERMISSION_GPS, PERMISSION_ID)
    }

    private val myLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var myLastLocation: Location? = locationResult.lastLocation
            if(myLastLocation != null) {
                var lastLatitude = myLastLocation.latitude
                var lastLongitude = myLastLocation.longitude
                var pos = LatLng(lastLatitude, lastLongitude)
                mMap.clear()
                mMap.addMarker(MarkerOptions()
                    .position(pos)
                    .title("Ubicaci??n ${contador}")
                    .snippet("${pos.latitude},\n${pos.longitude}")
                )?.run {
                    setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,20f))
                misRutas.add(pos)

                latitud = myLastLocation.latitude
                longitud = myLastLocation.longitude
                contador++
                getAddressName()
            }
        }
    }

    private fun getAddressName() {
        val geocoder = Geocoder(this)
        try {
            val direcciones = geocoder.getFromLocation(latitud,longitud,1)
            binding.txtDireccion.text = direcciones.get(0).getAddressLine(0)
        }catch (e:Exception){
            binding.txtDireccion.text="No se puede obtener la direccion"
        }
    }

    private fun setupPolyline(ruta: MutableList<LatLng>) {
        //las l??neas Polyline dependen de un arreglo o lista de coordenadas
        val polyline = mMap.addPolyline(
            PolylineOptions()
                .color(Color.YELLOW)
                .width(13f) //ancho de la l??nea
                .clickable(true) //la l??nea debe ser clickeada
                .geodesic(true) //curvatura con respecto al radio de la tierra
        )
        polyline.points = ruta

        lifecycleScope.launch{
            val misRutasEnTiempoReal = mutableListOf<LatLng>()
            for (punto in ruta){
                misRutasEnTiempoReal.add(punto)
                polyline.points = misRutasEnTiempoReal
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(punto,20f))
                delay(2_000)
            }
        }
    }

    private fun setupToggleButtons(){
        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked){
                when(checkedId){
                    R.id.btnNormal -> mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    R.id.btnHibrido -> mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                    R.id.btnSatelital -> mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    R.id.btnTerreno -> mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                    else  -> mMap.mapType = GoogleMap.MAP_TYPE_NONE
                }
            }
        }
    }
}