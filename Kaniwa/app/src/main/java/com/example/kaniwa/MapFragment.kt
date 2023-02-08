package com.example.kaniwa

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kaniwa.databinding.ActivityTutorialBinding
import com.example.kaniwa.databinding.FragmentFavoritasBinding
import com.example.kaniwa.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener{

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    lateinit var binding2: FragmentFavoritasBinding


    //private  lateinit var binding2: FragmentFavoritasBinding
    public lateinit var map:GoogleMap
    private lateinit var mapView:MapView
    var ban = false
    val paradas = mutableListOf<Parada>()
    var parada1 = Parada("Parada1",LatLng(19.541275, -96.927288),mutableListOf("Ruta1","Ruta2"))

    companion object{
        const val REQUEST_CODE_LOCATION = 0
        lateinit var instance: MapFragment
        @JvmName("getInstance1")
        fun getInstance(): MapFragment {
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding2 = FragmentFavoritasBinding.inflate(layoutInflater)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{
        instance = this
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this);
        listenerBoton()
    }

//--------------Listener de los botones flotantes del mapa--------------------------------\\

    private fun listenerBoton(){
//        Acciones del boton de la derecha del mapa
        binding.fab.setOnClickListener{
            map.clear()
            Toast.makeText(getContext(), "Limpiando mapa", Toast.LENGTH_SHORT).show()
        }

//        Acciones del boton de la lista de rutas con checkbox a la izquierda del mapa
        binding.listaRutas.setOnClickListener{
            val selectedItems = ArrayList<Int>() // Where we track the selected items
            val builder = AlertDialog.Builder(getContext())
            val arrayLista = arrayOf("Ruta 1", "Ruta 2" , "Ruta 3", "Mostrar todas las paradas")
            val checkLista = booleanArrayOf(
                    false,
                    false,
                    false,
                    false
                    )
            val rutasLista = Arrays.asList (*arrayLista)

            Toast.makeText(getContext(), "LISTA RUTAS", Toast.LENGTH_SHORT).show()
            // Ponerle titulo a el alertBox de las rutas
            builder.setTitle("Lista de rutas")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                builder.setMultiChoiceItems(arrayLista, checkLista){dialog, which,iscCHecked ->
                    checkLista[which] =iscCHecked
                    val currentItem = rutasLista[which]
                    Toast.makeText (getContext(),currentItem+" "+ iscCHecked, Toast.LENGTH_SHORT).show()
                }
                // Acciones de los botones
                builder.setPositiveButton(R.string.ok){ dialog, id ->
                        Toast.makeText(getContext(),"La/las rutas fueron seleccionadas", Toast.LENGTH_SHORT);
                        for (i in checkLista.indices) {
                            val checked = checkLista[i]
                            if (i == 0 && checked){
                                SUX1()
                            }else if (i == 1 && checked){
                                SUX2()
                            }else if (i == 2 && checked){
                                AMARILLO()
                            }else if (i == 3 && checked){
                                getInstance().ruta1()
                                getInstance().ruta2()
                                getInstance().ruta3()
                                getInstance().ruta4()
                            }
                        }
                    }
                builder.setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                builder.setNeutralButton("+ Favorito",
                    DialogInterface.OnClickListener{ dialog, id ->
                    //Insertar codigo de favorito
                })
            val dialog =builder.create()
            dialog.show()
        }
    }

//    Metodo que inicia el fragment del mapa de la aplicacion
    override fun onMapReady(googleMap: GoogleMap?){
        if (googleMap != null) {
            map = googleMap
            zoom()
            paradasConocidasList()
            //ruta1()
            //AMARILLO()
            map.setOnMyLocationButtonClickListener(this) //Mensaje cuando se toque el boton de ubicación actual
            map.setOnMyLocationClickListener(this) //Corrdenadas de cuando se de clcick en la ubicación actual (NO el boton)
            enableLocation()
        }
    }

    public fun compararDistancia(latLng: LatLng){
        for(parada in paradas){
            if(parada.calcularDistancia(latLng) < 0.3){
                val aux = MarkerOptions().position(parada.coord).title(parada.nombre).icon(BitmapDescriptorFactory.defaultMarker(110F))
                map.addMarker(aux)
            }
        }
    }

//    Metodo que hace zoom a unas coordenadas especificas (ESCUELA FEI)
    private fun zoom(){
        val coordenadas = LatLng(19.527409, -96.921380)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 13f), 2000,null)
    }

//    Metodo que hace zoom a un lugar especifico (Algun lugar)
    public fun setZoom(latLng: LatLng){
        val coordenadas = latLng
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 17f), 2000,null)
        val ubicacion = MarkerOptions().position(coordenadas).title("Ubicación buscada")
        map.addMarker(ubicacion)
    }

//    Polilineas a mano de una ruta
    public fun ATAZ(){
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
            .color(ContextCompat.getColor(requireContext(), R.color.ATAZ))
        val polyline = map.addPolyline(polylineOptions)
        val pattern = listOf(
            Dot(), Gap(10f), Dash(50f), Gap(10f)
        )

        polyline.pattern = pattern
        polyline.startCap = RoundCap()
        polyline.endCap = RoundCap()
        //polyline.endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.dcp1))
        polyline.isClickable = true
        map.setOnPolylineClickListener { Toast.makeText(getContext(),"Ruta: ATAZ",Toast.LENGTH_SHORT).show() }

    }

