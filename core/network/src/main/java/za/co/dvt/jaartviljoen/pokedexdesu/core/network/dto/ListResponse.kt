package za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ListItem>
)

@JsonClass(generateAdapter = true)
data class ListItem(
    val name: String,
    val url: String
)
