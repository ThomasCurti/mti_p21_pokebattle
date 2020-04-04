package com.epita.pokebattle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.epita.pokebattle.model.PokemonListItem
import com.epita.pokebattle.model.TypeItem

class MainActivity : AppCompatActivity(),
                     SplashScreen.SplashScreenInteractions,
                     Pokedex.PokedexInteractions,
                     Lobby.LobbyInteractions {

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

    override fun onListPokedexItemClicked(pokemonListItem: PokemonListItem) {
        var bundle = Bundle()
        bundle.putString("id", pokemonListItem.id.toString())
        bundle.putString("name", pokemonListItem.name)

        val detailPokemon = DetailPokemon()
        detailPokemon.arguments = bundle

        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.activity_main, detailPokemon)
            .commit()
    }

    override fun moveToFight() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun moveToTypeHelper(type: TypeItem) {
        var bundle = Bundle()
        bundle.putString("id", type.id.toString())
        bundle.putString("name", type.name)

        val typeHelper = TypeHelper()
        typeHelper.arguments = bundle

        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.activity_main, typeHelper)
            .commit()
    }


}
