package za.co.dvt.jaartviljoen.pokedexdesu.core.data.mapper

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.entity.PokemonDetailEntity
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.entity.PokemonEntity
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.PokemonDetail
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.PokemonStat
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.DetailResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.ListItem

private const val SPRITE_BASE_URL =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"

private val moshi: Moshi = Moshi.Builder().build()

private val stringListAdapter: JsonAdapter<List<String>> = moshi.adapter(
    Types.newParameterizedType(List::class.java, String::class.java)
)

private val statMapListAdapter: JsonAdapter<List<Map<String, Any>>> = moshi.adapter(
    Types.newParameterizedType(
        List::class.java,
        Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
    )
)

// e.g. "https://pokeapi.co/api/v2/pokemon/1/" -> 1
internal fun extractIdFromUrl(url: String): Int =
    url.trimEnd('/').substringAfterLast('/').toInt()

internal fun ListItem.toDomain(): Pokemon {
    val id = extractIdFromUrl(url)
    return Pokemon(
        id = id,
        name = name,
        imageUrl = "${SPRITE_BASE_URL}${id}.png",
    )
}

internal fun DetailResponse.toDomain(): PokemonDetail {
    val artworkUrl = sprites.other?.officialArtwork?.frontDefault
    return PokemonDetail(
        id = id,
        name = name,
        imageUrl = artworkUrl ?: sprites.frontDefault,
        height = height,
        weight = weight,
        baseExperience = baseExperience,
        stats = stats.map { PokemonStat(name = it.stat.name, baseStat = it.baseStat) },
        abilities = abilities.map { it.ability.name },
        types = types.map { it.type.name }
    )
}

internal fun PokemonDetail.toSummary(): Pokemon = Pokemon(
    id = id,
    name = name,
    imageUrl = imageUrl,
    hp = stats.firstOrNull { it.name == "hp" }?.baseStat,
    attack = stats.firstOrNull { it.name == "attack" }?.baseStat,
    types = types,
    abilities = abilities,
)

internal fun Pokemon.toEntity(): PokemonEntity = PokemonEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    hp = hp,
    attack = attack,
    typesJson = types.takeIf { it.isNotEmpty() }?.let(stringListAdapter::toJson),
    abilitiesJson = abilities.takeIf { it.isNotEmpty() }?.let(stringListAdapter::toJson),
)

internal fun PokemonEntity.toDomain(): Pokemon = Pokemon(
    id = id,
    name = name,
    imageUrl = imageUrl,
    hp = hp,
    attack = attack,
    types = typesJson?.let { stringListAdapter.fromJson(it) } ?: emptyList(),
    abilities = abilitiesJson?.let { stringListAdapter.fromJson(it) } ?: emptyList(),
)

internal fun PokemonDetail.toEntity(): PokemonDetailEntity {
    val statsAsMaps = stats.map {
        mapOf("name" to it.name, "baseStat" to it.baseStat)
    }
    return PokemonDetailEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        height = height,
        weight = weight,
        baseExperience = baseExperience,
        statsJson = statMapListAdapter.toJson(statsAsMaps),
        abilitiesJson = stringListAdapter.toJson(abilities),
        typesJson = stringListAdapter.toJson(types)
    )
}

internal fun PokemonDetailEntity.toDomain(): PokemonDetail {
    val statMaps = statMapListAdapter.fromJson(statsJson).orEmpty()
    val parsedStats = statMaps.map { map ->
        PokemonStat(
            name = map["name"] as? String ?: "",
            baseStat = (map["baseStat"] as? Double)?.toInt() ?: 0
        )
    }
    return PokemonDetail(
        id = id,
        name = name,
        imageUrl = imageUrl,
        height = height,
        weight = weight,
        baseExperience = baseExperience,
        stats = parsedStats,
        abilities = stringListAdapter.fromJson(abilitiesJson).orEmpty(),
        types = stringListAdapter.fromJson(typesJson).orEmpty()
    )
}
