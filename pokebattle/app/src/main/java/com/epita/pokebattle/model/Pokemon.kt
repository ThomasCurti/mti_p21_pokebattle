package com.epita.pokebattle.model

import java.util.*

data class PokemonSprites (
    val front_default: String,
    val front_female: String,
    val front_shiny: String,
    val front_shiny_female: String,
    val back_default: String,
    val back_female: String,
    val back_shiny: String,
    val back_shiny_female: String
)

data class TypePokemon (
    val slot: Int,
    val type: TypeNameUrl
)

data class TypeNameUrl (
    val name: String,
    val url: String
)

data class StatPokemon (
    val base_stat: Int,
    val effort: Int,
    val stat: StatNameUrl
)

data class StatNameUrl (
    val name: String,
    val url: String
)

data class Pokemon (
    val id: Int,
    val name: String,
    val base_experience: Int,
    val height: Int,
    val is_default: Boolean,
    val order: Int,
    val weight: Int,
    val abilities: Array<Objects>,
    val forms: Array<Objects>,
    val game_indices: Array<Objects>,
    val held_items: Array<Objects>,
    val location_area_encounters: String,
    val moves: Array<Objects>,
    val sprites: PokemonSprites,
    val species: Objects,
    val version_group: Objects,
    val names: Array<Objects>,
    val form_names: Array<Objects>,
    val stats: Array<StatPokemon>,
    val types: Array<TypePokemon>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pokemon

        if (id != other.id) return false
        if (name != other.name) return false
        if (base_experience != other.base_experience) return false
        if (height != other.height) return false
        if (is_default != other.is_default) return false
        if (order != other.order) return false
        if (weight != other.weight) return false
        if (!abilities.contentEquals(other.abilities)) return false
        if (!forms.contentEquals(other.forms)) return false
        if (!game_indices.contentEquals(other.game_indices)) return false
        if (!held_items.contentEquals(other.held_items)) return false
        if (location_area_encounters != other.location_area_encounters) return false
        if (!moves.contentEquals(other.moves)) return false
        if (sprites != other.sprites) return false
        if (species != other.species) return false
        if (version_group != other.version_group) return false
        if (!names.contentEquals(other.names)) return false
        if (!form_names.contentEquals(other.form_names)) return false
        if (!stats.contentEquals(other.stats)) return false
        if (!types.contentEquals(other.types)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + base_experience
        result = 31 * result + height
        result = 31 * result + is_default.hashCode()
        result = 31 * result + order
        result = 31 * result + weight
        result = 31 * result + abilities.contentHashCode()
        result = 31 * result + forms.contentHashCode()
        result = 31 * result + game_indices.contentHashCode()
        result = 31 * result + held_items.contentHashCode()
        result = 31 * result + location_area_encounters.hashCode()
        result = 31 * result + moves.contentHashCode()
        result = 31 * result + sprites.hashCode()
        result = 31 * result + species.hashCode()
        result = 31 * result + version_group.hashCode()
        result = 31 * result + names.contentHashCode()
        result = 31 * result + form_names.contentHashCode()
        result = 31 * result + stats.contentHashCode()
        result = 31 * result + types.contentHashCode()
        return result
    }
}