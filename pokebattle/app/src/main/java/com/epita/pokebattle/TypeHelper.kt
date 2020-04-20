package com.epita.pokebattle

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epita.pokebattle.methods.fixDataType
import com.epita.pokebattle.methods.getImageFromType
import com.epita.pokebattle.model.TypeUrlName
import com.epita.pokebattle.model.Types
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_pokedex.*
import kotlinx.android.synthetic.main.fragment_type_helper.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class TypeHelper : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_type_helper, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments!!["id"].toString().toInt()
        val name = arguments!!["name"].toString()

        typehelper_fragment_type_img.setImageResource(getImageFromType(name))

        val baseURL = "https://pokeapi.co/api/v2/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
            .build()
        val service: WSinterface = retrofit.create(WSinterface::class.java)
        service.getTypes(name).enqueue(wsCallback)
    }

    val wsCallback: Callback<Types> = object : Callback<Types> {
        override fun onFailure(call: Call<Types>, t: Throwable) {
            Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
            Log.e("WS", "WebService call failed " + t.message)
        }

        override fun onResponse(call: Call<Types>, response:
        Response<Types>
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

                FillTypes(fixDataType(responseData.damage_relations.double_damage_from), typehelper_fragment_doublefrom)
                FillTypes(fixDataType(responseData.damage_relations.double_damage_to), typehelper_fragment_doubleto)
                FillTypes(fixDataType(responseData.damage_relations.half_damage_from), typehelper_fragment_halffrom)
                FillTypes(fixDataType(responseData.damage_relations.half_damage_to), typehelper_fragment_halfto)
                FillTypes(fixDataType(responseData.damage_relations.no_damage_from), typehelper_fragment_normalfrom)
                FillTypes(fixDataType(responseData.damage_relations.no_damage_to), typehelper_fragment_normalto)

            }
            else
            {
                Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
                Log.w("WS", "WebService failed")
            }

        }
    }

    fun FillTypes(list: List<Array<TypeUrlName>>, recyclerView: RecyclerView)
    {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = TypeHelperAdapter(
            list,
            activity!!
            )
    }


    interface WSinterface {
        @GET("type/{name}")
        fun getTypes(@Path("name") name: String): Call<Types>
    }

}
