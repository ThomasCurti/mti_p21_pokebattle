package com.epita.pokebattle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.epita.pokebattle.methods.DownloadImageTask
import com.epita.pokebattle.methods.getBaseStat
import com.epita.pokebattle.methods.getImageFromType
import com.epita.pokebattle.model.Pokemon
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_detail_pokemon.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class DetailPokemon : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_pokemon, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments!!["id"]
        val name = arguments!!["name"].toString()

        val baseURL = "https://pokeapi.co/api/v2/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
            .build()
        val service: WSinterfaceGetPokemon = retrofit.create(WSinterfaceGetPokemon::class.java)
        service.getPokemon(name).enqueue(wsCallbackGetPokemon)
    }

    val wsCallbackGetPokemon: Callback<Pokemon> = object : Callback<Pokemon> {
        override fun onFailure(call: Call<Pokemon>, t: Throwable) {
            Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
            Log.e("WS", "WebService call failed " + t.message)
        }

        override fun onResponse(call: Call<Pokemon>, response:
        Response<Pokemon>
        ) {
            if (response.code() == 200) {
                val responseData = response.body()
                if (responseData != null) {
                    Log.d("WS", "WebService success : $responseData items found")
                }
                else {
                    Toast.makeText(context, "No item found, check your internet connection", Toast.LENGTH_LONG).show()
                    Log.w("WS", "WebService success : but no item found")
                    return
                }
                //can't use Glide so..
                DownloadImageTask(activity!!.findViewById(R.id.detail_pokemon_fragment_img))
                    .execute(responseData.sprites.front_default);

                detail_pokemon_fragment_name_txt.text = responseData.name
                detail_pokemon_fragment_height_txt.text = responseData.height.toString()
                detail_pokemon_fragment_speed_txt.text = getBaseStat(responseData, "speed").toString()
                detail_pokemon_fragment_attack_txt.text = getBaseStat(responseData, "attack").toString()
                detail_pokemon_fragment_special_attack_txt.text = getBaseStat(responseData, "special-attack").toString()
                detail_pokemon_fragment_defense_txt.text = getBaseStat(responseData, "defense").toString()
                detail_pokemon_fragment_special_defense_txt.text = getBaseStat(responseData, "special-defense").toString()
                detail_pokemon_fragment_weight_txt.text = responseData.weight.toString()
                detail_pokemon_fragment_hp_txt.text = getBaseStat(responseData, "hp").toString()

                if(responseData.types.isNotEmpty())
                    detail_pokemon_fragment_first_type_img.setImageResource(getImageFromType(responseData.types[0].type.name))
                if (responseData.types.size > 1)
                    detail_pokemon_fragment_second_type_img.setImageResource(getImageFromType(responseData.types[1].type.name))

            }
            else
            {
                Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
                Log.w("WS", "WebService failed")
            }

        }
    }


    interface WSinterfaceGetPokemon {
        @GET("pokemon/{name}")
        fun getPokemon(@Path("name") name: String): Call<Pokemon>
    }



}
