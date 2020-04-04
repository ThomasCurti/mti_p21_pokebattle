package com.epita.pokebattle.model

import java.util.*

data class TypeUrlName (
    val name: String,
    val url: String
)

data class InfoType(
    val double_damage_from: List<TypeUrlName>,
    val double_damage_to: List<TypeUrlName>,
    val half_damage_from: List<TypeUrlName>,
    val half_damage_to: List<TypeUrlName>,
    val no_damage_from: List<TypeUrlName>,
    val no_damage_to: List<TypeUrlName>
)

data class Types (
    val damage_relations: InfoType,
    val game_indices: List<Objects>,
    val generation: Objects,
    val id: Int,
    val move_damage_class: Objects,
    val moves: List<Objects>,
    val name: String,
    val names: List<Objects>,
    val pokemon: List<Objects>
)