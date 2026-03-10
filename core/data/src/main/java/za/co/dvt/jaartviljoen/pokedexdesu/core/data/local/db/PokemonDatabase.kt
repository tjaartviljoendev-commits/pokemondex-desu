package za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.dao.PokemonDao
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.entity.PokemonDetailEntity
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.entity.PokemonEntity

@Database(
    entities = [PokemonEntity::class, PokemonDetailEntity::class],
    version = 3,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}