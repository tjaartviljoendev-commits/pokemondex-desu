package za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.entity.PokemonDetailEntity
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.entity.PokemonEntity

@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemon ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getPokemonList(limit: Int, offset: Int): List<PokemonEntity>

    @Query("SELECT * FROM pokemon_detail WHERE id = :id")
    suspend fun getPokemonDetail(id: Int): PokemonDetailEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPokemonList(list: List<PokemonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPokemonList(list: List<PokemonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonDetail(detail: PokemonDetailEntity)

    @Query("DELETE FROM pokemon")
    suspend fun clearPokemonList()

    @Query("DELETE FROM pokemon_detail")
    suspend fun clearPokemonDetails()

    @Query("SELECT COUNT(*) FROM pokemon")
    suspend fun getPokemonCount(): Int

    @Query("SELECT * FROM pokemon WHERE name LIKE '%' || :query || '%' ORDER BY id ASC LIMIT 50")
    suspend fun searchByName(query: String): List<PokemonEntity>
}