package com.example.kaniwa

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.kaniwa.databinding.Fragment2Binding


class Fragment2 : Fragment() {
  private var _binding: Fragment2Binding? = null
    private val binding get() = _binding!!
    private val DURATION: Long = 2000
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_2, container, false)
         _binding = Fragment2Binding.inflate(inflater, container, false)

       /* binding.next.setOnClickListener{
            cambiarActivity()
        }*/
        return binding.root

    }


    /*private fun cambiarActivity(){
        val actividad = getActivity()
        actividad?.finish()
    }*/
}