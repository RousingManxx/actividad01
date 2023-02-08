package com.example.kaniwa
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class InitialActivity : AppCompatActivity() {
    private val DURATION: Long = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)
        //Eliminar el action bar de la pantalla
        supportActionBar?.hide()
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val logotipo = findViewById<ImageView>(R.id.logotipo)
        Glide.with(this).load(R.drawable.logo_kaniwa).transition(DrawableTransitionOptions.withCrossFade()).into(logotipo)
        cambiarActivity()
    }

    private fun cambiarActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle())},DURATION)
    }
}

