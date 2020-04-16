package com.epita.pokebattle

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.epita.pokebattle.methods.*
import com.epita.pokebattle.model.Move
import com.epita.pokebattle.model.Move.PokemonMove
import com.epita.pokebattle.model.Pokemon
import com.epita.pokebattle.model.Types
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

    var types: MutableList<Types> = mutableListOf()

    var currentPokemon: Int = 0
    var currentOpponent: Int = 0
    var canAttack: Boolean = true;

    var mustWait: Boolean = false

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

        //18 types in Pokemon
        val serviceType: WSinterfaceTypes = retrofit.create(WSinterfaceTypes::class.java)
        var index = 1
        while (index < 19)
        {
            serviceType.getTypes(index.toString()).enqueue(wsCallbackGetTypes)
            index++
        }

        /*
         * Add logic
         * */
        battle_fragment_attack_first_btn.setOnClickListener {
            onAttackClick(0)
        }

        battle_fragment_attack_second_btn.setOnClickListener {
            onAttackClick(1)
        }

        battle_fragment_attack_third_btn.setOnClickListener {
            onAttackClick(2)
        }

        battle_fragment_attack_fourth_btn.setOnClickListener {
            onAttackClick(3)
        }

        battle_fragment_pokemon_team_first_img.setOnClickListener {
            onChangePokemonClick(0)
        }

        battle_fragment_pokemon_team_second_img.setOnClickListener {
            onChangePokemonClick(1)
        }

        battle_fragment_pokemon_team_third_img.setOnClickListener {
            onChangePokemonClick(2)
        }

    }
    /*
     * Get all datas
     * */
    val wsCallbackGetPokemon: Callback<Pokemon> = object : Callback<Pokemon> {
            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                try
                {
                    Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
                    Log.e("WSPokemon", " WebService call failed " + t.message)
                }
                catch (e: Exception)
                {

                }
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

    val wsCallbackGetTypes: Callback<Types> = object : Callback<Types> {
        override fun onFailure(call: Call<Types>, t: Throwable) {
            Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
            Log.e("WSType", " WebService call failed " + t.message)
        }

        override fun onResponse(call: Call<Types>, response:
        Response<Types>
        ) {
            if (response.code() == 200) {
                val responseData = response.body()
                if (responseData != null) {
                    Log.d("WS type", "WebService success : $responseData items found")
                }
                else {
                    Toast.makeText(context, "No item found, check your internet connection", Toast.LENGTH_LONG).show()
                    Log.w("WS type", "WebService success : but no item found")
                    return
                }

                types.add(responseData)

            }
            else
            {
                Toast.makeText(context, "Error, check your internet connection", Toast.LENGTH_LONG).show()
                Log.e("WS type", "WebService failed " + response.code() + " " + response.body() + "\n" + response.errorBody())
            }

        }
    }

    interface WSinterfaceTypes {
        @GET("type/{id}")
        fun getTypes(@Path("id") name: String): Call<Types>
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
                Glide.with(this).load(data.sprites.back_default).into(battle_fragment_current_image_img)
            }
            else
            {
                Glide.with(this).load(data.sprites.front_default).into(battle_fragment_current_image_img)
            }

            battle_fragment_current_life_txt.text = "hp: " + getBaseStat(data, "hp").toString()
            battle_fragment_current_name_txt.text = data.name
            battle_fragment_current_type_img.setImageResource(getImageFromType(data.types[0].type.name))

            Glide.with(this).load(data.sprites.front_default).into(battle_fragment_pokemon_team_first_img)

            //deprecated since API 23 but we are using API 21 so we must use it
            battle_fragment_pokemon_team_first_img.setBackgroundColor(resources.getColor(R.color.background_lobby_select))

            pokemons[0] = data
            return 1
        }

        else if (data.name == secondName && pokemons[1] == null) {
            pokemons[1] = data

            Glide.with(this).load(data.sprites.front_default).into(battle_fragment_pokemon_team_second_img)

            //deprecated since API 23 but we are using API 21 so we must use it
            battle_fragment_pokemon_team_second_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))

            return 2
        }

        else if (data.name == thirdName && pokemons[2] == null) {
            pokemons[2] = data

            Glide.with(this).load(data.sprites.front_default).into(battle_fragment_pokemon_team_third_img)

            //deprecated since API 23 but we are using API 21 so we must use it
            battle_fragment_pokemon_team_third_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))

            return 3
        }

        else if (data.name == firstOpponentName && opponents[0] == null) {
            opponents[0] = data


            Glide.with(this).load(data.sprites.front_default).into(battle_fragment_opponent_image_img)

            battle_fragment_opponent_life_txt.text = "hp: " + getBaseStat(data, "hp").toString()
            battle_fragment_opponent_name_txt.text = data.name
            battle_fragment_opponent_type_img.setImageResource(getImageFromType(data.types[0].type.name))

            Glide.with(this).load(data.sprites.front_default).into(battle_fragment_opponent_first_img)

            //deprecated since API 23 but we are using API 21 so we must use it
            battle_fragment_opponent_first_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))

            return 4
        }

        else if (data.name == secondOpponentName && opponents[1] == null) {
            opponents[1] = data

            Glide.with(this).load(data.sprites.front_default).into(battle_fragment_opponent_second_img)

            //deprecated since API 23 but we are using API 21 so we must use it
            battle_fragment_opponent_second_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))
            return 5
        }

        else if (data.name == thirdOpponentName && opponents[2] == null) {
            opponents[2] = data

            Glide.with(this).load(data.sprites.front_default).into(battle_fragment_opponent_third_img)

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
                Log.e("WSMove", "WebService call failed " + t.message)
            }
            override fun onResponse(call: Call<Move>, response:
            Response<Move>
            ) {
                if (response.code() == 200) {
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d("WSMove", "WebService success : item found")
                    }
                    else {
                        Toast.makeText(context, "No item found, check your internet connection", Toast.LENGTH_LONG).show()
                        Log.w("WSMove", "WebService success : but no item found")
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
                    Log.w("WSMove", "WebService failed")
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

    fun checkMoves(data: Pokemon, nb: Int): Boolean
    {
        val test = (nb == 1 && pokemonsAttack[0] != null && data.name == pokemonsAttack[0]!!.name && pokemonsAttack[0]!!.moves.size >= 4) ||
                (nb == 2 && pokemonsAttack[1] != null && data.name == pokemonsAttack[1]!!.name && pokemonsAttack[1]!!.moves.size >= 4) ||
                (nb == 3 && pokemonsAttack[2] != null && data.name == pokemonsAttack[2]!!.name && pokemonsAttack[2]!!.moves.size >= 4) ||
                (nb == 4 && opponentsAttacks[0] != null && data.name == opponentsAttacks[0]!!.name && opponentsAttacks[0]!!.moves.size >= 4) ||
                (nb == 5 && opponentsAttacks[1] != null && data.name == opponentsAttacks[1]!!.name && opponentsAttacks[1]!!.moves.size >= 4) ||
                (nb == 6 && opponentsAttacks[2] != null && data.name == opponentsAttacks[2]!!.name && opponentsAttacks[2]!!.moves.size >= 4)
        return test
    }

    fun fillAttacks(move: Move, name: String, nb: Int)
    {
        if (pokemons[0] != null && name == pokemons[0]!!.name && nb == 1) {
            addAttackPokemons(0, move, nb)

            if (pokemonsAttack[0]!!.moves.size > 0)
                battle_fragment_attack_first_btn.text = pokemonsAttack[0]!!.moves[0].name
            if (pokemonsAttack[0]!!.moves.size > 1)
                battle_fragment_attack_second_btn.text = pokemonsAttack[0]!!.moves[1].name
            if (pokemonsAttack[0]!!.moves.size > 2)
                battle_fragment_attack_third_btn.text = pokemonsAttack[0]!!.moves[2].name
            if (pokemonsAttack[0]!!.moves.size > 3)
                battle_fragment_attack_fourth_btn.text = pokemonsAttack[0]!!.moves[3].name


        } else if (pokemons[1] != null && name == pokemons[1]!!.name && nb == 2) {
            addAttackPokemons(1, move, nb)

        } else if (pokemons[2] != null && name == pokemons[2]!!.name && nb == 3) {
            addAttackPokemons(2, move, nb)

        } else if (opponents[0] != null && name == opponents[0]!!.name && nb == 4) {
            addAttackOpponent(0, move, nb)

        } else if (opponents[1] != null && name == opponents[1]!!.name && nb == 5) {
            addAttackOpponent(1, move, nb)

        } else if (opponents[2] != null && name == opponents[2]!!.name && nb == 6) {
            addAttackOpponent(2, move, nb)

        }
    }

    fun addAttackPokemons(nbAttack: Int, move: Move, nbId: Int)
    {
        if (checkMoves(pokemons[nbAttack]!!, nbId)) {
            return
        }
        if (pokemonsAttack[nbAttack] == null) {
            pokemonsAttack[nbAttack] = PokemonMove(pokemons[nbAttack]!!.name, mutableListOf())
        }
        pokemonsAttack[nbAttack]!!.moves.add(move)
    }

    fun addAttackOpponent(nbAttack: Int, move: Move, nbId: Int)
    {
        if (checkMoves(opponents[nbAttack]!!, nbId))
        {
            return
        }
        if(opponentsAttacks[nbAttack] == null)
            opponentsAttacks[nbAttack] = PokemonMove(opponents[nbAttack]!!.name, mutableListOf())
        opponentsAttacks[nbAttack]!!.moves.add(move)
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

        if (opponentsLife <= 0)
            return 1

        return -1
    }

    fun isCurrentFirst(): Boolean
    {
        return getBaseStat(pokemons[currentPokemon]!!, "speed") >= getBaseStat(opponents[currentOpponent]!!, "speed")
    }

    fun attackEnemy(damage: Int, accuracy: Int, attackName: String)
    {
        if (getBaseStat(pokemons[currentPokemon]!!, "hp") <= 0)
        {
            return
        }

        if (damage <= 0)
        {
            Toast.makeText(context, pokemons[currentPokemon]!!.name + " use " + attackName + " but make no damage", Toast.LENGTH_SHORT).show()
            return
        }

        var value = Random.nextInt(100)
        if (accuracy >= value)
            Toast.makeText(context, pokemons[currentPokemon]!!.name + " use " + attackName, Toast.LENGTH_SHORT).show()
        else
        {
            Toast.makeText(context, pokemons[currentPokemon]!!.name + " fail his attack", Toast.LENGTH_SHORT).show()
            return
        }

        var life = removeHP(opponents[currentOpponent]!!, damage)
        if (life <= 0)
        {
            battle_fragment_opponent_life_txt.text = "hp: 0"
            val handler = Handler()
            handler.postDelayed(Runnable {
                if (checkEnd() == 1)
                {
                    battle_fragment_win_loose_txt.text = "You Win !"

                    val handler =  Handler()
                    handler.postDelayed({
                        (activity as BattleInteractions).goBackToLobby()
                    }, 2000)
                }
                else
                {
                    try {
                        changeEnemyPokemon()
                    }
                    catch (e: Exception) {
                        //should never happend except if the player press the back button after an attack
                    }

                }
            }, 2000)
        }
        else
        {
            battle_fragment_opponent_life_txt.text = "hp: " + life.toString()
        }
    }

    fun attackTeam(damage: Int, accuracy: Int, attackName: String)
    {
        if (getBaseStat(opponents[currentOpponent]!!, "hp") <= 0)
        {
            return
        }

        if (damage <= 0)
        {
            Toast.makeText(context, opponents[currentOpponent]!!.name + " use " + attackName + " but make no damage", Toast.LENGTH_SHORT).show()
            return
        }

        var value = Random.nextInt(100)
        if (accuracy >= value)
            Toast.makeText(context, opponents[currentOpponent]!!.name + " use " + attackName, Toast.LENGTH_SHORT).show()
        else
        {
            Toast.makeText(context, opponents[currentOpponent]!!.name + " fail his attack", Toast.LENGTH_SHORT).show()
            return
        }

        var life = removeHP(pokemons[currentPokemon]!!, damage)
        if (life <= 0)
        {
            battle_fragment_current_life_txt.text = "hp: 0"
            val handler = Handler()
            handler.postDelayed(Runnable {
                if (checkEnd() == -1)
                {
                    battle_fragment_win_loose_txt.text = "You Loose !"

                    val handler =  Handler()
                    handler.postDelayed({
                        (activity as BattleInteractions).goBackToLobby()
                    }, 2000)
                }
                else
                {
                    try {
                        forceChangePokemon()
                    }
                    catch (e: Exception) {
                        //should never happend except if the player press the back button after an enemy attack
                    }
                }
            }, 2000)
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


        Glide.with(this).load(opponents[currentOpponent]!!.sprites.front_default).into(battle_fragment_opponent_image_img)

        battle_fragment_opponent_life_txt.text = "hp: " + getBaseStat(opponents[currentOpponent]!!, "hp").toString()
        battle_fragment_opponent_name_txt.text = opponents[currentOpponent]!!.name
        battle_fragment_opponent_type_img.setImageResource(getImageFromType(opponents[currentOpponent]!!.types[0].type.name))
    }

    fun forceChangePokemon()
    {
        canAttack = false
        mustWait = false
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

        val selectedLife = getBaseStat(pokemons[selectedPokemon]!!, "hp")
        if (selectedLife < 0)
            return false

        val life = getBaseStat(pokemons[currentPokemon]!!, "hp")
        if (currentPokemon == 0 && life > 0)
            battle_fragment_pokemon_team_first_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))
        if (currentPokemon == 1 && life > 0)
            battle_fragment_pokemon_team_second_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))
        if (currentPokemon == 2 && life > 0)
            battle_fragment_pokemon_team_third_img.setBackgroundColor(resources.getColor(R.color.background_battle_unselected))

        currentPokemon = selectedPokemon

        if (currentPokemon == 0)
            battle_fragment_pokemon_team_first_img.setBackgroundColor(resources.getColor(R.color.background_lobby_select))
        if (currentPokemon == 1)
            battle_fragment_pokemon_team_second_img.setBackgroundColor(resources.getColor(R.color.background_lobby_select))
        if (currentPokemon == 2)
            battle_fragment_pokemon_team_third_img.setBackgroundColor(resources.getColor(R.color.background_lobby_select))


        if (pokemons[currentPokemon]!!.sprites.back_default != null)
        {
            Glide.with(this).load(pokemons[currentPokemon]!!.sprites.back_default).into(battle_fragment_current_image_img)
        }
        else
        {
            Glide.with(this).load(pokemons[currentPokemon]!!.sprites.front_default).into(battle_fragment_current_image_img)

        }

        battle_fragment_current_life_txt.text = "hp: " + getBaseStat(pokemons[currentPokemon]!!, "hp").toString()
        battle_fragment_current_name_txt.text = pokemons[currentPokemon]!!.name
        battle_fragment_current_type_img.setImageResource(getImageFromType(pokemons[currentPokemon]!!.types[0].type.name))

        if (pokemonsAttack[currentPokemon]!!.moves.size > 0)
            battle_fragment_attack_first_btn.text = pokemonsAttack[currentPokemon]!!.moves[0].name
        else
            battle_fragment_attack_first_btn.text = ""

        if (pokemonsAttack[currentPokemon]!!.moves.size > 1)
            battle_fragment_attack_second_btn.text = pokemonsAttack[currentPokemon]!!.moves[1].name
        else
            battle_fragment_attack_second_btn.text = ""

        if (pokemonsAttack[currentPokemon]!!.moves.size > 2)
            battle_fragment_attack_third_btn.text = pokemonsAttack[currentPokemon]!!.moves[2].name
        else
            battle_fragment_attack_third_btn.text = ""

        if (pokemonsAttack[currentPokemon]!!.moves.size > 3)
            battle_fragment_attack_fourth_btn.text = pokemonsAttack[currentPokemon]!!.moves[3].name
        else
            battle_fragment_attack_fourth_btn.text = ""

        return true
    }

    fun getTypeDamageMultiplier(fromType: String, toType: String): Float
    {
        for (type in types)
        {
            if (type.name == fromType)
            {
                for (t in type.damage_relations.double_damage_to)
                    if (t.name == toType)
                        return 2f
                for (t in type.damage_relations.half_damage_to)
                    if (t.name == toType)
                        return 1.5f
                return 1f
            }
        }
        return 1f
    }

    fun getDamageTo(selectedAttack: Int, from: PokemonMove, fromPokemon: Pokemon, toPokemon: Pokemon): Int
    {
        //get type multiplier
        var typeMultiplier = getTypeDamageMultiplier(
            from.moves[selectedAttack].type.name,
            toPokemon.types[0].type.name
        )
        if (toPokemon.types.size > 1)
        {
            val typeMultiplier2 = getTypeDamageMultiplier(
                from.moves[selectedAttack].type.name,
                toPokemon.types[1].type.name
            )
            if (typeMultiplier < typeMultiplier2)
                typeMultiplier = typeMultiplier2
        }

        //get defender defense
        val defense = getBaseStat(toPokemon, "defense")

        //get move power
        val power = from.moves[selectedAttack].power

        //get attack value => depends on physical or special
        var attack = 0
        if (from.moves[selectedAttack].damage_class.name == "physical")
            attack = getBaseStat(fromPokemon, "attack")
        else
            attack = getBaseStat(fromPokemon, "special-attack")

        return ((attack / 10 + power - defense) * typeMultiplier).toInt()
    }

    fun AIAttack()
    {
        var maxIndex = 0
        var maxAttack = 0
        var index = 0
        while (index < opponentsAttacks[currentOpponent]!!.moves.size)
        {
            var damage = getDamageTo(
                index,
                opponentsAttacks[currentOpponent]!!,
                opponents[currentOpponent]!!,
                pokemons[currentPokemon]!!
            )
            if (damage > maxAttack)
            {
                maxAttack = damage
                maxIndex = index
            }
            index++
        }

        attackTeam(
            maxAttack,
            opponentsAttacks[currentOpponent]!!.moves[maxIndex].accuracy,
            opponentsAttacks[currentOpponent]!!.moves[maxIndex].name
        )
    }

    fun IsGameLoaded(): Boolean
    {
        for (pokemon in pokemons)
        {
            if (pokemon == null)
                return false
        }

        for (opponent in opponents)
        {
            if (opponent == null)
                return false
        }

        for (attack in pokemonsAttack)
        {
            if (attack == null)
                return false
        }

        for (attack in opponentsAttacks)
        {
            if (attack == null)
                return false
        }

        return true
    }

    fun onAttackClick(buttonNb: Int)
    {
        if (!IsGameLoaded())
        {
            return
        }

        if (!canAttack)
        {
            try {
                Toast.makeText(context, "You can't attack!", Toast.LENGTH_SHORT).show()
            }
            catch (e: Exception)
            {
                //should never happen except if the player press the back button during an attack
            }
            return
        }

        if (!mustWait && pokemonsAttack[currentPokemon]!!.moves.size > buttonNb)
        {
            try {
                val handler = Handler()
                handler.postDelayed(Runnable {
                    mustWait = false
                }, 5000)

                mustWait = true
                val isFirst = isCurrentFirst()
                if (!isFirst)
                {
                    AIAttack()
                }
                val damage = getDamageTo(
                    buttonNb,
                    pokemonsAttack[currentPokemon]!!,
                    pokemons[currentPokemon]!!,
                    opponents[currentOpponent]!!
                )
                attackEnemy(
                    damage, pokemonsAttack[currentPokemon]!!.moves[buttonNb].accuracy,
                    pokemonsAttack[currentPokemon]!!.moves[buttonNb].name
                )
                if (isFirst)
                {
                    AIAttack()
                }
            }
            catch (e: Exception)
            {
                //should never happen except if the player press the back button during an attack
            }
        }
    }

    fun onChangePokemonClick(nb: Int)
    {
        if (mustWait)
            return

        if (changePokemon(nb))
        {
            mustWait = true
            val handler = Handler()
            handler.postDelayed(Runnable {
                mustWait = false
            }, 4000)

            canAttack = true

            handler.postDelayed(Runnable {
                try {
                    AIAttack()
                }
                catch (e: Exception)
                {
                    //should never happen except if the player press the back button during a pokemon change
                }

            }, 1000)
        }
    }

    interface BattleInteractions
    {
        fun goBackToLobby()
    }

}
