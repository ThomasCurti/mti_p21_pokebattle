package com.epita.pokebattle.model

import java.lang.reflect.GenericArrayType
import java.util.*

data class ObjNameUrl (
    val name: String,
    val url: String
)

data class Move (
    val accuracy: Int,
    val contest_combos: Objects,
    val contest_effect: Objects,
    val contest_type: Objects,
    val damage_class: ObjNameUrl,
    val effect_chance: Int,
    val effect_changes: Array<Objects>,
    val effect_entries: Array<Objects>,
    val flavor_text_entries: Array<Objects>,
    val generation: Objects,
    val id: Int,
    val machine: Array<Objects>,
    val meta: Objects,
    val name: String,
    val names: Array<Objects>,
    val past_values: Array<Objects>,
    val power: Int,
    val pp: Int,
    val priority: Int,
    val stat_changes: Array<Objects>,
    val super_contest_effect: Objects,
    val target: ObjNameUrl,
    val type: ObjNameUrl
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Move

        if (accuracy != other.accuracy) return false
        if (contest_combos != other.contest_combos) return false
        if (contest_effect != other.contest_effect) return false
        if (contest_type != other.contest_type) return false
        if (damage_class != other.damage_class) return false
        if (effect_chance != other.effect_chance) return false
        if (!effect_changes.contentEquals(other.effect_changes)) return false
        if (!effect_entries.contentEquals(other.effect_entries)) return false
        if (!flavor_text_entries.contentEquals(other.flavor_text_entries)) return false
        if (generation != other.generation) return false
        if (id != other.id) return false
        if (!machine.contentEquals(other.machine)) return false
        if (meta != other.meta) return false
        if (name != other.name) return false
        if (!names.contentEquals(other.names)) return false
        if (!past_values.contentEquals(other.past_values)) return false
        if (power != other.power) return false
        if (pp != other.pp) return false
        if (priority != other.priority) return false
        if (!stat_changes.contentEquals(other.stat_changes)) return false
        if (super_contest_effect != other.super_contest_effect) return false
        if (target != other.target) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = accuracy
        result = 31 * result + contest_combos.hashCode()
        result = 31 * result + contest_effect.hashCode()
        result = 31 * result + contest_type.hashCode()
        result = 31 * result + damage_class.hashCode()
        result = 31 * result + effect_chance.hashCode()
        result = 31 * result + effect_changes.contentHashCode()
        result = 31 * result + effect_entries.contentHashCode()
        result = 31 * result + flavor_text_entries.contentHashCode()
        result = 31 * result + generation.hashCode()
        result = 31 * result + id
        result = 31 * result + machine.contentHashCode()
        result = 31 * result + meta.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + names.contentHashCode()
        result = 31 * result + past_values.contentHashCode()
        result = 31 * result + power
        result = 31 * result + pp
        result = 31 * result + priority
        result = 31 * result + stat_changes.contentHashCode()
        result = 31 * result + super_contest_effect.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }


    data class PokemonMove (
        val name: String,
        val moves: MutableList<Move>
    )

}