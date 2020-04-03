package com.epita.pokebattle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.epita.pokebattle.model.PokemonListItem
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_pokedex.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class Pokedex : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pokedex , container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pokedex_fragment_error_txt.isVisible = false

        val baseURL = "https://www.surleweb.xyz/api/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
            .build()
        val service: WSInterface = retrofit.create(WSInterface::class.java)
        service.listPokemons().enqueue(wsCallback)
    }


    val wsCallback: Callback<List<PokemonListItem>> = object : Callback<List<PokemonListItem>> {

        override fun onFailure(call: Call<List<PokemonListItem>>, t: Throwable) {
            Log.e("WS", "WebService call failed " + t.message)
            pokedex_fragment_error_txt.isVisible = true
            pokedex_fragment_list.isVisible = false
        }

        override fun onResponse(call: Call<List<PokemonListItem>>, response:
        Response<List<PokemonListItem>>) {
            if (response.code() == 200) {
                val responseData = response.body()
                if (responseData != null) {
                    Log.d("WS", "WebService success : " + responseData.size + " items found")
                }
                else
                {
                    Log.w("WS", "WebService success : but no item found")
                    pokedex_fragment_error_txt.isVisible = true
                    pokedex_fragment_list.isVisible = false
                    return
                }
                val data = responseData!!.sortedBy { x -> x.name }

                val onItemClickListener = View.OnClickListener { clickedRow ->
                    val pokemon = clickedRow.tag as PokemonListItem
                    (activity as PokedexInteractions).onListItemClicked(pokemon)
                }

                pokedex_fragment_list.setHasFixedSize(true)
                pokedex_fragment_list.layoutManager = LinearLayoutManager(activity)
                pokedex_fragment_list.adapter = PokedexAdapter(data, activity!!, onItemClickListener)
            }
            else
            {
                Log.w("WS", "WebService failed")
                pokedex_fragment_error_txt.isVisible = true
                pokedex_fragment_list.isVisible = false
            }

        }
    }

    interface WSInterface {
        @GET("pokemons.json")
        fun listPokemons(): Call<List<PokemonListItem>>
    }

    interface PokedexInteractions {
        fun onListItemClicked(pokemonListItem: PokemonListItem)
    }

}
