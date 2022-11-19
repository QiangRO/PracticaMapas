package com.example.practicamapas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.example.practicamapas.databinding.ActivitySplashScreenBinding

@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {
    private lateinit var mhandler: Handler
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mhandler = Handler(mainLooper)
        limitarPantalla()
        crearThread()
    }
    private fun cargarPantalla(){
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }
    private fun limitarPantalla(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

    }

    private fun crearThread() {
        Thread{
            try {
                //TODO(PORQUE FALLA?)
                for (i in 0 .. 100){
                    Thread.sleep(100)
                    mhandler.post{
                        binding.apply {
                            pbCarga.progress = i
                            if (i == 100){
                                cargarPantalla()
                            }
                        }
                    }
                }
            } catch (e: InterruptedException){
                e.printStackTrace()
            }
        }.start()
    }
    private fun cargarActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            cargarPantalla()
            finish()
        }, 3000)
    }
}