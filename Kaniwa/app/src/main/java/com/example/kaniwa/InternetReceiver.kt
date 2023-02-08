package com.example.kaniwa

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class InternetReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val status = CheckInternet()
        if (context?.let { status.getNetworkInfo(it).equals("Conectado") } == true) {
            Toast.makeText(context, "Estás conectado a internet", Toast.LENGTH_SHORT).show()
        } else if (context?.let { status?.getNetworkInfo(it).equals("Desconectado") } == true){
            Toast.makeText(context, "Estas Desconectado :(", Toast.LENGTH_SHORT).show()
        }
    }
}