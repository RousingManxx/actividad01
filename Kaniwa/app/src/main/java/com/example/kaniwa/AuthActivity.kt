package com.example.kaniwa
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kaniwa.R.string
import com.example.kaniwa.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Analytics Event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitScreen",bundle)

        //SetUp
        setup()
        sesion()
    }

    override fun onStart() {
        super.onStart()
        binding.authlayout.visibility= View.VISIBLE

    }
    private fun sesion(){
        val prefs = getSharedPreferences(getString(string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)
        if (email !=null && provider != null){
            binding.authlayout.visibility= View.INVISIBLE
            showLogin(email, ProviderType.valueOf(provider))
        }
    }

    private  fun  setup(){
        title="Registro de Nuevo Usuario"
        binding.signButton.setOnClickListener{
            if (binding.emailTextField.toString().isNotEmpty() && binding.passwordTextField.toString().isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailTextField.editText?.text.toString(),binding.passwordTextField.editText?.text.toString()).addOnCompleteListener(){
                    if (it.isSuccessful){
                        val user = Firebase.auth.currentUser
                        user!!.sendEmailVerification().addOnCompleteListener{task->
                            if(task.isSuccessful){
                                Log.d(TAG, "Email enviado")
                                Toast.makeText(this,"Registro realizado, por favor consulte el correo de verificación",Toast.LENGTH_LONG).show()
                                showLogin(it.result?.user?.email ?:"",ProviderType.BASIC)
                            }else{
                                Log.d(TAG, "Email enviado")
                                Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                            }
                        }
                    }else{
                        showAlert()
                    }
                }
            }
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando el usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showLogin(email: String, provider: ProviderType){
        val loginIntent = Intent(this,LoginActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(loginIntent)
    }


}