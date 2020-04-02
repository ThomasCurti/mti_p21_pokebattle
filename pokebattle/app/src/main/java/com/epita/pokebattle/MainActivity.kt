package com.epita.pokebattle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(),
                     SplashScreen.SplashScreenInteractions  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.activity_main, SplashScreen())
        transaction.commit();
    }


}
