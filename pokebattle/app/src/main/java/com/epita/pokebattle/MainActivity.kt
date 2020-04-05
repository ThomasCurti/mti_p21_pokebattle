package com.epita.pokebattle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.epita.pokebattle.model.PokemonListItem
import com.epita.pokebattle.model.TypeItem
import com.google.gson.GsonBuilder


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

    override fun moveToBattle(firstSelected: PokemonListItem,
                              secondSelected: PokemonListItem,
                              thirdSelect: PokemonListItem,
                              opponentSelected: PokemonListItem) {

        var bundle = Bundle()
        bundle.putString("firstName", firstSelected.name)
        bundle.putString("secondName", secondSelected.name)
        bundle.putString("thirdName", thirdSelect.name)
        bundle.putString("opponentName", opponentSelected.name)

        val battle = Battle()
        battle.arguments = bundle

        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.activity_main, battle)
            .commit()
    }

    override fun moveToTypeHelper(type: TypeItem) {
        var bundle = Bundle()
        bundle.putString("id", type.id.toString())
        bundle.putString("name", type.name)

        val typeHelper = TypeHelper()
        typeHelper.arguments = bundle

        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .add(R.id.activity_main, typeHelper)
            .commit()
    }


}
