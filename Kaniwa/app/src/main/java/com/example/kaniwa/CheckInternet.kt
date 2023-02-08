package com.example.kaniwa

import android.content.Context
import android.net.ConnectivityManager

class CheckInternet(){
    fun getNetworkInfo(context: Context): String? {
        var status: String? = null
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val ni = cm?.activeNetwork
        return if (ni!=null){
            status = "Conectado"
            status
        } else {
            status = "Desconectado"
            status
        }
    }
}