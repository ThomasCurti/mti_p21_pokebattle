package com.epita.pokebattle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.epita.pokebattle.methods.*
import com.epita.pokebattle.model.Move
import com.epita.pokebattle.model.Pokemon
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.random.Random
import com.epita.pokebattle.model.Move.PokemonMove
import kotlinx.android.synthetic.main.fragment_battle.*

class Battle : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_battle, container, false)
    }

    var firstName = ""
    var secondName = ""
    var thirdName = ""
    var firstOpponentName = ""
    var secondOpponentName = ""
    var thirdOpponentName = ""


    var firstPokemon: Pokemon? = null
    var secondPokemon: Pokemon? = null
    var thirdPokemon: Pokemon? = null

    var firstAttacks: PokemonMove? = null
    var secondAttacks: PokemonMove? = null
    var thirdAttacks: PokemonMove? = null

    var firstOpponent: Pokemon? = null
    var secondOpponent: Pokemon? = null
    var thirdOpponent: Pokemon? = null

    var firstOpponentAttacks: PokemonMove? = null
    var secondOpponentAttacks: PokemonMove? = null
    var thirdOpponentAttacks: PokemonMove? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        * Get all datas
        * */
        firstName = arguments!!.getString("firstName")!!
        secondName = arguments!!.getString("secondName")!!
        thirdName = arguments!!.getString("thirdName")!!
        firstOpponentName = arguments!!.getString("opponentName")!!
        secondOpponentName = AllPokemons.data[Random.nextInt(AllPokemons.data.size)].name
        thirdOpponentName = AllPokemons.data[Random.nextInt(AllPokemons.data.size)].name


        val baseURL = "https://pokeapi.co/api/v2/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
            .build()
        val service: WSinterfaceGetPokemon = retrofit.create(WSinterfaceGetPokemon::class.java)
        service.getPokemon(firstName).enqueue(wsCallbackGetPokemon)
        service.getPokemon(secondName).enqueue(wsCallbackGetPokemon)
        service.getPokemon(thirdName).enqueue(wsCallbackGetPokemon)
        service.getPokemon(firstOpponentName).enqueue(wsCallbackGetPokemon)
        service.getPokemon(secondOpponentName).enqueue(wsCallbackGetPokemon)
        service.getPokemon(thirdOpponentName).enqueue(wsCallbackGetPokemon)
    }
    /*
     * Get all datas
     * */
        val wsCallbackGetPokemon: Callback<Pokemon> = object : Callback<Pokemon> {
            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
                Log.e("WSPokemon", "WebService call failed " + t.message)
            }

            override fun onResponse(call: Call<Pokemon>, response:
            Response<Pokemon>
            ) {
                if (response.code() == 200) {
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d("WSPokemon", "WebService success : $responseData items found")
                    }
                    else {
                        Toast.makeText(context, "No item found, check your internet connection", Toast.LENGTH_LONG).show()
                        Log.w("WSPokemon", "WebService success : but no item found")
                        return
                    }

                    var nb = fillData(responseData)
                    if (nb == -1)
                        return
                    getAttacks(responseData, nb)

                }
                else
                {
                    Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
                    Log.w("WSPokemon", "WebService failed")
                }

            }
        }

    interface WSinterfaceGetPokemon {
        @GET("pokemon/{name}")
        fun getPokemon(@Path("name") name: String): Call<Pokemon>
    }

    interface WSinterfaceGetMove {
        @GET("move/{id}/")
        fun getMove(@Path("id") name: String): Call<Move>
    }

    fun fillData(data: Pokemon): Int
    {
        if (data.name == firstName && firstPokemon == null) {

            //can't use Glide so..
            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_current_image_img))
                .execute(data.sprites.back_default);
            battle_fragment_current_life_txt.text = "hp: " + getBaseStat(data, "hp").toString()
            battle_fragment_current_name_txt.text = data.name
            battle_fragment_current_type_img.setImageResource(getImageFromType(data.types[0].type.name))

            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_pokemon_team_first_img))
                .execute(data.sprites.front_default);

            //deprecated since API 23 but we are using API 21 so we must use it
            battle_fragment_pokemon_team_first_img.setBackgroundColor(resources.getColor(R.color.background_lobby_select))

            firstPokemon = data
            return 1
        }

        else if (data.name == secondName && secondPokemon == null) {
            secondPokemon = data

            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_pokemon_team_second_img))
                .execute(data.sprites.front_default);
            battle_fragment_pokemon_team_second_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))

            return 2
        }

        else if (data.name == thirdName && thirdPokemon == null) {
            thirdPokemon = data

            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_pokemon_team_third_img))
                .execute(data.sprites.front_default);
            battle_fragment_pokemon_team_third_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))

            return 3
        }

        else if (data.name == firstOpponentName && firstOpponent == null) {
            firstOpponent = data
            return 4
        }

        else if (data.name == secondOpponentName && secondOpponent == null) {
            secondOpponent = data
            return 5
        }

        else if (data.name == thirdOpponentName && thirdOpponent == null) {
            thirdOpponent = data
            return 6
        }
        return -1
    }

    fun getAttacks(data: Pokemon, nb: Int)
    {
        val baseURL = "https://pokeapi.co/api/v2/"
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
            .build()
        val service: WSinterfaceGetMove = retrofit.create(WSinterfaceGetMove::class.java)

        val wsCallbackGetMove: CallBackWithArg<Move> = object : CallBackWithArg<Move>(data.name, nb) {
            override fun onFailure(call: Call<Move>, t: Throwable) {
                Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
                Log.e("WS", "WebService call failed " + t.message)
            }
            override fun onResponse(call: Call<Move>, response:
            Response<Move>
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

                    //Success
                    if ((responseData.damage_class.name == "physical" || responseData.damage_class.name == "special")
                        && responseData.target.name == "selected-pokemon")
                        fillAttacks(responseData, arg!!, nb)

                }
                else
                {
                    Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
                    Log.w("WS", "WebService failed")
                }
            }
        }

        //TODO random ?
        for (move in data.moves) {
            val moveSplit = move.move.url.split("/")
            val id = moveSplit[moveSplit.size - 2]
            service.getMove(id).enqueue(wsCallbackGetMove)
        }
    }

    fun checkMoves(data: Pokemon): Boolean
    {
        val test = (firstAttacks != null && data.name == firstAttacks!!.name && firstAttacks!!.moves.size >= 4) ||
                (secondAttacks != null && data.name == secondAttacks!!.name && secondAttacks!!.moves.size >= 4) ||
                (thirdAttacks != null && data.name == thirdAttacks!!.name && thirdAttacks!!.moves.size >= 4) ||
                (firstOpponentAttacks != null && data.name == firstOpponentAttacks!!.name && firstOpponentAttacks!!.moves.size >= 4) ||
                (secondOpponentAttacks != null && data.name == secondOpponentAttacks!!.name && secondOpponentAttacks!!.moves.size >= 4) ||
                (thirdOpponentAttacks != null && data.name == thirdOpponentAttacks!!.name && thirdOpponentAttacks!!.moves.size >= 4)
        return test
    }

    fun fillAttacks(move: Move, name: String, nb: Int)
    {
        if (firstPokemon != null && name == firstPokemon!!.name && nb == 1) {
            if (checkMoves(firstPokemon!!))
            {
                return
            }
            if(firstAttacks == null)
                firstAttacks = PokemonMove(firstPokemon!!.name, mutableListOf())
            firstAttacks!!.moves.add(move)

        } else if (secondPokemon != null && name == secondPokemon!!.name && nb == 2) {
            if (checkMoves(secondPokemon!!))
            {
                return
            }
            if(secondAttacks == null)
                secondAttacks = PokemonMove(secondPokemon!!.name, mutableListOf())
            secondAttacks!!.moves.add(move)

        } else if (thirdPokemon != null && name == thirdPokemon!!.name && nb == 3) {
            if (checkMoves(thirdPokemon!!))
            {
                return
            }
            if(thirdAttacks == null)
                thirdAttacks = PokemonMove(thirdPokemon!!.name, mutableListOf())
            thirdAttacks!!.moves.add(move)

        } else if (firstOpponent != null && name == firstOpponent!!.name && nb == 4) {
            if (checkMoves(firstOpponent!!))
            {
                return
            }
            if(firstOpponentAttacks == null)
                firstOpponentAttacks = PokemonMove(firstOpponent!!.name, mutableListOf())
            firstOpponentAttacks!!.moves.add(move)

        } else if (secondOpponent != null && name == secondOpponent!!.name && nb == 5) {
            if (checkMoves(secondOpponent!!))
            {
                return
            }
            if(secondOpponentAttacks == null)
                secondOpponentAttacks = PokemonMove(secondOpponent!!.name, mutableListOf())
            secondOpponentAttacks!!.moves.add(move)

        } else if (thirdOpponent != null && name == thirdOpponent!!.name && nb == 6) {
            if (checkMoves(thirdOpponent!!))
            {
                return
            }
            if(thirdOpponentAttacks == null)
                thirdOpponentAttacks = PokemonMove(thirdOpponent!!.name, mutableListOf())
            thirdOpponentAttacks!!.moves.add(move)

        }
    }


    /*
    * Add logic
    * */



}
