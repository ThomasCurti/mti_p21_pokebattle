package com.epita.pokebattle

import com.epita.pokebattle.model.PokemonListItem

interface HasList {
    fun binder(holder: Adapter.ViewHolder, position: Int, data : List<PokemonListItem>)
}