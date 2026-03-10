package za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_detail")
data class PokemonDetailEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String?,
    val height: Int,
    val weight: Int,
    val baseExperience: Int,
    val statsJson: String,
    val abilitiesJson: String,
    val typesJson: String
)
