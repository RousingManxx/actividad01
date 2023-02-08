package com.example.kaniwa

import com.google.android.gms.maps.model.LatLng

class Parada(nombre:String,coord:LatLng,rutas:MutableList<String>){
    var nombre: String = nombre
    var coord: LatLng = coord
    var rutas: MutableList<String> = rutas

    public fun verRutas(){
        for(ruta in rutas){
            println(ruta)
        }
    }

    public fun coordenadas():LatLng{
        return coord
    }

    public fun comprobarRuta(nombreRuta:String):Boolean{
        var bandera:Boolean = false
        for(ruta in rutas){
            if(ruta == nombreRuta) {
                bandera = true
                println("La ruta pasa por esta parada")
            }
        }
        return bandera
    }

    public fun calcularDistancia(latLng: LatLng):Double{
        val radio = 6371
        var difLat = Math.toRadians(latLng.latitude - coord.latitude)
        var difLon = Math.toRadians(latLng.longitude - coord.longitude)
        val oriLat = Math.toRadians(coord.latitude)
        val desLat = Math.toRadians(latLng.latitude)
        val a = Math.pow(Math.sin(difLat/2), 2.toDouble()) + Math.pow(Math.sin(difLon/2),2.toDouble()) * Math.cos(oriLat) * Math.cos(desLat);
        val c = 2*Math.asin(Math.sqrt(a));
        return radio*c;
    }
}