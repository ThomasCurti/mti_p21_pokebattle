package com.epita.pokebattle.model

data class TypeItem (
    val id: Int,
    val name: String
    )

data class PokemonListItem (
    val id: Int,
    val name: String,
    val sprite: String,
    val types: Array<TypeItem>
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PokemonListItem

        if (id != other.id) return false
        if (name != other.name) return false
        if (sprite != other.sprite) return false
        if (!types.contentEquals(other.types)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + sprite.hashCode()
        result = 31 * result + types.contentHashCode()
        return result
    }
}