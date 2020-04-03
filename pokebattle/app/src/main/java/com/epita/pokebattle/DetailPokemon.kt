package com.epita.pokebattle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.epita.pokebattle.methods.DownloadImageTask
import com.epita.pokebattle.methods.getBaseStat
import com.epita.pokebattle.model.Pokemon
import com.google.gson.GsonBuilder
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

        Log.w("TAG", "$id $name")

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
                else
                {
                    Log.w("WS", "WebService success : but no item found")
                    return
                }

                Log.e("TAG", responseData.name)
                Log.e("TAG", responseData.height.toString())
                Log.e("TAG", getBaseStat(responseData, "speed").toString())
                Log.e("TAG", getBaseStat(responseData, "attack").toString())
                Log.e("TAG", getBaseStat(responseData, "special-attack").toString())
                Log.e("TAG", getBaseStat(responseData, "defense").toString())
                Log.e("TAG", getBaseStat(responseData, "special-defense").toString())
                Log.e("TAG", responseData.weight.toString())
                Log.e("TAG", getBaseStat(responseData, "hp").toString())

                Log.e("TAG", responseData.types[0].type.name)
                if (responseData.types.size > 1)
                    Log.e("TAG", responseData.types[1].type.name)

                Log.e("TAG", responseData.sprites.front_default)

                DownloadImageTask(activity!!.findViewById(R.id.detail_pokemon_fragment_img))
                    .execute(responseData.sprites.front_default);

                /*var bmp: Bitmap
                thread(start = true) {
                    val url = URL(responseData.sprites.front_default);
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                }.join()
                detail_pokemon_fragment_img.setImageBitmap(bmp);*/

            }
            else
            {
                Log.w("WS", "WebService failed")
            }

        }
    }


    interface WSinterfaceGetPokemon {
        @GET("pokemon/{name}")
        fun getPokemon(@Path("name") name: String): Call<Pokemon>
    }



}
