package com.example.kaniwav0

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.kaniwav0.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var map: GoogleMap

    companion object{
        const val  Request_Code_Location =0
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        creatMarker()
        enableLocation()
    }
    private fun creatMarker(){
        val favoritePlace = LatLng(28.044195, -16.5363842)
        map.addMarker(MarkerOptions().position(favoritePlace).title("Mi playa favorita"))
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(favoritePlace, 18f),
            4000,
            null
        )
    }
    override fun onCreate(savedInstaceState: Bundle ?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createMapFragment()
    }
    private fun createMapFragment(){
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun isLocationPermissionGranted()=ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation(){
        if(!::map.isInitialized) return
        if(isLocationPermissionGranted()){
        map.isMyLocationEnabled = true
        }else{
            requestLocationPermission()
        }
    }
    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "ve a ajustes y acepta  los permisos ", Toast.LENGTH_SHORT).show()
        } else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Request_Code_Location)
        }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            Request_Code_Location -> if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled =true
            } else{
                Toast.makeText(this, "Para activar la localizacion ve a ajustes  y acepta los permisos ", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }


}



