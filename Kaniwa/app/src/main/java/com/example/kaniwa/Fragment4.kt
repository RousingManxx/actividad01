package com.example.kaniwa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kaniwa.databinding.Fragment4Binding

private var _binding: Fragment4Binding? = null
private val binding get() = _binding!!
private val DURATION: Long = 2000
class Fragment4 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = Fragment4Binding.inflate(inflater, container, false)

        binding.startButton.setOnClickListener{
            cambiarActivity()
        }
        return binding.root
    }
    private fun cambiarActivity(){
        val actividad = getActivity()
        actividad?.finish()
    }
}