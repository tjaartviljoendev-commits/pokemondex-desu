package za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model

data class PokemonDetail(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val height: Int,
    val weight: Int,
    val baseExperience: Int,
    val stats: List<PokemonStat>,
    val abilities: List<String>,
    val types: List<String>,
)
