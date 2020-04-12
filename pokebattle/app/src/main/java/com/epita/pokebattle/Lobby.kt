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
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.with
import com.epita.pokebattle.methods.AllPokemons
import com.epita.pokebattle.methods.getImageFromType
import com.epita.pokebattle.model.PokemonListItem
import com.epita.pokebattle.model.TypeItem
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_lobby.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import kotlin.random.Random

import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter

class Lobby : Fragment(), HasList {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lobby , container,false)
    }

    var selectedPokemon: PokemonListItem? = null

    var firstPokemon: PokemonListItem? = null
    var secondPokemon: PokemonListItem? = null
    var thirdPokemon: PokemonListItem? = null
    var opponentPokemon: PokemonListItem? = null
    var nbSelected: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lobby_fragment_error_txt.isVisible = false

        lobby_fragment_select_first_btn.setOnClickListener {
            if (selectedPokemon != null)
            {
                if (firstPokemon == null)
                    nbSelected++
                lobby_fragment_select_first_txt.text = selectedPokemon!!.name

                Glide.with(this).load(selectedPokemon!!.sprite).into(lobby_fragment_select_first_img)

                firstPokemon = selectedPokemon
                if (nbSelected >= 3)
                {
                    lobby_fragment_fight_btn.visibility = View.VISIBLE
                }

            } else {
                Toast.makeText(context, "You forgot to choose a Pokemon", Toast.LENGTH_SHORT).show()
            }
        }

        lobby_fragment_select_second_btn.setOnClickListener {
            if (selectedPokemon != null)
            {
                if (secondPokemon == null)
                    nbSelected++
                lobby_fragment_select_second_txt.text = selectedPokemon!!.name
                Glide.with(this).load(selectedPokemon!!.sprite).into(lobby_fragment_select_second_img)

                secondPokemon = selectedPokemon
                if (nbSelected >= 3)
                {
                    lobby_fragment_fight_btn.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(context, "You forgot to choose a Pokemon", Toast.LENGTH_SHORT).show()
            }
        }

        lobby_fragment_select_third_btn.setOnClickListener {
            if (selectedPokemon != null)
            {
                if (thirdPokemon == null)
                    nbSelected++
                lobby_fragment_select_third_txt.text = selectedPokemon!!.name
                Glide.with(this).load(selectedPokemon!!.sprite).into(lobby_fragment_select_third_img)

                thirdPokemon = selectedPokemon
                if (nbSelected >= 3)
                {
                    lobby_fragment_fight_btn.visibility = View.VISIBLE
                }

            } else {
                Toast.makeText(context, "You forgot to choose a Pokemon", Toast.LENGTH_SHORT).show()
            }
        }

        lobby_fragment_fight_btn.setOnClickListener {
            (activity as LobbyInteractions).moveToBattle(firstPokemon!!, secondPokemon!!, thirdPokemon!!, opponentPokemon!!)
        }

        lobby_fragment_opponent_first_attribute_img.setOnClickListener {
            if(opponentPokemon != null)
            {
                (activity as LobbyInteractions).moveToTypeHelper(opponentPokemon!!.types[0])
            }
        }

        lobby_fragment_opponent_second_attribute_img.setOnClickListener {
            if(opponentPokemon != null)
            {
                if (opponentPokemon!!.types.size > 1)
                    (activity as LobbyInteractions).moveToTypeHelper(opponentPokemon!!.types[1])
            }
        }

        if (AllPokemons.data.isNotEmpty()) {
            val onItemClickListener = itemClickedListener

            var data = AllPokemons.data
            data = data.sortedWith(compareBy<PokemonListItem>({ x -> x.types[0].name }, { x -> if (x.types.size > 1) x.types[1].name else ""}))

            lobby_fragment_list.setHasFixedSize(true)
            lobby_fragment_list.layoutManager = LinearLayoutManager(activity)
            lobby_fragment_list.adapter = Adapter(
                data,
                activity!!,
                onItemClickListener,
                R.layout.lobby_list_item,
                ::binder,
                R.id.lobby_list_item_name_txt,
                R.id.lobby_list_item_first_attr_img,
                R.id.lobby_list_item_sec_attr_img
            )

            setOpponent(data)
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
            lobby_fragment_error_txt.isVisible = true
            lobby_fragment_list.isVisible = false
        }

        override fun onResponse(call: Call<List<PokemonListItem>>, response:
        Response<List<PokemonListItem>>
        ) {
            if (response.code() == 200) {
                val responseData = response.body()
                if (responseData != null) {
                    Log.d("WS", "WebService success : " + responseData.size + " items found")
                } else {
                    Toast.makeText(context, "No item found, check your internet connection", Toast.LENGTH_LONG).show()
                    Log.w("WS", "WebService success : but no item found")
                    lobby_fragment_error_txt.isVisible = true
                    lobby_fragment_list.isVisible = false
                    return
                }
                var data = responseData.sortedBy { x -> x.name }
                AllPokemons.data = data

                data = data.sortedWith(compareBy<PokemonListItem>({ x -> x.types[0].name }, { x -> if (x.types.size > 1) x.types[1].name else ""}))

                val onItemClickListener = itemClickedListener

                lobby_fragment_list.setHasFixedSize(true)
                lobby_fragment_list.layoutManager = LinearLayoutManager(activity)
                lobby_fragment_list.adapter = Adapter (
                    data,
                    activity!!,
                    onItemClickListener,
                    R.layout.lobby_list_item,
                    ::binder,
                    R.id.lobby_list_item_name_txt,
                    R.id.lobby_list_item_first_attr_img,
                    R.id.lobby_list_item_sec_attr_img
                )

                setOpponent(data)
            }
            else
            {
                Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
                Log.w("WS", "WebService failed")
                lobby_fragment_error_txt.isVisible = true
                lobby_fragment_list.isVisible = false
            }

        }
    }

    val itemClickedListener : View.OnClickListener = View.OnClickListener{ clickedRow ->
        selectedPokemon = clickedRow.tag as PokemonListItem
        lobby_fragment_current_select_name_txt.text = selectedPokemon!!.name
        lobby_fragment_current_select_first_attribute_img.setImageResource(getImageFromType(selectedPokemon!!.types[0].name))
        if (selectedPokemon!!.types.size > 1)
            lobby_fragment_current_select_second_attribute_img.setImageResource(getImageFromType(selectedPokemon!!.types[1].name))
    }

    fun setOpponent(data: List<PokemonListItem>)
    {
        val opponentPokemon = data[Random.nextInt(data.size)]
        var name = opponentPokemon.name
        if (opponentPokemon.name.length > 8)
        {
            name = name.substring(0, 6) + ".."
        }

        lobby_fragment_opponent_name_txt.text = name
        Glide.with(this).load(opponentPokemon.sprite).into(lobby_fragment_opponent_img)

        lobby_fragment_opponent_first_attribute_img.setImageResource(getImageFromType(opponentPokemon.types[0].name))
        if (opponentPokemon.types.size > 1)
        {
            lobby_fragment_opponent_second_attribute_img.setImageResource(getImageFromType(opponentPokemon.types[1].name))
        }

        this.opponentPokemon = opponentPokemon
    }

    override fun binder(holder: Adapter.ViewHolder, position: Int, data: List<PokemonListItem>) {
        val pokemon =  data[position]

        holder.itemView.tag = pokemon

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

    interface LobbyInteractions {
        fun moveToBattle(firstSelected: PokemonListItem,
                                  secondSelected: PokemonListItem,
                                  thirdSelect: PokemonListItem,
                                  opponentSelected: PokemonListItem)
        fun moveToTypeHelper(type: TypeItem)
    }


}
