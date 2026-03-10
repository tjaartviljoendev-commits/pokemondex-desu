package za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String?,
    val hp: Int? = null,
    val attack: Int? = null,
    val typesJson: String? = null,
    val abilitiesJson: String? = null,
)
