package com.epita.pokebattle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.epita.pokebattle.model.PokemonListItem

class MainActivity : AppCompatActivity(),
                     SplashScreen.SplashScreenInteractions,
                     Pokedex.PokedexInteractions {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_main, SplashScreen())
            .commit();
    }

    override fun moveToPokedex() {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.activity_main, Pokedex())
            .commit()
    }

    override fun moveToLobby() {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.activity_main, Lobby())
            .commit()
    }

    override fun onListItemClicked(pokemonListItem: PokemonListItem) {
        var bundle = Bundle()
        bundle.putString("id", pokemonListItem.id.toString())
        bundle.putString("name", pokemonListItem.name)

        val detailPokemon = DetailPokemon()
        detailPokemon.arguments = bundle

        var transaction = supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.activity_main, detailPokemon)
            .commit()
    }


}