//    Coordenadas de algunas paradas
    private fun paradasConocidas(){
        //Parada Fei
        val c0 = LatLng(19.542635, -96.927233)
        val economia = MarkerOptions().position(c0).title("Economía")
        map.addMarker(economia)

        //Agua Santa 1
        val c1 = LatLng(19.529693, -96.900883)
        val aguaSanta1 = MarkerOptions().position(c1).title("Agua Santa 1")
        map.addMarker(aguaSanta1)

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

    private fun paradasConocidasList(){
        //Parada Fei
        var economia = Parada("Economia",LatLng(19.542635, -96.927233),mutableListOf("Ruta1","Ruta2"))
        paradas.add(economia)
        //Agua Santa 1
        var aguaSanta1 = Parada("Agua Santa 1",LatLng(19.529693, -96.900883),mutableListOf("Ruta1","Ruta2"))
        paradas.add(aguaSanta1)
        //Agua Santa 2
        var aguaSanta2 = Parada("Agua Santa 2",LatLng(19.529690, -96.900371),mutableListOf("Ruta1","Ruta2"))
        paradas.add(aguaSanta2)
        //Doña Falla
        var parqueDonaFalla = Parada("Parque Doña Falla",LatLng(19.522595, -96.894830),mutableListOf("Ruta1","Ruta2"))
        paradas.add(parqueDonaFalla)
        //BIENESTAR
        var secretariaBienestar = Parada("Secretaria Bienestar",LatLng(19.522595, -96.894830),mutableListOf("Ruta1","Ruta2"))
        paradas.add(secretariaBienestar)
        //Parque natura
        var parqueNatura = Parada("Parque Natura",LatLng(19.518945, -96.883305),mutableListOf("Ruta1","Ruta2"))
        paradas.add(parqueNatura)
        //Plaza americas
        var plazaAmericas = Parada("Plaza Americas",LatLng(19.513259188258463, -96.8756648576462),mutableListOf("Ruta1","Ruta2"))
        paradas.add(plazaAmericas)
        //Parada frente a plaza americas
        var plazaAmericas2 = Parada("Plaza Americas 2",LatLng(19.513174, -96.875285),mutableListOf("Ruta1","Ruta2"))
        paradas.add(plazaAmericas2)
        //Plaza Animas
        var plazaAnimas = Parada("Plaza Animas",LatLng(19.518794276525053, -96.88282015160287),mutableListOf("Ruta1","Ruta2"))
        paradas.add(plazaAnimas)
        //Parada CorpusGym
        var corpusGym = Parada("CorpusGym",LatLng(19.532288, -96.905237),mutableListOf("Ruta1","Ruta2"))
        paradas.add(corpusGym)
        //Pomona
        var pomona = Parada("Pomona",LatLng(19.531994, -96.905337),mutableListOf("Ruta1","Ruta2"))
        paradas.add(pomona)
    }

//    Coordenadas de paradas de la ruta 1 de acuerdo a mapaton.org
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

        //Carr. Veracruz-Xalapa 473, Rubi Animas, 91194 Xalapa-Enríquez, Ver., México
        val c58 = LatLng(19.5122659854283, -96.8745788908467)
        val cc58= MarkerOptions()
            .position(c58)
            .title("Ruta 1 : Parada-52")
        map.addMarker(cc58)

        //Veracruz 140, Rubi Animas, 91194 Xalapa-Enríquez, Ver., México
        val c59 = LatLng(19.513638316544554, -96.87566932884106)
        val cc59= MarkerOptions()
            .position(c59)
            .title("Ruta 1 : Parada-53")
        map.addMarker(cc59)

        //Carr. Veracruz-Xalapa 16, Dos Vistas Las Animas, Lomas de Oro, 91190 Xalapa-Enríquez, Ver., México
        val c60 = LatLng(19.51845331166297, -96.8825032706483)
        val cc60= MarkerOptions()
            .position(c60)
            .title("Ruta 1 : Parada-54")
        map.addMarker(cc60)

        //G4C8+52 Xalapa-Enríquez, Veracruz, México
        val c61 = LatLng(19.520452655085364, -96.88497196717314)
        val cc61= MarkerOptions()
            .position(c61)
            .title("Ruta 1 : Parada-55")
        map.addMarker(cc61)

        //Carr. Xalapa Veracruz 191, Jardines de las Animas, 91190 Xalapa-Enríquez, Ver., México
        val c62 = LatLng(19.52196284814631, -96.89132771393463)
        val cc62= MarkerOptions()
            .position(c62)
            .title("Ruta 1 : Parada-56")
        map.addMarker(cc62)

        //Av. Lázaro Cárdenas 104, Campo Nuevo, 91198 Xalapa-Enríquez, Ver., México
        val c63 = LatLng(19.5226025474966, -96.89438420113508)
        val cc63= MarkerOptions()
            .position(c63)
            .title("Ruta 1 : Parada-57")
        map.addMarker(cc63)

        //C. Lázaro Cárdenas 449, Col. Badillo, 91190 Xalapa-Enríquez, Ver., México
        val c64 = LatLng(19.5226025474966, -96.89438420113508)
        val cc64= MarkerOptions()
            .position(c64)
            .title("Ruta 1 : Parada-58")
        map.addMarker(cc64)

        //  C. Lázaro Cárdenas 449, Col. Badillo, 91190 Xalapa-Enríquez, Ver., México
        val c65 = LatLng(19.529390242886645, -96.89984538409963)
        val cc65= MarkerOptions()
            .position(c65)
            .title("Ruta 1 : Parada-59")
        map.addMarker(cc65)

        //C. Lázaro Cárdenas 445, Encinal, 91180 Xalapa-Enríquez, Ver., México
        val c66 = LatLng(19.53238516997482, -96.9053768428033)
        val cc66= MarkerOptions()
            .position(c66)
            .title("Ruta 1 : Parada-60")
        map.addMarker(cc66)

        //C. Lázaro Cárdenas 423, Encinal, 91180 Xalapa-Enríquez, Ver., México
        val c67 = LatLng(19.532802265508142, -96.90614931895534)
        val cc67= MarkerOptions()
            .position(c67)
            .title("Ruta 1 : Parada-61")
        map.addMarker(cc67)

        //91170, Av Ferrocarril Interoceánico 146, El Mirador, 91170 Xalapa-Enríquez, Ver., México
        val c68 = LatLng(19.538928216713305, -96.90771966921983)
        val cc68= MarkerOptions()
            .position(c68)
            .title("Ruta 1 : Parada-62")
        map.addMarker(cc68)

        //Plaza Andador Encanto, C. Lázaro Cárdenas Local #3, Jardines de Xalapa, 91179 Xalapa-Enríquez, Ver., México
        val c69 = LatLng(19.542561651526785, -96.90857912159468)
        val cc69= MarkerOptions()
            .position(c69)
            .title("Ruta 1 : Parada-63")
        map.addMarker(cc69)

        //Av. Lázaro Cárdenas 560, 7 de Noviembre, 91143 Xalapa-Enríquez, Ver., México
        val c70 = LatLng(19.542561651526785, -96.90857912159468)
        val cc70= MarkerOptions()
            .position(c70)
            .title("Ruta 1 : Parada-64")
        map.addMarker(cc70)

        //Av. Lázaro Cárdenas 560, 7 de Noviembre, 91143 Xalapa-Enríquez, Ver., México
        val c71 = LatLng(19.55132773573054, -96.91289788500367)
        val cc71= MarkerOptions()
            .position(c71)
            .title("Ruta 1 : Parada-65")
        map.addMarker(cc71)

        //Av Miguel Aleman carret.Ver-Mexico, C. Lázaro Cárdenas 842, Heroes Ferrocarrileros, 91110 Xalapa-Enríquez, Ver.
        val c72 = LatLng(19.553673243979727, -96.91388495543254)
        val cc72= MarkerOptions()
            .position(c72)
            .title("Ruta 1 : Parada-66")
        map.addMarker(cc72)

        //C. Lázaro Cárdenas 443, Ferrocarrilera, 91120 Xalapa-Enríquez, Ver., México
        val c73 = LatLng(19.556549026443943, -96.91536307920053)
        val cc73= MarkerOptions()
            .position(c73)
            .title("Ruta 1 : Parada-67")
        map.addMarker(cc71)

        //C. Lázaro Cárdenas 17, Rafael Lucio, 91110 Xalapa-Enríquez, Ver., México
        val c74 = LatLng(19.558162559400614, -96.91718685461811)
        val cc74= MarkerOptions()
            .position(c74)
            .title("Ruta 1 : Parada-68")
        map.addMarker(cc74)

        //C. Lázaro Cárdenas 169, Rafael Lucio, 91110 Xalapa-Enríquez, Ver., México
        val c75 = LatLng(19.560912332768318, -96.92432693436007)
        val cc75= MarkerOptions()
            .position(c75)
            .title("Ruta 1 : Parada-69")
        map.addMarker(cc75)

        //Av. Lázaro Cárdenas Y Plaza Crital 984, Rafael Lucio, 91110 Xalapa-Enríquez, Ver., México
        val c76 = LatLng(19.563037194779845, -96.92872328233977)
        val cc76= MarkerOptions()
            .position(c76)
            .title("Ruta 1 : Parada-70")
        map.addMarker(cc76)

        //Av. Adolfo Ruiz Cortines 863, U.H. del Bosque, 91010 Xalapa-Enríquez, Ver., México
        val c77 = LatLng(19.56230884440318, -96.92962744438579)
        val cc77= MarkerOptions()
            .position(c77)
            .title("Ruta 1 : Parada-71")
        map.addMarker(cc77)

        //Estacionamiento Super ISSTE, Av. Xalapa 59, U.H. del Bosque, 91010 Xalapa-Enríquez, Ver., México
        val c78 = LatLng(19.560194228778936, -96.92885164367571)
        val cc78= MarkerOptions()
            .position(c78)
            .title("Ruta 1 : Parada-72")
        map.addMarker(cc78)

        //Av. Xalapa s/n, Unidad Magisterial, 91110 Xalapa-Enríquez, Ver.
        val c79 = LatLng(19.554046924321757, -96.92926068375091)
        val cc79= MarkerOptions()
            .position(c79)
            .title("Ruta 1 : Parada-73")
        map.addMarker(cc79)

        //Unidad Magisterial, 91017 Centro, Ver.
        val c80 = LatLng(19.551522682518808, -96.92970226278251)
        val cc80= MarkerOptions()
            .position(c80)
            .title("Ruta 1 : Parada-74")
        map.addMarker(cc80)

        //Av. Orizaba 185 Col, Obrero Campesina, 91020 Xalapa-Enríquez, Ver.
        val c81 = LatLng(19.54867019865691, -96.92988468971025)
        val cc81= MarkerOptions()
            .position(c81)
            .title("Ruta 1 : Parada-75")
        map.addMarker(cc81)

        //Av. Xalapa No. 155, Obrero Campesina, 91020 Xalapa-Enríquez, Ver., México
        val c82 = LatLng(19.546649496091927, -96.92859529341867)
        val cc82= MarkerOptions()
            .position(c82)
            .title("Ruta 1 : Parada-76")
        map.addMarker(cc82)

        //Ernesto Ortiz Medina 1, Aguacatal, 91020 Xalapa-Enríquez, Ver., México
        val c83 = LatLng(19.542882617558764, -96.92738302101789)
        val cc83= MarkerOptions()
            .position(c83)
            .title("Ruta 1 : Parada-77")
        map.addMarker(cc83)
    }

