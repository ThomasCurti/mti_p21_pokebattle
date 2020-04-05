package com.epita.pokebattle

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.epita.pokebattle.methods.*
import com.epita.pokebattle.model.Move
import com.epita.pokebattle.model.Move.PokemonMove
import com.epita.pokebattle.model.Pokemon
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_battle.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.random.Random


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

    var pokemons: MutableList<Pokemon?> = mutableListOf(null, null, null)
    var pokemonsAttack: MutableList<PokemonMove?> = mutableListOf(null, null, null)

    var opponents: MutableList<Pokemon?> = mutableListOf(null, null, null)
    var opponentsAttacks: MutableList<PokemonMove?> = mutableListOf(null, null, null)


    var currentPokemon: Int = 0
    var currentOpponent: Int = 0
    var canAttack: Boolean = true;


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

        /*
         * Add logic
         * */
        battle_fragment_attack_first_btn.setOnClickListener {
            if (pokemonsAttack[currentPokemon]!!.moves.size > 1)
            {

            }
        }

        battle_fragment_attack_second_btn.setOnClickListener {
            if (pokemonsAttack[currentPokemon]!!.moves.size > 2)
            {

            }
        }

        battle_fragment_attack_third_btn.setOnClickListener {
            if (pokemonsAttack[currentPokemon]!!.moves.size > 3)
            {

            }
        }

        battle_fragment_attack_fourth_btn.setOnClickListener {
            if (pokemonsAttack[currentPokemon]!!.moves.size > 4)
            {

            }
        }

        battle_fragment_pokemon_team_first_img.setOnClickListener {
            if (changePokemon(0))
            {
                //TODO AI turn
            }
        }

        battle_fragment_pokemon_team_second_img.setOnClickListener {
            if (changePokemon(1))
            {
                //TODO AI turn
            }
        }

        battle_fragment_pokemon_team_third_img.setOnClickListener {
            if (changePokemon(2))
            {
                //TODO AI turn
            }
        }

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
        if (data.name == firstName && pokemons[0] == null) {

            //can't use Glide so..
            if (data.sprites.back_default != null)
            {
                DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_current_image_img))
                    .execute(data.sprites.back_default);
            }
            else
            {
                DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_current_image_img))
                    .execute(data.sprites.front_default);
            }

            battle_fragment_current_life_txt.text = "hp: " + getBaseStat(data, "hp").toString()
            battle_fragment_current_name_txt.text = data.name
            battle_fragment_current_type_img.setImageResource(getImageFromType(data.types[0].type.name))

            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_pokemon_team_first_img))
                .execute(data.sprites.front_default);

            //deprecated since API 23 but we are using API 21 so we must use it
            battle_fragment_pokemon_team_first_img.setBackgroundColor(resources.getColor(R.color.background_lobby_select))

            pokemons[0] = data
            return 1
        }

        else if (data.name == secondName && pokemons[1] == null) {
            pokemons[1] = data

            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_pokemon_team_second_img))
                .execute(data.sprites.front_default);
            //deprecated since API 23 but we are using API 21 so we must use it
            battle_fragment_pokemon_team_second_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))

            return 2
        }

        else if (data.name == thirdName && pokemons[2] == null) {
            pokemons[2] = data

            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_pokemon_team_third_img))
                .execute(data.sprites.front_default);
            //deprecated since API 23 but we are using API 21 so we must use it
            battle_fragment_pokemon_team_third_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))

            return 3
        }

        else if (data.name == firstOpponentName && opponents[0] == null) {
            opponents[0] = data

            //can't use Glide so..
            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_opponent_image_img))
                .execute(data.sprites.front_default);
            battle_fragment_opponent_life_txt.text = "hp: " + getBaseStat(data, "hp").toString()
            battle_fragment_opponent_name_txt.text = data.name
            battle_fragment_opponent_type_img.setImageResource(getImageFromType(data.types[0].type.name))

            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_opponent_first_img))
                .execute(data.sprites.front_default);

            //deprecated since API 23 but we are using API 21 so we must use it
            battle_fragment_opponent_first_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))

            return 4
        }

        else if (data.name == secondOpponentName && opponents[1] == null) {
            opponents[1] = data

            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_opponent_second_img))
                .execute(data.sprites.front_default);

            //deprecated since API 23 but we are using API 21 so we must use it
            battle_fragment_opponent_second_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))
            return 5
        }

        else if (data.name == thirdOpponentName && opponents[2] == null) {
            opponents[2] = data

            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_opponent_third_img))
                .execute(data.sprites.front_default);

            //deprecated since API 23 but we are using API 21 so we must use it
            battle_fragment_opponent_third_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))
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

        //TODO random choose
        for (move in data.moves) {
            val moveSplit = move.move.url.split("/")
            val id = moveSplit[moveSplit.size - 2]
            service.getMove(id).enqueue(wsCallbackGetMove)
        }
    }

    fun checkMoves(data: Pokemon): Boolean
    {
        val test = (pokemonsAttack[0] != null && data.name == pokemonsAttack[0]!!.name && pokemonsAttack[0]!!.moves.size >= 4) ||
                (pokemonsAttack[1] != null && data.name == pokemonsAttack[1]!!.name && pokemonsAttack[1]!!.moves.size >= 4) ||
                (pokemonsAttack[2] != null && data.name == pokemonsAttack[2]!!.name && pokemonsAttack[2]!!.moves.size >= 4) ||
                (opponentsAttacks[0] != null && data.name == opponentsAttacks[0]!!.name && opponentsAttacks[0]!!.moves.size >= 4) ||
                (opponentsAttacks[1] != null && data.name == opponentsAttacks[1]!!.name && opponentsAttacks[1]!!.moves.size >= 4) ||
                (opponentsAttacks[2] != null && data.name == opponentsAttacks[2]!!.name && opponentsAttacks[2]!!.moves.size >= 4)
        return test
    }

    fun fillAttacks(move: Move, name: String, nb: Int)
    {
        if (pokemons[0] != null && name == pokemons[0]!!.name && nb == 1) {
            if (checkMoves(pokemons[0]!!))
            {
                return
            }
            if(pokemonsAttack[0] == null)
                pokemonsAttack[0] = PokemonMove(pokemons[0]!!.name, mutableListOf())
            pokemonsAttack[0]!!.moves.add(move)

            if (pokemonsAttack[0]!!.moves.size > 0)
                battle_fragment_attack_first_btn.text = pokemonsAttack[0]!!.moves[0].name
            if (pokemonsAttack[0]!!.moves.size > 1)
                battle_fragment_attack_second_btn.text = pokemonsAttack[0]!!.moves[1].name
            if (pokemonsAttack[0]!!.moves.size > 2)
                battle_fragment_attack_third_btn.text = pokemonsAttack[0]!!.moves[2].name
            if (pokemonsAttack[0]!!.moves.size > 3)
                battle_fragment_attack_fourth_btn.text = pokemonsAttack[0]!!.moves[3].name


        } else if (pokemons[1] != null && name == pokemons[1]!!.name && nb == 2) {
            if (checkMoves(pokemons[1]!!))
            {
                return
            }
            if(pokemonsAttack[1] == null)
                pokemonsAttack[1] = PokemonMove(pokemons[1]!!.name, mutableListOf())
            pokemonsAttack[1]!!.moves.add(move)

        } else if (pokemons[2] != null && name == pokemons[2]!!.name && nb == 3) {
            if (checkMoves(pokemons[2]!!))
            {
                return
            }
            if(pokemonsAttack[2] == null)
                pokemonsAttack[2] = PokemonMove(pokemons[2]!!.name, mutableListOf())
            pokemonsAttack[2]!!.moves.add(move)

        } else if (opponents[0] != null && name == opponents[0]!!.name && nb == 4) {
            if (checkMoves(opponents[0]!!))
            {
                return
            }
            if(opponentsAttacks[0] == null)
                opponentsAttacks[0] = PokemonMove(opponents[0]!!.name, mutableListOf())
            opponentsAttacks[0]!!.moves.add(move)

        } else if (opponents[1] != null && name == opponents[1]!!.name && nb == 5) {
            if (checkMoves(opponents[1]!!))
            {
                return
            }
            if(opponentsAttacks[1] == null)
                opponentsAttacks[1] = PokemonMove(opponents[1]!!.name, mutableListOf())
            opponentsAttacks[1]!!.moves.add(move)

        } else if (opponents[2] != null && name == opponents[2]!!.name && nb == 6) {
            if (checkMoves(opponents[2]!!))
            {
                return
            }
            if(opponentsAttacks[2] == null)
                opponentsAttacks[2] = PokemonMove(opponents[2]!!.name, mutableListOf())
            opponentsAttacks[2]!!.moves.add(move)

        }
    }


    /*
    * Add logic
    * */

    //return 0 if not end, -1 if loose, 1 if win
    fun checkEnd(): Int
    {
        val opponentsLife = getBaseStat(opponents[0]!!, "hp") +
                            getBaseStat(opponents[1]!!, "hp") +
                            getBaseStat(opponents[2]!!, "hp")

        val teamLife = getBaseStat(pokemons[0]!!, "hp") +
                        getBaseStat(pokemons[1]!!, "hp") +
                        getBaseStat(pokemons[2]!!, "hp")

        if (opponentsLife > 0 && teamLife > 0)
            return 0

        if (opponentsLife < 0)
            return 1

        return -1
    }

    fun isCurrentFirst(): Boolean {
        return getBaseStat(pokemons[currentPokemon]!!, "speed") >= getBaseStat(opponents[currentOpponent]!!, "speed")
    }

    fun attackEnemy(damage: Int) {
        var life = removeHP(opponents[currentOpponent]!!, damage)
        if (life <= 0)
        {
            battle_fragment_opponent_life_txt.text = "hp: 0"
            val handler = Handler()
            handler.postDelayed(Runnable {
                if (checkEnd() == 1)
                {
                    //TODO Show WIN blabla
                    (activity as BattleInteractions).goBackToLobby()
                }
                else
                {
                    changeEnemyPokemon()
                }
            }, 1000)
        }
        else
        {
            battle_fragment_opponent_life_txt.text = "hp: " + life.toString()
        }
    }

    fun attackTeam(damage: Int) {
        var life = removeHP(pokemons[currentPokemon]!!, damage)
        if (life <= 0)
        {
            battle_fragment_current_life_txt.text = "hp: 0"
            val handler = Handler()
            handler.postDelayed(Runnable {
                if (checkEnd() == -1)
                {
                    //TODO Show Loose blabla
                    (activity as BattleInteractions).goBackToLobby()
                }
                else
                {
                    forceChangePokemon()
                }
            }, 1000)
        }
        else
        {
            battle_fragment_current_life_txt.text = "hp: " + life.toString()
        }
    }

    fun changeEnemyPokemon()
    {
        if (currentOpponent == 0)
            battle_fragment_opponent_first_img.setBackgroundColor(resources.getColor(R.color.background_battle_dead))
        if (currentOpponent == 1)
            battle_fragment_opponent_second_img.setBackgroundColor(resources.getColor(R.color.background_battle_dead))
        //should never happen
        if (currentOpponent == 2)
            battle_fragment_opponent_third_img.setBackgroundColor(resources.getColor(R.color.background_battle_dead))

        currentOpponent++

        //can't use Glide so..
        DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_opponent_image_img))
            .execute(opponents[currentOpponent]!!.sprites.front_default);
        battle_fragment_opponent_life_txt.text = "hp: " + getBaseStat(opponents[currentOpponent]!!, "hp").toString()
        battle_fragment_opponent_name_txt.text = opponents[currentOpponent]!!.name
        battle_fragment_opponent_type_img.setImageResource(getImageFromType(opponents[currentOpponent]!!.types[0].type.name))
    }

    fun forceChangePokemon() {
        canAttack = false
        if (currentPokemon == 0)
            battle_fragment_pokemon_team_first_img.setBackgroundColor(resources.getColor(R.color.background_battle_dead))
        if (currentPokemon == 1)
            battle_fragment_pokemon_team_second_img.setBackgroundColor(resources.getColor(R.color.background_battle_dead))
        if (currentPokemon == 2)
            battle_fragment_pokemon_team_third_img.setBackgroundColor(resources.getColor(R.color.background_battle_dead))

        battle_fragment_attack_first_btn.text = ""
        battle_fragment_attack_second_btn.text = ""
        battle_fragment_attack_third_btn.text = ""
        battle_fragment_attack_fourth_btn.text = ""
    }

    fun changePokemon(selectedPokemon: Int): Boolean
    {
        if (currentPokemon == selectedPokemon)
            return false

        if (currentPokemon == 0)
            battle_fragment_pokemon_team_first_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))
        if (currentPokemon == 1)
            battle_fragment_pokemon_team_second_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))
        if (currentPokemon == 2)
            battle_fragment_pokemon_team_third_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))

        currentPokemon = selectedPokemon

        if (currentPokemon == 0)
            battle_fragment_pokemon_team_first_img.setBackgroundColor(resources.getColor(R.color.background_lobby_select))
        if (currentPokemon == 1)
            battle_fragment_pokemon_team_second_img.setBackgroundColor(resources.getColor(R.color.background_lobby_select))
        if (currentPokemon == 2)
            battle_fragment_pokemon_team_third_img.setBackgroundColor(resources.getColor(R.color.background_lobby_select))


        //can't use Glide so..
        if (pokemons[currentPokemon]!!.sprites.back_default != null)
        {
            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_current_image_img))
                .execute(pokemons[currentPokemon]!!.sprites.back_default);
        }
        else
        {
            DownloadImageTask(activity!!.findViewById(R.id.battle_fragment_current_image_img))
                .execute(pokemons[currentPokemon]!!.sprites.front_default);
        }

        battle_fragment_current_life_txt.text = "hp: " + getBaseStat(pokemons[currentPokemon]!!, "hp").toString()
        battle_fragment_current_name_txt.text = pokemons[currentPokemon]!!.name
        battle_fragment_current_type_img.setImageResource(getImageFromType(pokemons[currentPokemon]!!.types[0].type.name))

        return true
    }


    interface BattleInteractions {
        fun goBackToLobby()
    }

}
