package com.example.kaniwa
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kaniwa.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?: "", provider ?: "")

        //gUARDADO DE DATOS
        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

    private fun setup(email: String, provider: String) {
        title = "Inicio"
        binding.emailTextView.text = email
        binding.providerTextView.text = provider
        binding.closeButton.setOnClickListener {
            //Borrado de datos de sesion
            val prefs =
                getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }

}