package za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val hp: Int? = null,
    val attack: Int? = null,
    val types: List<String> = emptyList(),
    val abilities: List<String> = emptyList(),
)
