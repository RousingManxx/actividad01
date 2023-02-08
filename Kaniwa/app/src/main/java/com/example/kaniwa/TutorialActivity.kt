package com.example.kaniwa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.kaniwa.databinding.ActivityTutorialBinding
import com.example.kaniwa.databinding.Fragment2Binding
import com.google.android.material.tabs.TabLayoutMediator

class TutorialActivity : AppCompatActivity() {

    private  lateinit var binding: ActivityTutorialBinding


    private val adapter by lazy { ViewPagerAdapter(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_tutorial)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.pager.adapter=adapter
        val tabLayoutMediator = TabLayoutMediator(binding.tabLayout,binding.pager){tab,position ->}.attach()
         //onBackPressed()







    }
}