//    Coordenadas de paradas de la ruta 2 que pasa cerca de la fei
    private fun ruta2(){
        //Av. Xalapa 73, Obrero Campesina, 91020 Xalapa-Enríquez, Ver., México
        val c01 = LatLng(19.54290379391993, -96.92740439152757)
        val cc01= MarkerOptions()
            .position(c01)
            .title("Ruta 2 : Parada-01")
        map.addMarker(cc01)

        //Av. Manuel Ávila Camacho 290, Zona Centro, Centro, 91000 Xalapa-Enríquez, Ver., México
        val c02 = LatLng(19.539828304542322, -96.9269260265749)
        val cc02= MarkerOptions()
            .position(c02)
            .title("Ruta 2 : Parada-02")
        map.addMarker(cc02)

        //Av. Manuel Ávila Camacho 188, Zona Centro, Centro, 91000 Xalapa-Enríquez, Ver., México
        val c03 = LatLng(19.53606175945289, -96.93135957385441)
        val cc03= MarkerOptions()
            .position(c03)
            .title("Ruta 2 : Parada-03")
        map.addMarker(cc03)

        //C. Ignacio de la Llave 16 Altos, Represa del Carmen, 91050 Xalapa-Enríquez, Ver., México
        val c04 = LatLng(19.532099927888673, -96.93411806272037)
        val cc04= MarkerOptions()
            .position(c04)
            .title("Ruta 2 : Parada-04")
        map.addMarker(cc04)

        //Mora Beristain 281, Benito Juárez, 91070 Xalapa-Enríquez, Ver., México
        val c05 = LatLng(19.525462524243107, -96.93385099655845)
        val cc05= MarkerOptions()
            .position(c05)
            .title("Ruta 2 : Parada-05")
        map.addMarker(cc05)

        //Av Venustiano Carranza 38, Francisco I. Madero, 91070 Xalapa-Enríquez, Ver., México
        val c06 = LatLng(19.523615294710655, -96.93134390568473)
        val cc06= MarkerOptions()
            .position(c06)
            .title("Ruta 2 : Parada-06")
        map.addMarker(cc06)

        //Av Venustiano Carranza 91, Venustiano Carranza, 91080 Xalapa-Enríquez, Ver., México
        val c07 = LatLng(19.52237186133981, -96.92858534686054)
        val cc07= MarkerOptions()
            .position(c07)
            .title("Ruta 2 : Parada-07")
        map.addMarker(cc07)

        //Poeta Angel Núñez Beltrán 21, Felipe Carrillo Puerto, 91080 Xalapa-Enríquez, Ver., México
        val c08 = LatLng(19.521338249423263, -96.92640433650502)
        val cc08= MarkerOptions()
            .position(c08)
            .title("Ruta 2 : Parada-08")
        map.addMarker(cc08)

        //Av Venustiano Carranza 148, Felipe Carrillo Puerto, 91080 Xalapa-Enríquez, Ver., México
        val c09 = LatLng(19.521938142911402, -96.92603234201778)
        val cc09= MarkerOptions()
            .position(c09)
            .title("Ruta 2 : Parada-09")
        map.addMarker(cc09)

        // Av Venustiano Carranza 310, Felipe Carrillo Puerto, 91080 Xalapa-Enríquez, Ver., México
        val c10 = LatLng(19.518906749935997, -96.92243557772092)
        val cc10= MarkerOptions()
            .position(c10)
            .title("Ruta 2 : Parada-10")
        map.addMarker(cc10)

        //Universitario Gonzalo Aguirre Beltrán 1136, Zona Universitaria, 91090 Xalapa-Enríquez, Ver., México
        val c11 = LatLng(19.51878101312714, -96.92122153297707)
        val cc11= MarkerOptions()
            .position(c11)
            .title("Ruta 2 : Parada-11")
        map.addMarker(cc11)

        //Universitario Gonzalo Aguirre Beltrán 430, Zona Universitaria, 91090 Xalapa-Enríquez, Ver., México
        val c12 = LatLng(19.519098971635835, -96.91810309368206)
        val cc12= MarkerOptions()
            .position(c12)
            .title("Ruta 2 : Parada-12")
        map.addMarker(cc12)

        //Calz. Salvador Díaz Mirón 50, Lomas del Estadio, 91090 Xalapa-Enríquez, Ver., México
        val c13 = LatLng(19.5207059851168, -96.91841076110235)
        val cc13= MarkerOptions()
            .position(c13)
            .title("Ruta 2 : Parada-13")
        map.addMarker(cc13)

        //C. Salvador Díaz Mirón 31, Zona Centro, Centro, 91000 Xalapa-Enríquez, Ver.
        val c14 = LatLng(19.52154522539265, -96.91779147015583)
        val cc14= MarkerOptions()
            .position(c14)
            .title("Ruta 2 : Parada-14")
        map.addMarker(cc14)

        //Avenida Enrique C. Rébsamen 24 Zona Centro, Obreros Textiles, 91000 Xalapa-Enríquez, Ver.
        val c15 = LatLng(19.521879177914283, -96.91654672623675)
        val cc15= MarkerOptions()
            .position(c15)
            .title("Ruta 2 : Parada-15")
        map.addMarker(cc15)

        //Av. Enrique C. Rébsamen 234, Martires de Chicago, 91090 Xalapa-Enríquez, Ver.
        val c16 = LatLng(19.51454178153587, -96.90990458212111)
        val cc16= MarkerOptions()
            .position(c16)
            .title("Ruta 2 : Parada-16")
        map.addMarker(cc16)

        //Av. Enrique C. Rébsamen, Martires de Chicago, 91090 Xalapa-Enríquez, Ver.
        val c17 = LatLng(19.512318791944402, -96.90628595211429)
        val cc17= MarkerOptions()
            .position(c17)
            .title("Ruta 2 : Parada-17")
        map.addMarker(cc17)

        //Xalapa 2000, 91097 Xalapa-Enríquez, Ver.
        val c18 = LatLng(19.512443103228783, -96.90122466401093)
        val cc18= MarkerOptions()
            .position(c18)
            .title("Ruta 2 : Parada-18")
        map.addMarker(cc18)

        //Av. Enrique C. Rébsamen 645, Nuevo Xalapa, 91097 Xalapa-Enríquez, Ver.
        val c19 = LatLng(19.508260773761386, -96.90166021846613)
        val cc19= MarkerOptions()
            .position(c19)
            .title("Ruta 2 : Parada-19")
        map.addMarker(cc19)

        //Arco Vial Sur 728, Lomas Verdes, 91097 Xalapa-Enríquez, Ver.
        val c20 = LatLng(19.505762224772223, -96.89010890336999)
        val cc20= MarkerOptions()
            .position(c20)
            .title("Ruta 2 : Parada-20")
        map.addMarker(cc20)

        //Lomas Verdes, Agua Santa II, 91098 Xalapa-Enríquez, Ver.
        val c21 = LatLng(19.506092002047616, -96.88839304912568)
        val cc21= MarkerOptions()
            .position(c21)
            .title("Ruta 2 : Parada-21")
        map.addMarker(cc21)

        //Circuito Rafael Guízar y Valencia # 626 Planta Baja Locales 1,2 y 3, Reserva Territorial, 91223 Xalapa-Enríquez, Ver.
        val c22 = LatLng(19.507304437081782, -96.88173511246045)
        val cc22= MarkerOptions()
            .position(c22)
            .title("Ruta 2 : Parada-22")
        map.addMarker(cc22)

        //Circuito Rafael Guízar y Valencia # 626 Planta Baja Locales 1,2 y 3, Reserva Territorial, Xalapa-Enríquez, Ver.
        val c23 = LatLng(19.507097033842722, -96.8797624398613)
        val cc23= MarkerOptions()
            .position(c23)
            .title("Ruta 2 : Parada-23")
        map.addMarker(cc23)

        //Xallitic 2, Moctezuma, 91096 Xalapa-Enríquez, Ver.
        val c24 = LatLng(19.502975127459674, -96.87818766950772)
        val cc24= MarkerOptions()
            .position(c24)
            .title("Ruta 2 : Parada-24")
        map.addMarker(cc24)

        //Calz. del Tecnológico 8, Reserva Territorial, 91098 Pacho Viejo, Ver.
        val c25 = LatLng(19.501057569527134, -96.8792256160391)
        val cc25= MarkerOptions()
            .position(c25)
            .title("Ruta 2 : Parada-25")
        map.addMarker(cc25)

        //Calle Cto. Unidad 25 Pastoresa Pacho Viejo, Pastoresa, 91223 Pacho Viejo, Ver.
        val c26 = LatLng(19.506163899849223, -96.87774689239974)
        val cc26= MarkerOptions()
            .position(c26)
            .title("Ruta 2 : Parada-26")
        map.addMarker(cc26)

        //Av. principal hacia las Trancas #1 Enfrente de farmacia del ahorro Col, El Olmo, 91194 Xalapa-Enríquez, Ver.
        val c27 = LatLng(19.50743620552431, -96.86943855062536)
        val cc27= MarkerOptions()
            .position(c27)
            .title("Ruta 2 : Parada-27")
        map.addMarker(cc27)

        //Rafael Fuentes Boettinger 3, Rubi Animas, 91193 Xalapa-Enríquez, Ver.
        val c28 = LatLng(19.51314695397489, -96.87522865163342)
        val cc28= MarkerOptions()
            .position(c28)
            .title("Ruta 2 : Parada-28")
        map.addMarker(cc28)

        //Carretera México-Veracruz 560, Carr. Xalapa Veracruz, Pastoresa Av. Salvador Díaz Mirón 1010, Centro, Veracruz, Centro, 91193 Xalapa-Enríquez, Ver.
        val c29 = LatLng(19.51550332466404, -96.879313107558)
        val cc29= MarkerOptions()
            .position(c29)
            .title("Ruta 2 : Parada-29")
        map.addMarker(cc29)

        //91193, Carr. Xalapa Veracruz 225, Campo Nuevo, 91193 Xalapa-Enríquez, Ver.
        val c30 = LatLng(19.520602236912644, -96.88551634159865)
        val cc30= MarkerOptions()
            .position(c30)
            .title("Ruta 2 : Parada-30")
        map.addMarker(cc30)

        //Carretera Xalapa-Veracruz, Km 0+700, Col, Indeco Animas, 91190 Enríquez, Ver.
        val c31 = LatLng(19.52294717243575, -96.89481483361834)
        val cc31= MarkerOptions()
            .position(c31)
            .title("Ruta 2 : Parada-31")
        map.addMarker(cc31)

        //C. Lázaro Cárdenas 198, Encinal, 91180 Xalapa-Enríquez, Ver.
        val c32 = LatLng(19.53198588827804, -96.9047102898112)
        val cc32= MarkerOptions()
            .position(c32)
            .title("Ruta 2 : Parada-32")
        map.addMarker(cc32)

        //Pl. Crystal Local 23 D, Encinal, 91180 Xalapa-Enríquez, Ver.
        val c33 = LatLng(19.533320047469324, -96.90693392271301)
        val cc33= MarkerOptions()
            .position(c33)
            .title("Ruta 2 : Parada-33")
        map.addMarker(cc33)

        //C. Lázaro Cárdenas 402, Francisco Villa, 91180 Xalapa-Enríquez, Ver.
        val c34 = LatLng(19.535435502086912, -96.9077532216284)
        val cc34= MarkerOptions()
            .position(c34)
            .title("Ruta 2 : Parada-34")
        map.addMarker(cc34)

        //C. Lázaro Cárdenas 373, El Mirador, 91170 Xalapa-Enríquez, Ver.
        val c35 = LatLng(19.53888003851993, -96.9077189243112)
        val cc35= MarkerOptions()
            .position(c35)
            .title("Ruta 2 : Parada-35")
        map.addMarker(cc35)

        //Av Miguel Alemán 204A, Miguel Hidalgo, 91140 Xalapa-Enríquez, Ver.
        val c36 = LatLng(19.55064025781004, -96.91239684099077)
        val cc36= MarkerOptions()
            .position(c36)
            .title("Ruta 2 : Parada-36")
        map.addMarker(cc36)

        //C. Lázaro Cárdenas 690, Ferrocarrilera, 91120 Xalapa-Enríquez, Ver.
        val c37 = LatLng(19.55442081818692, -96.91416897901088)
        val cc37= MarkerOptions()
            .position(c37)
            .title("Ruta 2 : Parada-37")
        map.addMarker(cc37)

        //Lázaro Cádenas 796, Rafael Lucio, 91110 Xalapa-Enríquez, Ver.
        val c38 = LatLng(19.55838315310607, -96.9176216929931)
        val cc38= MarkerOptions()
            .position(c38)
            .title("Ruta 2 : Parada-38")
        map.addMarker(cc38)

        //C. Lázaro Cárdenas 862, Rafael Lucio, 91110 Xalapa-Enríquez, Ver.
        val c39 = LatLng(19.559873695774744, -96.9217104156258)
        val cc39= MarkerOptions()
            .position(c39)
            .title("Ruta 2 : Parada-39")
        map.addMarker(cc39)

        //Av. Adolfo Ruiz Cortines 939, U.H. del Bosque, 91010 Xalapa-Enríquez, Ver.
        val c40 = LatLng(19.562572888879828, -96.92967584948698)
        val cc40= MarkerOptions()
            .position(c40)
            .title("Ruta 2 : Parada-40")
        map.addMarker(cc40)

        //Av. Adolfo Ruiz Cortines 942, U.H. del Bosque, 91017 Xalapa-Enríquez, Ver.
        val c41 = LatLng(19.562361380242617, -96.92949627712534)
        val cc41= MarkerOptions()
            .position(c41)
            .title("Ruta 2 : Parada-41")
        map.addMarker(cc41)

        //Av. Xalapa 205, U.H. del Bosque, 91010 Xalapa-Enríquez, Ver.
        val c42 = LatLng(19.55976289130537, -96.92881294479012)
        val cc42= MarkerOptions()
            .position(c42)
            .title("Ruta 2 : Parada-42")
        map.addMarker(cc42)

        //Arcos del Panteón 389, Rafael Lucio, 91130 Xalapa-Enríquez, Ver.
        val c43 = LatLng(19.55900144561089, -96.92885222625053)
        val cc43= MarkerOptions()
            .position(c43)
            .title("Ruta 2 : Parada-43")
        map.addMarker(cc43)

        //Av. Xalapa s/n, Unidad Magisterial, 91110 Xalapa-Enríquez, Ver.
        val c44 = LatLng(19.554059782921154, -96.92931023090672)
        val cc44= MarkerOptions()
            .position(c44)
            .title("Ruta 2 : Parada-44")
        map.addMarker(cc44)

        //Av. Xalapa s/n, Unidad Magisterial, 91017 Xalapa-Enríquez, Ver.
        val c45 = LatLng(19.55165216872238, -96.92977013948234)
        val cc45= MarkerOptions()
            .position(c45)
            .title("Ruta 2 : Parada-45")
        map.addMarker(cc45)

        //Av. Xalapa 630, Obrero Campesina, 91020 Xalapa-Enríquez, Ver.
        val c46 = LatLng(19.54845537004497, -96.92986779545839)
        val cc46= MarkerOptions()
            .position(c46)
            .title("Ruta 2 : Parada-46")
        map.addMarker(cc46)

        //Av. Xalapa 201, Obrero Campesina, 91020 Xalapa-Enríquez, Ver.
        val c47 = LatLng(19.543248788869555, -96.92756606074998)
        val cc47= MarkerOptions()
            .position(c47)
            .title("Ruta 2 : Parada-47")
        map.addMarker(cc47)

    }

