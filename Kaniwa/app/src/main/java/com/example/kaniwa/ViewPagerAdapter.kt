package com.example.kaniwa

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa){
    companion object{ private  const val ARG_OBJECT = "object" }

    override fun getItemCount(): Int = 4 //cuantos swipe tendra

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> { Fragment1() }
            1 -> { Fragment2() }
            2 -> { Fragment3() }
            3 -> { Fragment4() }
            else-> Fragment1()
        }


        val fragment = Fragment1() //para cada instancia se debe hacer uno de estos
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt(ARG_OBJECT, position + 1)
        }
        return fragment
    }
}