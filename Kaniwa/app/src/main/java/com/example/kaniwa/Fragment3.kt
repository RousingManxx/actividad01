package com.example.kaniwa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kaniwa.databinding.Fragment2Binding
import com.example.kaniwa.databinding.Fragment3Binding

private var _binding: Fragment3Binding? = null
private val binding get() = _binding!!
private val DURATION: Long = 2000
class Fragment3 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = Fragment3Binding.inflate(inflater, container, false)
        return binding.root
    }


}