//    Coordenadas de paradas de la ruta 3 que pasa cerca de la fei
    private fun ruta3(){
        //Av. Xalapa 226, Col del Maestro, 91030 Xalapa-Enríquez, Ver.
        val c01 = LatLng(19.541775999298387, -96.92675775577904)
        val cc01= MarkerOptions()
            .position(c01)
            .title("Ruta 3 : Parada-01")
        map.addMarker(cc01)

        //Av. 20 de Noviembre, Zona Centro, Centro, 91000 Xalapa-Enríquez, Ver.
        val c02 = LatLng(19.536563886710823, -96.92065711069382)
        val cc02= MarkerOptions()
            .position(c02)
            .title("Ruta 3 : Parada-02")
        map.addMarker(cc02)

        //Calle Revolución 64, Zona Centro, Rafael Lucio, 91110 Xalapa-Enríquez, Ver.
        val c03 = LatLng(19.53548983682819, -96.91756488470762)
        val cc03= MarkerOptions()
            .position(c03)
            .title("Ruta 3 : Parada-03")
        map.addMarker(cc03)

        //Av. 20 de Noviembre, Justo Sierra esquina, Centro, 91000 Xalapa-Enríquez, Ver.
        val c04 = LatLng(19.531794141544797, -96.91443742900262)
        val cc04= MarkerOptions()
            .position(c04)
            .title("Ruta 3 : Parada-04")
        map.addMarker(cc04)

        //Av. 20 de Noviembre 384, Los Ángeles, 91060 Xalapa-Enríquez, Ver.
        val c05 = LatLng(19.527583912062266, -96.91103260201788)
        val cc05= MarkerOptions()
            .position(c05)
            .title("Ruta 3 : Parada-05")
        map.addMarker(cc05)

        //Av. 20 de Noviembre 444, Los Ángeles, 91060 Xalapa-Enríquez, Ver.
        val c06 = LatLng(19.52596580742933, -96.9086539733961)
        val cc06= MarkerOptions()
            .position(c06)
            .title("Ruta 3 : Parada-06")
        map.addMarker(cc06)

        //INFONAVIT Pomona, 91040 Xalapa-Enríquez, Ver.
        val c07 = LatLng(19.52561789968329, -96.90718528194215)
        val cc07= MarkerOptions()
            .position(c07)
            .title("Ruta 3 : Parada-07")
        map.addMarker(cc07)

        //Carr. Veracruz-Xalapa 357, Jardines de las Animas, 91190 Xalapa-Enríquez, Ver.
        val c08 = LatLng(19.520374192955483, -96.88554897306102)
        val cc08= MarkerOptions()
            .position(c08)
            .title("Ruta 3 : Parada-08")
        map.addMarker(cc08)

        //Carr. Veracruz-Xalapa 357, Jardines de las Animas, 91190 Xalapa-Enríquez, Ver.
        val c09 = LatLng(19.520437255364286, -96.88487033355653)
        val cc09= MarkerOptions()
            .position(c09)
            .title("Ruta 3 : Parada-09")
        map.addMarker(cc09)

        //Carr. Veracruz-Xalapa 412, Rubi Animas, 91194 Xalapa-Enríquez, Ver.
        val c10 = LatLng(19.515767586946392, -96.87979722082032)
        val cc10= MarkerOptions()
            .position(c09)
            .title("Ruta 3 : Parada-10")
        map.addMarker(cc10)
    }

