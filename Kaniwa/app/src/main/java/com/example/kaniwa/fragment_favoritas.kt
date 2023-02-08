package com.example.kaniwa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.kaniwa.MapFragment
import com.example.kaniwa.databinding.ActivityTutorialBinding
import com.example.kaniwa.databinding.FragmentFavoritasBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


private  var _binding: FragmentFavoritasBinding? = null
private  val binding get() = _binding!!
class fragment_favoritas : Fragment() {
    // TODO: Rename and change types of parameters


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = FragmentFavoritasBinding.inflate(layoutInflater)



        /*binding.button.setOnClickListener{
            Toast.makeText(getActivity(), "Rutas", Toast.LENGTH_SHORT).show()
            if(MapFragment.getInstance().ban == false){
                Toast.makeText(getContext(), "Rutas", Toast.LENGTH_SHORT).show()
                MapFragment.getInstance().ban = true
                //ATAZ()
                //AMARILLO()
                MapFragment.getInstance().SUX2()
                print("Si entra")

            }else if(MapFragment.getInstance().ban == true){
                Toast.makeText(getContext(), "Limpiar mapa", Toast.LENGTH_SHORT).show()
                MapFragment.getInstance().ban = false
                MapFragment.getInstance().map.clear()
            }
        }*/

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritasBinding.inflate(inflater,container,false)
        binding.button.setOnClickListener{
            Toast.makeText(getActivity(), "PRUEBA DE QUE JALA", Toast.LENGTH_SHORT).show()
            if(MapFragment.getInstance().ban == false){
                Toast.makeText(getContext(), "Favorita", Toast.LENGTH_SHORT).show()
                MapFragment.getInstance().ban = true
                MapFragment.getInstance().ATAZ()
                //AMARILLO()
                //MapFragment.getInstance().SUX2()


            }else if(MapFragment.getInstance().ban == true){
                Toast.makeText(getContext(), "Limpiar mapa", Toast.LENGTH_SHORT).show()
                MapFragment.getInstance().ban = false
                MapFragment.getInstance().map.clear()
            }
        }
        return  binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_favoritas.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_favoritas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}