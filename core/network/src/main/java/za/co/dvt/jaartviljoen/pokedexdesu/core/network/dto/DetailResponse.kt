package za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    @Json(name = "base_experience") val baseExperience: Int,
    val sprites: SpritesResponse,
    val stats: List<StatEntryResponse>,
    val abilities: List<AbilityEntryResponse>,
    val types: List<TypeEntryResponse>
)

@JsonClass(generateAdapter = true)
data class SpritesResponse(
    @Json(name = "front_default") val frontDefault: String?,
    val other: OtherSpritesResponse?
)

@JsonClass(generateAdapter = true)
data class OtherSpritesResponse(
    @Json(name = "official-artwork") val officialArtwork: OfficialArtworkResponse?
)

@JsonClass(generateAdapter = true)
data class OfficialArtworkResponse(
    @Json(name = "front_default") val frontDefault: String?
)

@JsonClass(generateAdapter = true)
data class StatEntryResponse(
    @Json(name = "base_stat") val baseStat: Int,
    val effort: Int,
    val stat: NamedApiResource
)

@JsonClass(generateAdapter = true)
data class AbilityEntryResponse(
    val ability: NamedApiResource,
    @Json(name = "is_hidden") val isHidden: Boolean,
    val slot: Int
)

@JsonClass(generateAdapter = true)
data class TypeEntryResponse(
    val slot: Int,
    val type: NamedApiResource
)

@JsonClass(generateAdapter = true)
data class NamedApiResource(
    val name: String,
    val url: String
)