//    Coordenadas de paradas de la ruta 4 que pasa cerca de la fei
    private fun ruta4(){
        //Av. Xalapa s/n, Obrero Campesina, 91020 Xalapa-Enríquez, Ver.
        val c01 = LatLng(19.541790245494738, -96.92673453783364)
        val cc01= MarkerOptions()
            .position(c01)
            .title("Ruta 4 : Parada-01")
        map.addMarker(cc01)

        //Col del Maestro, 91030 Xalapa-Enríquez, Ver.
        val c02 = LatLng(19.539856768302176, -96.92337628973101)
        val cc02= MarkerOptions()
            .position(c02)
            .title("Ruta 4 : Parada-02")
        map.addMarker(cc02)

        //Mercado la Rotonda, C. Revolución 11, Col del Maestro, 91000 Xalapa-Enríquez, Ver.
        val c03 = LatLng(19.540370268564185, -96.92250892767223)
        val cc03= MarkerOptions()
            .position(c03)
            .title("Ruta 4 : Parada-03")
        map.addMarker(cc03)

        //C. Revolución 330, Col del Maestro, 91030 Xalapa-Enríquez, Ver.
        val c04 = LatLng(19.53962621663285, -96.92309272905796)
        val cc04= MarkerOptions()
            .position(c04)
            .title("Ruta 4 : Parada-04")
        map.addMarker(cc04)

        //Av. 20 de Noviembre Oriente 75, Centro, 91000 Xalapa-Enríquez, Ver.
        val c05 = LatLng(19.5366742206323, -96.9207634916421)
        val cc05= MarkerOptions()
            .position(c05)
            .title("Ruta 4 : Parada-05")
        map.addMarker(cc05)

        //Av. 20 de Noviembre Ote 156-int 3, Zona Centro, Centro, 91000 Xalapa-Enríquez, Ver.
        val c06 = LatLng(19.53584107274034, -96.91827260563167)
        val cc06= MarkerOptions()
            .position(c06)
            .title("Ruta 4 : Parada-06")
        map.addMarker(cc06)

        //Av. 20 de Noviembre 256, Zona Centro, Centro, 91000 Xalapa-Enríquez, Ver.
        val c07 = LatLng(19.531567581970524, -96.91432641796408)
        val cc07= MarkerOptions()
            .position(c07)
            .title("Ruta 4 : Parada-07")
        map.addMarker(cc07)

        //Av. 20 de Noviembre Oriente 400, Los Ángeles, 91000 Xalapa-Enríquez, Ver.
        val c08 = LatLng(19.526953398846892, -96.91005883862245)
        val cc08= MarkerOptions()
            .position(c08)
            .title("Ruta 4 : Parada-08")
        map.addMarker(cc08)

        //Av. 20 de Noviembre 583, Col. Badillo, 91190 Xalapa-Enríquez, Ver.
        val c09 = LatLng(19.525403419149207, -96.90277525048245)
        val cc09= MarkerOptions()
            .position(c09)
            .title("Ruta 4 : Parada-09")
        map.addMarker(cc09)

        //Av. 20 de Noviembre, Alvaro Obregon, 91060 Xalapa-Enríquez, Ver.
        val c10 = LatLng(19.525655832035156, -96.89852370056381)
        val cc10= MarkerOptions()
            .position(c10)
            .title("Ruta 4 : Parada-10")
        map.addMarker(cc10)

        //Av. Lázaro Cárdenas #4107 Col. Sipeh Ánimas Alvaro Obregon, 91190 Xalapa-Enríquez, Ver.
        val c11 = LatLng(19.524803936961746, -96.89675780089756)
        val cc11= MarkerOptions()
            .position(c11)
            .title("Ruta 4 : Parada-11")
        map.addMarker(cc11)

    }

