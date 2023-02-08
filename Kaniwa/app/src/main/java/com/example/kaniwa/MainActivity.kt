package com.example.kaniwa

import android.app.ActionBar
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.kaniwa.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var binding: ActivityMainBinding
    private lateinit var placesClient:PlacesClient
    private lateinit var navigationView:NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout

    var broadcastReceiver: InternetReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //inicializa Places en caso de que no este inicializado
        if(!Places.isInitialized()){
            Places.initialize(this, getString(R.string.google_maps_key))
        }
        //Crea un cliente de Places
        placesClient = Places.createClient(this)
        setupAutocomplete()

        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        //GUARDADO DE DATOS
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        //Navigation view
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        drawerLayout=findViewById(R.id.drawer_layout)
        toggle=ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        //Metodo para ver si hay conexion a internet
        broadcastReceiver = InternetReceiver()
        internetStatus()
    }

    fun internetStatus(){
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION) )
    }

    /*override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mapFragment -> replaceFragment(MapFragment(),item.title.toString())
            R.id.costosFragment -> replaceFragment(PruebaFragment(),item.title.toString())
            R.id.FavoritasFragment -> replaceFragment(fragment_favoritas(),item.title.toString())
            R.id.cerrar -> logout()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(fragment: Fragment, title: String){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment, fragment)
        fragmentTransaction.commit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupAutocomplete(){
        //Tomar el fragment del layout para referenciarlo
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setPlaceFields(listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        val token = AutocompleteSessionToken.newInstance()
        //instanciar rectangulo del area de Xalapa
        val xalapa = RectangularBounds.newInstance(
            LatLng(19.487500, -96.972906),
            LatLng(19.599870, -96.839109)
        )
        //Se setea el area restringida de busqueda y el pais
        autocompleteFragment.setLocationRestriction(xalapa)
        autocompleteFragment.setCountries("MX")

        //Listener al elegir elemento del fragment
        autocompleteFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener{
            override fun onPlaceSelected(place: Place){
                val latLong = "latitud ${place.latLng.latitude!!} longitud ${place.latLng?.longitude}"
                buscarUbi(place.latLng)
                comparar(place.latLng)
            }
            override fun onError(status: Status) {
                Toast.makeText(applicationContext,"No se selecciono destino",Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun buscarUbi(latLng: LatLng){
        val mapFrag = MapFragment.getInstance()
        if(mapFrag!=null){
            mapFrag.setZoom(latLng)
        }else{
            Toast.makeText(applicationContext, "MapFragment null",Toast.LENGTH_LONG).show()
        }
    }

    private fun comparar(latLng: LatLng){
        val mapFrag = MapFragment.getInstance()
        if(mapFrag!=null){
            mapFrag.compararDistancia(latLng)
        }else{
            Toast.makeText(applicationContext, "MapFragment null",Toast.LENGTH_LONG).show()
        }
    }

    private fun logout(){
        val editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        editor.remove ("email")
        editor.remove ("provider")
        editor.apply()
        this.finish()
    }

}
