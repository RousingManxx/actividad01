package com.example.kaniwa

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth

class Maps : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map:GoogleMap
    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        createFragment()

        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?: "", provider ?: "")

        //gUARDADO DE DATOS
        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }
    private fun setup(email: String, provider: String) {
        title = "Inicio"
        /*
        binding.closeButton.setOnClickListener {
            //Borrado de datos de sesion
            val prefs =
                getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
            */
    }

    private fun createFragment(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
//-----------------------------------------------------------------------------------------------------------------------------------------------//
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap //Crea el mapa
        zoom() //Añade Zoom sobre la ubicación de xalapa
        ATAZ()
        paradasConocidas()
        ruta1()

        //Configuración de ubicación actual así como declaración de permisos.
        map.setOnMyLocationButtonClickListener(this) //Mensaje cuando se toque el boton de ubicación actual
        map.setOnMyLocationClickListener(this) //Corrdenadas de cuando se de clcick en la ubicación actual (NO el boton)
        enableLocation() //Permisos
    }
//-----------------------------------------------------------------------------------------------------------------------------------------------//

    private fun zoom(){
        val coordenadas = LatLng(19.527409, -96.921380)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 13f), 4000,null)
    }

    private fun ATAZ(){
        val polylineOptions = PolylineOptions()
            .add(LatLng(19.513159, -96.875301)).add(LatLng(19.514231, -96.876293)).add(LatLng(19.514867, -96.877655))
            .add(LatLng(19.515570, -96.879629)).add(LatLng(19.515919, -96.879994)).add(LatLng(19.517322, -96.880932))
            .add(LatLng(19.517664, -96.881358)).add(LatLng(19.518573, -96.882722)).add(LatLng(19.518917, -96.883012)).add(LatLng(19.519913, -96.883790))
            .add(LatLng(19.520247, -96.884289)).add(LatLng(19.522476, -96.894258)).add(LatLng(19.524312, -96.896621)).add(LatLng(19.528484, -96.898686))
            .add(LatLng(19.529091, -96.899457)).add(LatLng(19.532111, -96.905057)).add(LatLng(19.532222, -96.905119))
            .add(LatLng(19.533087, -96.906741)).add(LatLng(19.533237, -96.906911)).add(LatLng(19.533716606735606, -96.9073637145511))
            .add(LatLng(19.534072399005154, -96.90756353912303)).add(LatLng(19.53403700940078, -96.90770837840937)).add(LatLng(19.533956118848486, -96.90787065205386))
            .add(LatLng(19.533731937823557, -96.9081209466737)).add(LatLng(19.533730041948, -96.90819135466118))
            .add(LatLng(19.533790709950953, -96.90833753505167)).add(LatLng(19.54335988617218, -96.92675977189103))
            .add(LatLng(19.543374708864892, -96.92695983914527)).add(LatLng(19.54331404446314, -96.92705505756476))
            .add(LatLng(19.54325085235174, -96.92716368702924)).add(LatLng(19.54313584264787, -96.92738362816768))
            .add(LatLng(19.543035998993457, -96.92741447357118)).add(LatLng(19.541266154099898, -96.92631659622768))
            .add(LatLng(19.54111449106751, -96.92624819989771)).add(LatLng(19.540799789823655, -96.92617443914985))
            .add(LatLng(19.540632, -96.926203)).add(LatLng(19.540554, -96.926232)).add(LatLng(19.540479, -96.926229))
            .add(LatLng(19.540391, -96.926171)).add(LatLng(19.540391, -96.926171)).add(LatLng(19.540355, -96.925914))
            .add(LatLng(19.540400, -96.925841)).add(LatLng(19.540487, -96.925792)).add(LatLng(19.540661, -96.925827))
            .add(LatLng(19.540992613164047, -96.9260052770552)).add(LatLng(19.541288, -96.926215)).add(LatLng(19.542585, -96.927003))
            .add(LatLng(19.543212, -96.927058)).add(LatLng(19.543271, -96.926937)).add(LatLng(19.543270999176876, -96.92678947870952))
            .add(LatLng(19.543205, -96.926640)).add(LatLng(19.535418, -96.911594)).add(LatLng(19.534450, -96.912290))
            .add(LatLng(19.534371, -96.912155)).add(LatLng(19.535286, -96.911455)).add(LatLng(19.535459, -96.911346))
            .add(LatLng(19.535980, -96.911013)).add(LatLng(19.536605, -96.910506)).add(LatLng(19.537113, -96.910160))
            .add(LatLng(19.538359, -96.909300)).add(LatLng(19.539387, -96.908537)).add(LatLng(19.539914, -96.908198))
            .add(LatLng(19.539944538309033, -96.90811333102408)).add(LatLng(19.53991748428516, -96.9080080708572))
            .add(LatLng(19.539803, -96.907909)).add(LatLng(19.539652, -96.907894)).add(LatLng(19.539344, -96.907907))
            .add(LatLng(19.538808, -96.907899)).add(LatLng(19.538381, -96.907982)).add(LatLng(19.534387, -96.908157))
            .add(LatLng(19.534192, -96.908136)).add(LatLng(19.534033, -96.908105)).add(LatLng(19.533777, -96.908022))
            .add(LatLng(19.533700, -96.907972)).add(LatLng(19.533404, -96.907726)).add(LatLng(19.533331, -96.907633))
            .add(LatLng(19.533088, -96.907114)).add(LatLng(19.532961, -96.906939)).add(LatLng(19.532747396045167, -96.90659970076227))
            .add(LatLng(19.532747396045167, -96.90659970076227)) .add(LatLng(19.531855, -96.904816))
            .add(LatLng(19.531855, -96.904816)).add(LatLng(19.528950, -96.899542)).add(LatLng(19.528605, -96.899062))
            .add(LatLng(19.528009, -96.898590)).add(LatLng(19.526616, -96.897870)).add(LatLng(19.526014, -96.897629))
            .add(LatLng(19.525529, -96.897366)).add(LatLng(19.524659, -96.896910)).add(LatLng(19.524310, -96.896722))
            .add(LatLng(19.524017, -96.896478)).add(LatLng(19.523301, -96.895624)).add(LatLng(19.522952, -96.895144))
            .add(LatLng(19.522434, -96.894495)).add(LatLng(19.522262, -96.894146)).add(LatLng(19.521407, -96.889561))
            .add(LatLng(19.520944, -96.887580)).add(LatLng(19.520017, -96.884168)).add(LatLng(19.519840, -96.883900))
            .add(LatLng(19.519418, -96.883506)).add(LatLng(19.519350, -96.883464)).add(LatLng(19.518607, -96.882941))
            .add(LatLng(19.518263, -96.882512)).add(LatLng(19.518035, -96.882206)).add(LatLng(19.517641, -96.881498))
            .add(LatLng(19.517338, -96.881157)).add(LatLng(19.516587, -96.880586)).add(LatLng(19.515839, -96.880087))
            .add(LatLng(19.515566, -96.879805)).add(LatLng(19.515407, -96.879523)).add(LatLng(19.514631, -96.877257))
            .add(LatLng(19.514444, -96.876881)).add(LatLng(19.514136, -96.876457)).add(LatLng(19.513752, -96.876186))
            .add(LatLng(19.513535, -96.876093)).add(LatLng(19.513115, -96.875728)).add(LatLng(19.512718, -96.875296))
            .add(LatLng(19.512522, -96.874968))

            //.add(LatLng())
            .width(15f)
            .color(ContextCompat.getColor(this, R.color.ATAZ))
        val polyline = map.addPolyline(polylineOptions)
        val pattern = listOf(
            Dot(), Gap(10f), Dash(50f), Gap(10f)
        )
        polyline.pattern = pattern
        polyline.startCap = RoundCap()
        polyline.endCap = RoundCap()
        //polyline.endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.dcp1))
        polyline.isClickable = true
        map.setOnPolylineClickListener { Toast.makeText(this,"Ruta: ATAZ",Toast.LENGTH_SHORT).show() }
    }

    private fun paradasConocidas(){
        //Parada Fei
        val c0 = LatLng(19.542635, -96.927233)
        val economia = MarkerOptions().position(c0).title("Economía")
        map.addMarker(economia)
        //Agua Santa 1
        val c1 = LatLng(19.529693, -96.900883)
        val aguaSanta1 = MarkerOptions().position(c1).title("Agua Santa 1")
        map.addMarker(aguaSanta1)
        //map.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.
        // ic_paradas1)).anchor(0.0f,1.0f).position(coordenadas).title("Agua Santa 1"))

        //Agua Santa 2
        val c2 = LatLng(19.529690, -96.900371)
        val aguaSanta2 = MarkerOptions().position(c2).title("Agua Santa 2")
        map.addMarker(aguaSanta2)

        //Doña Falla
        val c3 = LatLng(19.522595, -96.894830)
        val dFalla = MarkerOptions().position(c3).title("Parque de doña Falla")
        map.addMarker(dFalla)

        //BIENESTAR
        val c3_2 = LatLng(19.522595, -96.894830)
        val sBienestar = MarkerOptions().position(c3_2).title("Secretaria Bienestar")
        map.addMarker(sBienestar)

        //Parque natura
        val c4 = LatLng(19.518945, -96.883305)
        val pNatura = MarkerOptions().position(c4).title("Parque Natura")
        map.addMarker(pNatura)

        //Plaza americas
        val c5 = LatLng(19.513259188258463, -96.8756648576462)
        val plazaAmericas = MarkerOptions().position(c5).title("Plaza Americas")
        map.addMarker(plazaAmericas)

        //Parada frente a plaza americas
        val c6 = LatLng(19.513174, -96.875285)
        val plazaAmericas2 = MarkerOptions().position(c6).title("Plaza americas")
        map.addMarker(plazaAmericas2)

        //Plaza Animas
        val c7 = LatLng(19.518794276525053, -96.88282015160287)
        val  plazaAnimas= MarkerOptions().position(c7).title("Plaza Animas")
        map.addMarker(plazaAnimas)

        //Parada CorpusGym
        val c8 = LatLng(19.532288, -96.905237)
        val corpusGym = MarkerOptions().position(c8).title("CorpusGym")
        map.addMarker(corpusGym)

        //Pomona
        val c9 = LatLng(19.531994, -96.905337)
        val pomona = MarkerOptions().position(c9).title("Pomona")
        map.addMarker(pomona)



        /*
        //
        val c = LatLng()
        val  = MarkerOptions().position(c).title("")
        map.addMarker()

        //
        val c = LatLng()
        val  = MarkerOptions().position(c).title("")
        map.addMarker()

        //
        val c = LatLng()
        val  = MarkerOptions().position(c).title("")
        map.addMarker()
         */

    }

    private fun ruta1(){
        //Antojitos Vero
        /*val c0 = LatLng(19.542635, -96.927233)
        val cc0 = MarkerOptions()
            .position(c0)
            .title("Ruta 1: Parada-01")
            //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_paradas))
        map.addMarker(cc0)*/
        val c0 = LatLng(19.542635, -96.927233)
        val cc0 = map.addMarker(
            MarkerOptions()
                .position(c0)
                .title("Ruta 1: Parada-01")
            //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_paradas))
        )

        //map.addMarker(cc0)

        // Av. Manuel Ávila Camacho 290, Zona Centro, Centro, 91000 Xalapa-Enríquez, Ver., México
        val c8 = LatLng(19.539844, -96.926898)
        val cc8= MarkerOptions()
            .position(c8)
            .title("Ruta 1 : Parada-02")
        map.addMarker(cc8)

        // Av. Manuel Ávila Camacho 244A, Zona Centro, Centro, 91000 Xalapa-Enríquez, Ver., México
        val c9 = LatLng(19.538384, -96.928626)
        val cc9= MarkerOptions()
            .position(c9)
            .title("Ruta 1: Parada-03")
        map.addMarker(cc9)

        //C. Coatepec n2, Veracruz, 91020 Xalapa-Enríquez, Ver., México
        val c10 = LatLng(19.537598, -96.929504)
        val cc10= MarkerOptions()
            .position(c10)
            .title("Ruta 1 : Parada-04")
        map.addMarker(cc10)

        //Av. Manuel Ávila Camacho 188, Zona Centro, Centro, 91000 Xalapa-Enríquez, Ver., México
        val c11 = LatLng(19.536053, -96.931345)
        val cc11= MarkerOptions()
            .position(c11)
            .title("Ruta 1 : Parada-05")
        map.addMarker(cc11)

        //Av. Manuel Ávila Camacho 159, Francisco Ferrer Guardia, 91026 Xalapa-Enríquez, Ver., México
        val c12 = LatLng(19.534837, -96.932690)
        val cc12= MarkerOptions()
            .position(c12)
            .title("Ruta 1 : Parada-06")
        map.addMarker(cc12)

        // C. Ignacio de la Llave 16 Altos, Represa del Carmen, 91050 Xalapa-Enríquez, Ver., México
        val c13 = LatLng(19.532100, -96.934069)
        val cc13= MarkerOptions()
            .position(c13)
            .title("Ruta 1 : Parada-07")
        map.addMarker(cc13)

        //C. Ignacio de la Llave 45, Represa del Carmen, 91050 Xalapa-Enríquez, Ver., México
        val c14 = LatLng(19.529710, -96.934683)
        val cc14= MarkerOptions()
            .position(c14)
            .title("Ruta 1 : Parada-08")
        map.addMarker(cc14)

        //José Cardel 105, Benito Juárez, 91070 Xalapa-Enríquez, Ver., México
        val c15 = LatLng(19.526119, -96.934323)
        val cc15= MarkerOptions()
            .position(c15)
            .title("Ruta 1 : Parada-09")
        map.addMarker(cc15)

        //Mora Beristain 2, Zona Centro, Allanburg, 12242 Xalapa-Enríquez, Ver., México
        val c16 = LatLng(19.525303, -96.933597)
        val cc16= MarkerOptions()
            .position(c16)
            .title("Ruta 1 : Parada-10")
        map.addMarker(cc16)

        //Av Venustiano Carranza 23, Francisco I. Madero, 91070 Xalapa-Enríquez, Ver., México
        val c17 = LatLng(19.523578419666222, -96.93122363142665)
        val cc17= MarkerOptions()
            .position(c17)
            .title("Ruta 1 : Parada-11")
        map.addMarker(cc17)

        //Av Venustiano Carranza 66, Francisco I. Madero, 91070 Xalapa-Enríquez, Ver., México
        val c18 = LatLng(19.526119, -96.934323)
        val cc18= MarkerOptions()
            .position(c18)
            .title("Ruta 1 : Parada-12")
        map.addMarker(cc18)

        //91080, Av Venustiano Carranza 93, Venustiano Carranza, 91070 Xalapa-Enríquez, Ver., México
        val c19 = LatLng(19.522152780716706, -96.92815555684797)
        val cc19= MarkerOptions()
            .position(c19)
            .title("Ruta 1 : Parada-13")
        map.addMarker(cc19)

        //Av Venustiano Carranza 124, Felipe Carrillo Puerto, 91080 Xalapa-Enríquez, Ver.
        val c20 = LatLng(19.521552286456302, -96.92687548095408)
        val cc20= MarkerOptions()
            .position(c20)
            .title("Ruta 1 : Parada-14")
        map.addMarker(cc20)

        //Av Venustiano Carranza 144, Felipe Carrillo Puerto, 91080 Xalapa-Enríquez, Ver., México
        val c21 = LatLng(19.521468, -96.926276)
        val cc21= MarkerOptions()
            .position(c21)
            .title("Ruta 1 : Parada-15")
        map.addMarker(cc21)

        //Av Venustiano Carranza 168, Felipe Carrillo Puerto, 91080 Xalapa-Enríquez, Ver., México
        val c22 = LatLng(19.523007423710673, -96.92546385898572)
        val cc22= MarkerOptions()
            .position(c22)
            .title("Ruta 1 : Parada-16")
        map.addMarker(cc22)

        //Av Venustiano Carranza 269A, Felipe Carrillo Puerto, 91080 Xalapa-Enríquez, Ver., México
        val c23 = LatLng(19.52013786649953, -96.9233289435346)
        val cc23= MarkerOptions()
            .position(c23)
            .title("Ruta 1 : Parada-17")
        map.addMarker(cc23)

        //Av Venustiano Carranza 299, Felipe Carrillo Puerto, 91080 Xalapa-Enríquez, Ver., México
        val c24 = LatLng(19.518963686109146, -96.92250116705212)
        val cc24= MarkerOptions()
            .position(c24)
            .title("Ruta 1 : Parada-18")
        map.addMarker(cc24)

        //Universitario Gonzalo Aguirre Beltrán, Zona Universitaria, 91090 Xalapa-Enríquez, Ver., México
        val c25 = LatLng(19.518300001172367, -96.919902039671)
        val cc25= MarkerOptions()
            .position(c25)
            .title("Ruta 1 : Parada-19")
        map.addMarker(cc25)

        //Pergola 8, Lomas del Estadio, 91090 Xalapa-Enríquez, Ver., México
        val c26 = LatLng(19.519127905211196, -96.91809034643435)
        val cc26= MarkerOptions()
            .position(c26)
            .title("Ruta 1 : Parada-20")
        map.addMarker(cc26)

        //C. Salvador Díaz Mirón 37, Zona Centro, Lomas del Estadio, 91000 Xalapa-Enríquez, Ver., México
        val c27 = LatLng(19.521012160288073, -96.91819139204021)
        val cc27= MarkerOptions()
            .position(c27)
            .title("Ruta 1 : Parada-21")
        map.addMarker(cc27)

        //Enrique C Rébsamen 69, Centro, 91000 Xalapa-Enríquez, Ver., México
        val c28 = LatLng(19.521476109074012, -96.91626201002641)
        val cc28= MarkerOptions()
            .position(c28)
            .title("Ruta 1 : Parada-22")
        map.addMarker(cc28)

        //Av. Enrique C. Rébsamen 140, Bella Vista, 91090 Xalapa-Enríquez, Ver., México
        val c29 = LatLng(19.517391018443167, -96.91308574102696)
        val cc29= MarkerOptions()
            .position(c29)
            .title("Ruta 1 : Parada-23")
        map.addMarker(cc29)

        //Av. Enrique C. Rébsamen, Martires de Chicago, 91094 Xalapa-Enríquez, Ver., México
        val c30 = LatLng(19.515383200788044, -96.91108681973272)
        val cc30= MarkerOptions()
            .position(c30)
            .title("Ruta 1 : Parada-24")
        map.addMarker(cc30)

        //Av. Enrique C. Rébsamen, Martires de Chicago, 91090 Xalapa-Enríquez, Ver., México
        val c31 = LatLng(19.514113338028054, -96.90938359925401)
        val cc31= MarkerOptions()
            .position(c31)
            .title("Ruta 1 : Parada-25")
        map.addMarker(cc31)

        //Av. Enrique C. Rébsamen 272, Martires de Chicago, 91090 Xalapa-Enríquez, Ver., México
        val c32 = LatLng(19.512751863725498, -96.9070187816892)
        val cc32= MarkerOptions()
            .position(c32)
            .title("Ruta 1 : Parada-26")
        map.addMarker(cc32)

        //Observatorio metereologico de xalapa, Xalapa 2000, 91097 Xalapa-Enríquez, Ver.
        val c33 = LatLng(19.512524370965586, -96.90404925525434)
        val cc33= MarkerOptions()
            .position(c33)
            .title("Ruta 1 : Parada-27")
        map.addMarker(cc33)

        //Miscelanea "Micro 2000", Manantial de San Cristóbal, Xalapa 2000, 91097 Xalapa-Enríquez, Ver.
        val c34 = LatLng(19.511960449227736, -96.90146353716793)
        val cc34= MarkerOptions()
            .position(c34)
            .title("Ruta 1 : Parada-28")
        map.addMarker(cc34)

        //Parque Xalapa 2000
        val c35 = LatLng(19.50874817012107, -96.90033364689575)
        val cc35= MarkerOptions()
            .position(c35)
            .title("Ruta 1 : Parada-29")
        map.addMarker(cc35)

        //Centro de Verificación C-XL32
        val c36 = LatLng(19.507941051368448, -96.90115439530913)
        val cc36= MarkerOptions()
            .position(c36)
            .title("Ruta 1 : Parada-30")
        map.addMarker(cc36)

        //Instituto Mexicano de Contadores Públicos…
        val c37 = LatLng(19.50556766853243, -96.89623020463095)
        val cc37= MarkerOptions()
            .position(c37)
            .title("Ruta 1 : Parada-31")
        map.addMarker(cc37)

        //Estatua A Yanga
        val c38 = LatLng(19.50518701380518, -96.89346446960897)
        val cc38= MarkerOptions()
            .position(c38)
            .title("Ruta 1 : Parada-32")
        map.addMarker(cc38)

        //Arco Sur 140, Lomas Verdes, 91098 Lomas Verdes, Ver.
        val c39 = LatLng(19.505487367630394, -96.89169141158781)
        val cc39= MarkerOptions()
            .position(c39)
            .title("Ruta 1 : Parada-33")
        map.addMarker(cc39)

        //OXXO Lomas Verdes
        val c40 = LatLng(19.504694614601387, -96.89062574324079)
        val cc40= MarkerOptions()
            .position(c40)
            .title("Ruta 1 : Parada-34")
        map.addMarker(cc40)

        //Gusto Culposo
        val c41 = LatLng(19.505892132924068, -96.8895160101093)
        val cc41= MarkerOptions()
            .position(c41)
            .title("Ruta 1 : Parada-35")
        map.addMarker(cc41)

        //Lomas Verdes, 91098 Lomas Verdes, Ver.
        val c42 = LatLng(19.506391287626922, -96.8868425809209)
        val cc42= MarkerOptions()
            .position(c42)
            .title("Ruta 1 : Parada-36")
        map.addMarker(cc42)

        //Frankfurt Food Truck
        val c43 = LatLng(19.507299629788744, -96.88173050553556)
        val cc43= MarkerOptions()
            .position(c43)
            .title("Ruta 1 : Parada-37")
        map.addMarker(cc43)

        //Arco Sur, Pastoresa, 91223 Pacho Viejo, Ver.
        val c44 = LatLng(19.50684038458372, -96.87661133725486)
        val cc44= MarkerOptions()
            .position(c44)
            .title("Ruta 1 : Parada-38")
        map.addMarker(cc44)

        //Santa Lucía 1, Xalapa, Reserva Territorial, Col Santa Bárbara, Ver.
        val c45 = LatLng(19.508508642854167, -96.87280168036887)
        val cc45= MarkerOptions()
            .position(c45)
            .title("Ruta 1 : Parada-39")
        map.addMarker(cc45)

        //VerifiCentro - El Olmo
        val c46 = LatLng(19.50741171849216, -96.86938588540266)
        val cc46= MarkerOptions()
            .position(c46)
            .title("Ruta 1 : Parada-40")
        map.addMarker(cc46)

        //La Naolinqueña
        val c47 = LatLng(19.506817900718687, -96.86662901755243)
        val cc47= MarkerOptions()
            .position(c47)
            .title("Ruta 1 : Parada-41")
        map.addMarker(cc47)

        //La Toga Pastelería Fina, Carretera Trancas-Coatepec KM 0.2 #122, Emiliano Zapata, 91637 Las Trancas, Ver.
        val c48 = LatLng(19.505157684649337, -96.86263659781252)
        val cc48= MarkerOptions()
            .position(c48)
            .title("Ruta 1 : Parada-42")
        map.addMarker(cc48)

        //Refaccionaria Kimura suc. Trancas, Carr. Coatepec - Las Trancas km. 600-Local 6, Fracc. La Cienega, 91637 Ver.
        val c49 = LatLng(19.50122345611109, -96.86077289608028)
        val cc49= MarkerOptions()
            .position(c49)
            .title("Ruta 1 : Parada-43")
        map.addMarker(cc49)

        //Caoba, Xalitic, 91637 Pacho Viejo, Ver.
        val c50 = LatLng(19.49835525234024, -96.85643338485156)
        val cc50= MarkerOptions()
            .position(c50)
            .title("Ruta 1 : Parada-44")
        map.addMarker(cc50)

        //Centro Deportivo Bugambilias, C. Diamante 263, 91637 Jacarandas, Ver.
        val c51 = LatLng(19.494644, -96.851495)
        val cc51= MarkerOptions()
            .position(c51)
            .title("Ruta 1 : Parada-45")
        map.addMarker(cc51)

        //C. Aguamarina 2b, 91637 Jacarandas, Ver., México
        val c52 = LatLng(19.495252, -96.850424)
        val cc52= MarkerOptions()
            .position(c52)
            .title("Ruta 1 : Parada-46")
        map.addMarker(cc52)

        //And. Zafiro 2b, Las Trancas, 91637 Pacho Viejo, Ver., México
        val c53 = LatLng(19.495774, -96.851433)
        val cc53= MarkerOptions()
            .position(c53)
            .title("Ruta 1 : Parada-47")
        map.addMarker(cc53)

        //C. Hacienda Claudina 3, 91637 Jacarandas, Ver., México
        val c54 = LatLng(19.496602, -96.852159)
        val cc54= MarkerOptions()
            .position(c54)
            .title("Ruta 1 : Parada-48")
        map.addMarker(cc54)

        //Fracc. Dalias Prolong. Bugambilias 2, 91637 Xalapa-Enríquez, Ver., México
        val c55 = LatLng(19.500343, -96.860193)
        val cc55= MarkerOptions()
            .position(c55)
            .title("Ruta 1 : Parada-49")
        map.addMarker(cc55)

        //Dr. Rafael Lucio 38, 91637 Las Trancas, Ver., México
        val c56 = LatLng(19.505906, -96.863399)
        val cc56= MarkerOptions()
            .position(c56)
            .title("Ruta 1 : Parada-50")
        map.addMarker(cc56)

        // Las Trancas 35, Santa Anita, 91190 Xalapa-Enríquez, Ver., México
        val c57 = LatLng(19.506810, -96.865633)
        val cc57= MarkerOptions()
            .position(c57)
            .title("Ruta 1 : Parada-51")
        map.addMarker(cc57)

        /*//
        val c58 = LatLng()
        val cc58= MarkerOptions()
            .position(c58)
            .title("Ruta 1 : Parada-52")
        map.addMarker(cc58)

        //
        val c59 = LatLng()
        val cc59= MarkerOptions()
            .position(c59)
            .title("Ruta 1 : Parada-53")
        map.addMarker(cc59)

        //
        val c60 = LatLng()
        val cc60= MarkerOptions()
            .position(c60)
            .title("Ruta 1 : Parada-54")
        map.addMarker(cc60)

        //
        val c61 = LatLng()
        val cc61= MarkerOptions()
            .position(c61)
            .title("Ruta 1 : Parada-55")
        map.addMarker(cc61)

        //
        val c62 = LatLng()
        val cc62= MarkerOptions()
            .position(c62)
            .title("Ruta 1 : Parada-56")
        map.addMarker(cc62)

        //
        val c63 = LatLng()
        val cc63= MarkerOptions()
            .position(c63)
            .title("Ruta 1 : Parada-57")
        map.addMarker(cc63)

        //
        val c64 = LatLng()
        val cc64= MarkerOptions()
            .position(c64)
            .title("Ruta 1 : Parada-58")
        map.addMarker(cc64)

        //
        val c65 = LatLng()
        val cc65= MarkerOptions()
            .position(c65)
            .title("Ruta 1 : Parada-59")
        map.addMarker(cc65)

        //
        val c66 = LatLng()
        val cc66= MarkerOptions()
            .position(c66)
            .title("Ruta 1 : Parada-60")
        map.addMarker(cc66)

        //
        val c67 = LatLng()
        val cc67= MarkerOptions()
            .position(c67)
            .title("Ruta 1 : Parada-61")
        map.addMarker(cc67)

        //
        val c68 = LatLng()
        val cc68= MarkerOptions()
            .position(c68)
            .title("Ruta 1 : Parada-62")
        map.addMarker(cc68)

        //
        val c69 = LatLng()
        val cc69= MarkerOptions()
            .position(c69)
            .title("Ruta 1 : Parada-63")
        map.addMarker(cc69)

        //
        val c70 = LatLng()
        val cc70= MarkerOptions()
            .position(c70)
            .title("Ruta 1 : Parada-64")
        map.addMarker(cc70)*/

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------//
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation(){
        if(!::map.isInitialized) return
        if(isLocationPermissionGranted()){
            //SI
            map.isMyLocationEnabled = true

        }else{
            //NO
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Ve a ajustees y acepta los permisos", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Para utilizar la localización, ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else->{}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if(!::map.isInitialized) return
        if(!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Para utilizar la localización, ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Dirigiendo a ubicación actual", Toast.LENGTH_SHORT).show()
        return false

    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Estas en: ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }
}