//    Ruta 5 en construccion
    /*private fun ruta5(){
    //Culturas Veracruzanas 120, Reserva Territorial, 91096 Xalapa-Enríquez
    val c01 = LatLng(19.508349182109, -96.87418855761308)
    val cc01= MarkerOptions()
        .position(c01)
        .title("Ruta 5 : Parada-01")
    map.addMarker(cc01)

    //Plaza Patio, Arco Sur 128, Lomas Verdes, 91098 Xalapa-Enríquez, Ver.
    val c02 = LatLng(19.50768644226826, -96.88089754773428)
    val cc02= MarkerOptions()
        .position(c02)
        .title("Ruta 5 : Parada-02")
    map.addMarker(cc02)

    //Av. Arco vial Sur No. 109, 91096 Xalapa-Enríquez, Ver.
    val c03 = LatLng(19.506810075725408, -96.88608300444723)
    val cc03= MarkerOptions()
        .position(c03)
        .title("Ruta 5 : Parada-03")
    map.addMarker(cc03)

    //
    val c04 = LatLng()
    val cc04= MarkerOptions()
        .position(c04)
        .title("Ruta 5 : Parada-04")
    map.addMarker(cc04)

    //
    val c05 = LatLng()
    val cc05= MarkerOptions()
        .position(c05)
        .title("Ruta 5 : Parada-05")
    map.addMarker(cc05)

    //
    val c06 = LatLng()
    val cc06= MarkerOptions()
        .position(c06)
        .title("Ruta 5 : Parada-06")
    map.addMarker(cc06)

    //
    val c07 = LatLng()
    val cc07= MarkerOptions()
        .position(c07)
        .title("Ruta 5 : Parada-07")
    map.addMarker(cc07)

    //
    val c08 = LatLng()
    val cc08= MarkerOptions()
        .position(c08)
        .title("Ruta 5 : Parada-08")
    map.addMarker(cc08)

    //
    val c09 = LatLng()
    val cc09= MarkerOptions()
        .position(c09)
        .title("Ruta 5 : Parada-09")
    map.addMarker(cc09)

    //
    val c10 = LatLng()
    val cc10= MarkerOptions()
        .position(c10)
        .title("Ruta 5 : Parada-10")
    map.addMarker(cc10)

    //
    val c11 = LatLng()
    val cc11= MarkerOptions()
        .position(c11)
        .title("Ruta 5 : Parada-11")
    map.addMarker(cc11)

    //
    val c12 = LatLng()
    val cc12= MarkerOptions()
        .position(c12)
        .title("Ruta 5 : Parada-12")
    map.addMarker(cc12)

    //
    val c13 = LatLng()
    val cc13= MarkerOptions()
        .position(c13)
        .title("Ruta 5 : Parada-13")
    map.addMarker(cc13)

    //
    val c14 = LatLng()
    val cc14= MarkerOptions()
        .position(c14)
        .title("Ruta 5 : Parada-14")
    map.addMarker(cc14)

    //
    val c15 = LatLng()
    val cc15= MarkerOptions()
        .position(c15)
        .title("Ruta 5 : Parada-15")
    map.addMarker(cc15)

    //
    val c16 = LatLng()
    val cc16= MarkerOptions()
        .position(c16)
        .title("Ruta 5 : Parada-16")
    map.addMarker(cc16)

    //
    val c17 = LatLng()
    val cc17= MarkerOptions()
        .position(c17)
        .title("Ruta 5 : Parada-17")
    map.addMarker(cc17)

    //
    val c18 = LatLng()
    val cc18= MarkerOptions()
        .position(c18)
        .title("Ruta 5 : Parada-18")
    map.addMarker(cc18)

    //
    val c19 = LatLng()
    val cc19= MarkerOptions()
        .position(c19)
        .title("Ruta 5 : Parada-19")
    map.addMarker(cc19)

    //
    val c20 = LatLng()
    val cc20= MarkerOptions()
        .position(c20)
        .title("Ruta 5 : Parada-20")
    map.addMarker(cc20)

    //
    val c2 = LatLng()
    val cc2= MarkerOptions()
        .position(c2)
        .title("Ruta 5 : Parada-2")
    map.addMarker(cc2)

    //
    val c2 = LatLng()
    val cc2= MarkerOptions()
        .position(c2)
        .title("Ruta 5 : Parada-2")
    map.addMarker(cc2)

    //
    val c2 = LatLng()
    val cc2= MarkerOptions()
        .position(c2)
        .title("Ruta 5 : Parada-2")
    map.addMarker(cc2)

    //
    val c2 = LatLng()
    val cc2= MarkerOptions()
        .position(c2)
        .title("Ruta 5 : Parada-2")
    map.addMarker(cc2)

    //
    val c2 = LatLng()
    val cc2= MarkerOptions()
        .position(c2)
        .title("Ruta 5 : Parada-2")
    map.addMarker(cc2)

    //
    val c2 = LatLng()
    val cc2= MarkerOptions()
        .position(c2)
        .title("Ruta 5 : Parada-2")
    map.addMarker(cc2)

    //
    val c2 = LatLng()
    val cc2= MarkerOptions()
        .position(c2)
        .title("Ruta 5 : Parada-2")
    map.addMarker(cc2)

    //
    val c2 = LatLng()
    val cc2= MarkerOptions()
        .position(c2)
        .title("Ruta 5 : Parada-2")
    map.addMarker(cc2)

    //
    val c2 = LatLng()
    val cc2= MarkerOptions()
        .position(c2)
        .title("Ruta 5 : Parada-2")
    map.addMarker(cc2)
}*/

