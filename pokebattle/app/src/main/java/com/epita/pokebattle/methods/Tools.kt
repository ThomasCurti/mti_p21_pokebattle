package com.epita.pokebattle.methods

import android.util.Log
import com.epita.pokebattle.R
import com.epita.pokebattle.model.Pokemon
import com.epita.pokebattle.model.TypeUrlName

val images = mapOf("poison" to R.drawable.poison,
    "bug" to R.drawable.bug,
    "dark" to R.drawable.dark,
    "dragon" to R.drawable.dragon,
    "electric" to R.drawable.electric,
    "fighting" to R.drawable.fighting,
    "fire" to R.drawable.fire,
    "flying" to R.drawable.flying,
    "ghost" to R.drawable.ghost,
    "grass" to R.drawable.grass,
    "ground" to R.drawable.ground,
    "ice" to R.drawable.ice,
    "normal" to R.drawable.normal,
    "psychic" to R.drawable.psychic,
    "rock" to R.drawable.rock,
    "steel" to R.drawable.steel,
    "water" to R.drawable.water,
    "fairy" to R.drawable.fairy)

public fun getImageFromType(type: String): Int
{
    if (!(images.containsKey(type)))
    {
        Log.w("NOT FOUND", "type: $type")
        return R.drawable.ic_launcher_foreground
    }
    return images.getValue(type)
}

public fun getBaseStat(pokemon: Pokemon, type: String): Int
{
    for (stat in pokemon.stats)
    {
        if (stat.stat.name == type)
            return stat.base_stat
    }
    return 0
}

public fun removeHP(pokemon: Pokemon, hp: Int): Int
{
    for (stat in pokemon.stats)
    {
        if (stat.stat.name == "hp")
        {
            stat.base_stat = stat.base_stat - hp
            if (stat.base_stat < 0)
                stat.base_stat = 0
            return stat.base_stat
        }
    }
    return 0
}

public fun fixDataType(list: List<TypeUrlName>) : List<Array<TypeUrlName>>
{
    var res: MutableList<Array<TypeUrlName>> = mutableListOf()
    var current = 0
    var currentArray = mutableListOf<TypeUrlName>()

    for (item in list)
    {
        if (current == 3)
        {
            current = 0
            res.add(currentArray.toTypedArray())
            currentArray.clear()
        }
        currentArray.add(item)
        current++
    }

    if (currentArray.size != 0)
        res.add(currentArray.toTypedArray())

    return res
}