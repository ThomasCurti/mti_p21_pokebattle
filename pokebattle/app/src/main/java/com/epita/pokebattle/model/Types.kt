package com.epita.pokebattle.model

import java.util.*

public data class TypeUrlName (
    val name: String,
    val url: String
)

public data class InfoType(
    val double_damage_from: List<TypeUrlName>,
    val double_damage_to: List<TypeUrlName>,
    val half_damage_from: List<TypeUrlName>,
    val half_damage_to: List<TypeUrlName>,
    val no_damage_from: List<TypeUrlName>,
    val no_damage_to: List<TypeUrlName>
)

public data class Types (
    val damage_relations: InfoType,
    val game_indices: List<kotlin.Any>,
    val generation: kotlin.Any,
    val id: Int,
    val move_damage_class: kotlin.Any,
    val moves: List<kotlin.Any>,
    val name: String,
    val names: List<kotlin.Any>,
    val pokemon: List<kotlin.Any>
)