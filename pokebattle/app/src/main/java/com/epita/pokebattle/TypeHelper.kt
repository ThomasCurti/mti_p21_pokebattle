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
import com.epita.pokebattle.methods.getImageFromType
import com.epita.pokebattle.model.TypeUrlName
import com.epita.pokebattle.model.Types
import com.google.gson.GsonBuilder
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

    var doubleFrom: List<ImageView> = listOf()
    var doubleTo: List<ImageView> = listOf()
    var halfFrom: List<ImageView> = listOf()
    var halfTo: List<ImageView> = listOf()
    var normalFrom: List<ImageView> = listOf()
    var normalTo: List<ImageView> = listOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doubleFrom = kotlin.collections.listOf<ImageView> (
            typehelper_fragment_double_from_1,
            typehelper_fragment_double_from_2,
            typehelper_fragment_double_from_3,
            typehelper_fragment_double_from_4,
            typehelper_fragment_double_from_5,
            typehelper_fragment_double_from_6,
            typehelper_fragment_double_from_7,
            typehelper_fragment_double_from_8,
            typehelper_fragment_double_from_9
        )

        doubleTo = kotlin.collections.listOf<ImageView> (
            typehelper_fragment_double_to_1,
            typehelper_fragment_double_to_2,
            typehelper_fragment_double_to_3,
            typehelper_fragment_double_to_4,
            typehelper_fragment_double_to_5,
            typehelper_fragment_double_to_6,
            typehelper_fragment_double_to_7,
            typehelper_fragment_double_to_8,
            typehelper_fragment_double_to_9
        )

        halfFrom = kotlin.collections.listOf<ImageView> (
            typehelper_fragment_half_from_1,
            typehelper_fragment_half_from_2,
            typehelper_fragment_half_from_3,
            typehelper_fragment_half_from_4,
            typehelper_fragment_half_from_5,
            typehelper_fragment_half_from_6,
            typehelper_fragment_half_from_7,
            typehelper_fragment_half_from_8,
            typehelper_fragment_half_from_9
        )

        halfTo = kotlin.collections.listOf<ImageView> (
            typehelper_fragment_half_to_1,
            typehelper_fragment_half_to_2,
            typehelper_fragment_half_to_3,
            typehelper_fragment_half_to_4,
            typehelper_fragment_half_to_5,
            typehelper_fragment_half_to_6,
            typehelper_fragment_half_to_7,
            typehelper_fragment_half_to_8,
            typehelper_fragment_half_to_9
        )

        normalFrom = kotlin.collections.listOf<ImageView> (
            typehelper_fragment_normal_from_1,
            typehelper_fragment_normal_from_2,
            typehelper_fragment_normal_from_3,
            typehelper_fragment_normal_from_4,
            typehelper_fragment_normal_from_5,
            typehelper_fragment_normal_from_6,
            typehelper_fragment_normal_from_7,
            typehelper_fragment_normal_from_8,
            typehelper_fragment_normal_from_9
        )

        normalTo = kotlin.collections.listOf<ImageView> (
            typehelper_fragment_normal_to_1,
            typehelper_fragment_normal_to_2,
            typehelper_fragment_normal_to_3,
            typehelper_fragment_normal_to_4,
            typehelper_fragment_normal_to_5,
            typehelper_fragment_normal_to_6,
            typehelper_fragment_normal_to_7,
            typehelper_fragment_normal_to_8,
            typehelper_fragment_normal_to_9
        )


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

                FillTypes(responseData.damage_relations.double_damage_from, doubleFrom)
                FillTypes(responseData.damage_relations.double_damage_to, doubleTo)
                FillTypes(responseData.damage_relations.half_damage_from, halfFrom)
                FillTypes(responseData.damage_relations.half_damage_to, halfTo)
                FillTypes(responseData.damage_relations.no_damage_from, normalFrom)
                FillTypes(responseData.damage_relations.no_damage_to, normalTo)

            }
            else
            {
                Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
                Log.w("WS", "WebService failed")
            }

        }
    }

    fun FillTypes(list: List<TypeUrlName>, array: List<ImageView>)
    {
        for ((index, type) in list.withIndex())
        {
            if (index >= 9)
            {
                Log.e("TYPE", "TOO MUCH TYPES: " + list.toString() + "\n" + typehelper_fragment_type_img.drawable.toString())
                break;
            }
            array[index].setImageResource(getImageFromType(type.name))
        }
    }


    interface WSinterface {
        @GET("type/{name}")
        fun getTypes(@Path("name") name: String): Call<Types>
    }

}
