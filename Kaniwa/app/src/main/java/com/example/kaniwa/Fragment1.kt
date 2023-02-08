package com.example.kaniwa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kaniwa.databinding.Fragment1Binding
import com.example.kaniwa.databinding.FragmentMapBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class Fragment1 : Fragment() {

    private var _binding: Fragment1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_1, container, false)
        _binding = Fragment1Binding.inflate(inflater, container, false)
        return binding.root
    }


}