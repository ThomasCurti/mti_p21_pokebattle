package com.epita.pokebattle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.epita.pokebattle.methods.AllPokemons
import com.epita.pokebattle.methods.getImageFromType
import com.epita.pokebattle.model.PokemonListItem
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_pokedex.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class Pokedex : Fragment(), HasList {

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

        if (AllPokemons.data.isNotEmpty()) {
            val onItemClickListener = clickedListener

            pokedex_fragment_list.setHasFixedSize(true)
            pokedex_fragment_list.layoutManager = LinearLayoutManager(activity)
            pokedex_fragment_list.adapter = Adapter(
                AllPokemons.data,
                activity!!,
                onItemClickListener,
                R.layout.pokedex_list_item,
                ::binder,
                R.id.pokedex_list_item_name_txt,
                R.id.pokedex_list_item_first_attr_img,
                R.id.pokedex_list_item_sec_attr_img
            )
        }
        else
        {
            val baseURL = "https://www.surleweb.xyz/api/"
            val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
            val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(jsonConverter)
                .build()
            val service: WSInterface = retrofit.create(WSInterface::class.java)
            service.listPokemons().enqueue(wsCallback)
        }


    }

    val wsCallback: Callback<List<PokemonListItem>> = object : Callback<List<PokemonListItem>> {

        override fun onFailure(call: Call<List<PokemonListItem>>, t: Throwable) {
            Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
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
                } else {
                    Toast.makeText(context, "No item found, check your internet connection", Toast.LENGTH_LONG).show()
                    Log.w("WS", "WebService success : but no item found")
                    pokedex_fragment_error_txt.isVisible = true
                    pokedex_fragment_list.isVisible = false
                    return
                }
                val data = responseData.sortedBy { x -> x.name }
                AllPokemons.data = data


                val onItemClickListener = clickedListener

                pokedex_fragment_list.setHasFixedSize(true)
                pokedex_fragment_list.layoutManager = LinearLayoutManager(activity)
                pokedex_fragment_list.adapter = Adapter(
                    data,
                    activity!!,
                    onItemClickListener,
                    R.layout.pokedex_list_item,
                    ::binder,
                    R.id.pokedex_list_item_name_txt,
                    R.id.pokedex_list_item_first_attr_img,
                    R.id.pokedex_list_item_sec_attr_img
                )
            }
            else
            {
                Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
                Log.w("WS", "WebService failed")
                pokedex_fragment_error_txt.isVisible = true
                pokedex_fragment_list.isVisible = false
            }

        }
    }

    val clickedListener : View.OnClickListener = View.OnClickListener{ clickedRow ->
        val pokemon = clickedRow.tag as PokemonListItem
        (activity as PokedexInteractions).onListPokedexItemClicked(pokemon)
    }

    override fun binder(holder: Adapter.ViewHolder, position: Int, data : List<PokemonListItem>)
    {
        holder.itemView.tag = data[position]

        holder.name.text =  data[position].name.capitalize()

        if(data[position].types.isNotEmpty())
            holder.firstAttribute.setImageResource(getImageFromType(data[position].types[0].name))

        if (data[position].types.size > 1)
            holder.secondAttribute.setImageResource(getImageFromType(data[position].types[1].name))
    }


    interface WSInterface {
        @GET("pokemons.json")
        fun listPokemons(): Call<List<PokemonListItem>>
    }

    interface PokedexInteractions {
        fun onListPokedexItemClicked(pokemonListItem: PokemonListItem)
    }

}
