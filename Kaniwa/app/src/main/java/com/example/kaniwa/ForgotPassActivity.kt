package com.example.kaniwa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kaniwa.databinding.ActivityForgotPassBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPassBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPassBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setup()
    }

    private fun setup(){
        binding.submit.setOnClickListener{
            //Toma el correo del campo de texto para enviar el mensaje de recuperación a dicho correo
            val email:String = binding.forgotPassText.editText?.text.toString().trim{it <=' '}
            //Si esta vacio envia un Toast solicitando que llene el campo
            if(email.isEmpty()){
                Toast.makeText(this,"Ingrese su correo electrónico por favor",Toast.LENGTH_LONG).show()
            }else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,"Email de recuperación de contraseña enviado",Toast.LENGTH_LONG).show()
                        finish()
                    }else{
                        Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