//    Metodo que permite ver si se tienen los permisos de locaclizacion
private fun isLocationPermissionGranted() = getContext()?.let {
    ActivityCompat.checkSelfPermission(
        it, Manifest.permission.ACCESS_FINE_LOCATION)
} == PackageManager.PERMISSION_GRANTED

//    Metodo que permite ver la ubicacion en tiempo real
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

//    Metodo que solicita acceso a la localizacion
private fun requestLocationPermission(){
    if(ActivityCompat.shouldShowRequestPermissionRationale(getContext() as Activity, Manifest.permission.ACCESS_FINE_LOCATION)){
        Toast.makeText(getContext(), "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
    }else{
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Maps.REQUEST_CODE_LOCATION)
    }
}

//    ??
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when(requestCode){
        Maps.REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            map.isMyLocationEnabled = true
        }else{
            Toast.makeText(getContext(), "Para utilizar la localización, ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
        else->{}
    }
}

//    Accion de darle al boton de mi localizacion
override fun onMyLocationButtonClick(): Boolean {
    Toast.makeText(getContext(), "Dirigiendo a ubicación actual", Toast.LENGTH_SHORT).show()
    return false
}

//    Metodo que dice las coordenadas de la ubicacion actual
override fun onMyLocationClick(p0: Location) {
    Toast.makeText(getContext(), "Estas en: ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
}

//    Ruta creada con api creadora de rutas
private fun AMARILLO(){
    createRoute("-96.875261,19.513131", "-96.928171,19.563536", R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.928171,19.563536","-96.929721,19.562556", R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.929721,19.562556","-96.92892458917628,19.56218658134051", R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.92892458917628,19.56218658134051","-96.929364,19.547755", R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.929364,19.547755","-96.926199,19.540880", R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.926199,19.540880","-96.934203,19.525623",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.934203,19.525623","-96.932042,19.524095",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.932042,19.524095","-96.926272, 19.521492",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.926272, 19.521492","-96.925413,19.523156",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.925413,19.523156","-96.919231 ,19.518609",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.919231 ,19.518609","-96.917973,19.519238",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.917973,19.519238","-96.916871,19.522509",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.916871,19.522509","-96.905654,19.512380",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.905654,19.512380","-96.902502,19.512607",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.902502,19.512607","-96.900389,19.508830",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.900389,19.508830","-96.902025,19.508765",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.902025,19.508765","-96.901477,19.508223",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.901477,19.508223","-96.891360,19.505620",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.891360,19.505620","-96.890580,19.504534",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.890580,19.504534","-96.890537,19.504739",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.890537,19.504739","-96.890722,19.505743",R.color.AMARILLO, "Ruta: Amarillo")
    createRoute("-96.890722,19.505743","-96.875261,19.513131",R.color.AMARILLO, "Ruta: Amarillo")
    //createRoute("-96.890722,19.505743","-96.878139,19.503100",R.color.AMARILLO, "Ruta: Amarillo")
    //createRoute("-96.878139,19.503100","-96.878066,19.503253",R.color.AMARILLO, "Ruta: Amarillo")
    //createRoute("-96.878066,19.503253","-96.878340,19.506112",R.color.AMARILLO, "Ruta: Amarillo")
    //createRoute("-96.878340,19.506112","-96.868707,19.508221",R.color.AMARILLO, "Ruta: Amarillo")
    //createRoute("-96.868707,19.508221","-96.875261,19.513131",R.color.AMARILLO, "Ruta: Amarillo")

    //createRoute("","",R.color.AMARILLO, "")

    /*
    val polylineOptions = PolylineOptions()
        //.add(LatLng())
        .width(15f)
        .color(ContextCompat.getColor(requireContext(), R.color.ATAZ))
    val polyline = map.addPolyline(polylineOptions)
    val pattern = listOf(
        Dot(), Gap(10f), Dash(50f), Gap(10f)
    )

    polyline.pattern = pattern
    polyline.startCap = RoundCap()
    polyline.endCap = RoundCap()
    //polyline.endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.dcp1))
    polyline.isClickable = true
    map.setOnPolylineClickListener { Toast.makeText(getContext(),"Ruta: ATAZ",Toast.LENGTH_SHORT).show() }
    */

}

//   Ruta del SUX de 20 de noviembre //// Ruta creada con api creadora de rutas
private fun SUX1(){
    createRoute("-96.927010,19.542316","-96.909697,19.526759",R.color.SUX1, "Ruta: SUX 20 de Nov")
    createRoute("-96.909697,19.526759","-96.906752,19.525588",R.color.SUX2, "Ruta: SUX 20 de Nov")
    createRoute("-96.906752,19.525588","-96.875525,19.512891",R.color.SUX2, "Ruta: SUX 20 de Nov")
    createRoute("-96.875525,19.512891","-96.856217,19.507713",R.color.SUX1, "Ruta: SUX 20 de Nov")
    createRoute("-96.856217,19.507713","-96.858375,19.513317",R.color.SUX1, "Ruta: SUX 20 de Nov")
    createRoute("-96.858375,19.513317","-96.875258,19.513161",R.color.SUX2, "Ruta: SUX 20 de Nov")
    createRoute("-96.875258,19.513161","-96.909639,19.526767",R.color.SUX1, "Ruta: SUX 20 de Nov")
    createRoute("-96.909639,19.526767","-96.926305,19.541471",R.color.SUX2, "Ruta: SUX 20 de Nov")
    createRoute("-96.926305,19.541471","-96.930085,19.562400",R.color.SUX2, "Ruta: SUX 20 de Nov")
    createRoute("-96.930085,19.562400","-96.927010,19.542316",R.color.SUX1, "Ruta: SUX 20 de Nov")
}

public fun SUX2(){
    createRoute("-96.926271,19.540507","-96.932883,19.532421",R.color.SUX1, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.932883,19.532421","-96.920064,19.525970",R.color.SUX1, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.920064,19.525970","-96.916445,19.524522",R.color.SUX1, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.916445,19.524522","-96.905822,19.525459",R.color.SUX1, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.905822,19.525459","-96.861405,19.503760",R.color.SUX1, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.861405,19.503760","-96.869164,19.508822",R.color.SUX2, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.869164,19.508822","-96.905632,19.525569",R.color.SUX2, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.905632,19.525569","-96.928986,19.528720",R.color.SUX2, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.928986,19.528720","-96.930382,19.536761",R.color.SUX2, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.930382,19.536761","-96.927027,19.543042",R.color.SUX2, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.927027,19.543042","-96.929829,19.562514",R.color.SUX2, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.929829,19.562514","-96.936458,19.549392",R.color.SUX1, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.936458,19.549392","-96.928960,19.561946",R.color.SUX1, "Ruta: SUX Av. Avila Camacho")
    createRoute("-96.928960,19.561946","-96.926271,19.540507",R.color.SUX1, "Ruta: SUX Av. Avila Camacho")

    //createRoute("","",R.color.SUX1, "Ruta: SUX Av. Avila Camacho")

}

//Funcion retrofit (api creadora de rutas)
private fun createRoute(start:String, end:String, color:Int, msg: String){
    CoroutineScope(Dispatchers.IO).launch {
        val call = getRetrofit().create(ApiService::class.java)
            .getRoute("5b3ce3597851110001cf624838aa5637335c4c80a982d049c947aa76", start, end)
        if(call.isSuccessful){
            Log.i("Funciona", "Si jalo")
            drawRoute(call.body(), color, msg)
        }else{
            Log.i("No funciona", "KO")

        }
    }
}

//    Parte de retrofit
private fun drawRoute(routeResponse: RouteResponse?, color:Int, msg:String) {
    val polylineOptions = PolylineOptions()
    routeResponse?.features?.first()?.geometry?.coordinates?.forEach{
        polylineOptions.add(LatLng(it[1],it[0]))
    }
    runOnUiThread{
        polylineOptions.width(15f).color(ContextCompat.getColor(requireContext(), color))
        val poly = map.addPolyline(polylineOptions)
        poly.isClickable = true
        map.setOnPolylineClickListener { Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show() }
    }
}

//    Parte de retrofit
fun Fragment?.runOnUiThread(action:()-> Unit){
    this?:return
    if(!isAdded) return
    activity?.runOnUiThread(action)
}

//    Parte de retrofit
private fun getRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://api.openrouteservice.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
}
