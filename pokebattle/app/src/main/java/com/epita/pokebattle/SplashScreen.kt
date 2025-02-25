package com.epita.pokebattle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_splash_screen.*

class SplashScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        splash_fragment_pokedex_btn.setOnClickListener {
            (activity as SplashScreenInteractions).moveToPokedex()
        }

        splash_fragment_battle_btn.setOnClickListener {
            (activity as SplashScreenInteractions).moveToLobby()
        }


    }


    interface SplashScreenInteractions {
        fun moveToPokedex()
        fun moveToLobby()
    }

}
