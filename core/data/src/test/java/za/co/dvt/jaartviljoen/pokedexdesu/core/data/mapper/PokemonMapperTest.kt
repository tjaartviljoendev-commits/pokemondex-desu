package za.co.dvt.jaartviljoen.pokedexdesu.core.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.entity.PokemonDetailEntity
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.PokemonDetail
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.PokemonStat
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.AbilityEntryResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.NamedApiResource
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.OfficialArtworkResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.OtherSpritesResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.DetailResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.ListItem
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.SpritesResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.StatEntryResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.TypeEntryResponse

class PokemonMapperTest {

    @Test
    fun `extractIdFromUrl extracts id from standard url with trailing slash`() {
        val id = extractIdFromUrl("https://pokeapi.co/api/v2/pokemon/1/")
        assertEquals(1, id)
    }

    @Test
    fun `extractIdFromUrl extracts id from url without trailing slash`() {
        val id = extractIdFromUrl("https://pokeapi.co/api/v2/pokemon/151")
        assertEquals(151, id)
    }

    @Test
    fun `extractIdFromUrl handles large ids correctly`() {
        val id = extractIdFromUrl("https://pokeapi.co/api/v2/pokemon/10001/")
        assertEquals(10001, id)
    }

    @Test
    fun `PokemonListItem toDomain maps name correctly`() {
        val item = ListItem(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/")
        val domain = item.toDomain()
        assertEquals("bulbasaur", domain.name)
    }

    @Test
    fun `PokemonListItem toDomain extracts correct id from url`() {
        val item = ListItem(name = "charizard", url = "https://pokeapi.co/api/v2/pokemon/6/")
        val domain = item.toDomain()
        assertEquals(6, domain.id)
    }

    @Test
    fun `PokemonListItem toDomain constructs sprite image url from extracted id`() {
        val item = ListItem(name = "mewtwo", url = "https://pokeapi.co/api/v2/pokemon/150/")
        val domain = item.toDomain()
        assertEquals(
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/150.png",
            domain.imageUrl
        )
    }

    @Test
    fun `PokemonDetailResponse toDomain maps scalar fields correctly`() {
        val response = createDetailResponse(id = 25, name = "pikachu", height = 4, weight = 60, baseExperience = 112)
        val domain = response.toDomain()

        assertEquals(25, domain.id)
        assertEquals("pikachu", domain.name)
        assertEquals(4, domain.height)
        assertEquals(60, domain.weight)
        assertEquals(112, domain.baseExperience)
    }

    @Test
    fun `PokemonDetailResponse toDomain prefers official artwork url over front default`() {
        val artworkUrl = "https://example.com/artwork/25.png"
        val response = createDetailResponse(
            sprites = SpritesResponse(
                frontDefault = "https://example.com/front/25.png",
                other = OtherSpritesResponse(
                    officialArtwork = OfficialArtworkResponse(frontDefault = artworkUrl)
                )
            )
        )
        val domain = response.toDomain()
        assertEquals(artworkUrl, domain.imageUrl)
    }

    @Test
    fun `PokemonDetailResponse toDomain falls back to frontDefault when official artwork is missing`() {
        val frontDefault = "https://example.com/front/25.png"
        val response = createDetailResponse(
            sprites = SpritesResponse(
                frontDefault = frontDefault,
                other = OtherSpritesResponse(officialArtwork = OfficialArtworkResponse(frontDefault = null))
            )
        )
        val domain = response.toDomain()
        assertEquals(frontDefault, domain.imageUrl)
    }

    @Test
    fun `PokemonDetailResponse toDomain falls back to frontDefault when other sprites section is null`() {
        val frontDefault = "https://example.com/front/1.png"
        val response = createDetailResponse(
            sprites = SpritesResponse(frontDefault = frontDefault, other = null)
        )
        val domain = response.toDomain()
        assertEquals(frontDefault, domain.imageUrl)
    }

    @Test
    fun `PokemonDetailResponse toDomain imageUrl is null when both sprite sources are null`() {
        val response = createDetailResponse(
            sprites = SpritesResponse(
                frontDefault = null,
                other = OtherSpritesResponse(officialArtwork = OfficialArtworkResponse(frontDefault = null))
            )
        )
        val domain = response.toDomain()
        assertNull(domain.imageUrl)
    }

    @Test
    fun `PokemonDetailResponse toDomain maps all stat entries with correct names and values`() {
        val response = createDetailResponse(
            stats = listOf(
                statEntry("hp", 45),
                statEntry("attack", 49),
                statEntry("defense", 49),
                statEntry("speed", 45)
            )
        )
        val domain = response.toDomain()

        assertEquals(4, domain.stats.size)
        assertEquals(PokemonStat("hp", 45), domain.stats[0])
        assertEquals(PokemonStat("attack", 49), domain.stats[1])
        assertEquals(PokemonStat("defense", 49), domain.stats[2])
        assertEquals(PokemonStat("speed", 45), domain.stats[3])
    }

    @Test
    fun `PokemonDetailResponse toDomain maps ability names correctly`() {
        val response = createDetailResponse(
            abilities = listOf(
                abilityEntry("overgrow", isHidden = false, slot = 1),
                abilityEntry("chlorophyll", isHidden = true, slot = 3)
            )
        )
        val domain = response.toDomain()
        assertEquals(listOf("overgrow", "chlorophyll"), domain.abilities)
    }

    @Test
    fun `PokemonDetailResponse toDomain maps type names correctly`() {
        val response = createDetailResponse(
            types = listOf(
                typeEntry("grass", slot = 1),
                typeEntry("poison", slot = 2)
            )
        )
        val domain = response.toDomain()
        assertEquals(listOf("grass", "poison"), domain.types)
    }

    @Test
    fun `Pokemon toEntity and back preserves all fields`() {
        val original = Pokemon(id = 1, name = "bulbasaur", imageUrl = "https://example.com/1.png")
        val roundTripped = original.toEntity().toDomain()
        assertEquals(original, roundTripped)
    }

    @Test
    fun `Pokemon toEntity and back preserves null imageUrl`() {
        val original = Pokemon(id = 1, name = "missingno", imageUrl = null)
        val roundTripped = original.toEntity().toDomain()
        assertEquals(original, roundTripped)
    }

    @Test
    fun `PokemonDetail toEntity and back preserves all scalar fields`() {
        val original = sampleDetail()
        val roundTripped = original.toEntity().toDomain()

        assertEquals(original.id, roundTripped.id)
        assertEquals(original.name, roundTripped.name)
        assertEquals(original.imageUrl, roundTripped.imageUrl)
        assertEquals(original.height, roundTripped.height)
        assertEquals(original.weight, roundTripped.weight)
        assertEquals(original.baseExperience, roundTripped.baseExperience)
    }

    @Test
    fun `PokemonDetail toEntity and back preserves stats list`() {
        val original = sampleDetail(
            stats = listOf(PokemonStat("hp", 45), PokemonStat("attack", 49))
        )
        val roundTripped = original.toEntity().toDomain()
        assertEquals(original.stats, roundTripped.stats)
    }

    @Test
    fun `PokemonDetail toEntity and back preserves abilities list`() {
        val original = sampleDetail(abilities = listOf("overgrow", "chlorophyll"))
        val roundTripped = original.toEntity().toDomain()
        assertEquals(original.abilities, roundTripped.abilities)
    }

    @Test
    fun `PokemonDetail toEntity and back preserves types list`() {
        val original = sampleDetail(types = listOf("grass", "poison"))
        val roundTripped = original.toEntity().toDomain()
        assertEquals(original.types, roundTripped.types)
    }

    @Test
    fun `PokemonDetail toEntity and back with empty stats list`() {
        val original = sampleDetail(stats = emptyList())
        val roundTripped = original.toEntity().toDomain()
        assertTrue(roundTripped.stats.isEmpty())
    }

    @Test
    fun `PokemonDetail toEntity and back with empty abilities list`() {
        val original = sampleDetail(abilities = emptyList())
        val roundTripped = original.toEntity().toDomain()
        assertTrue(roundTripped.abilities.isEmpty())
    }

    @Test
    fun `PokemonDetail toEntity and back with stat value at zero`() {
        val original = sampleDetail(stats = listOf(PokemonStat("hp", 0)))
        val roundTripped = original.toEntity().toDomain()
        assertEquals(0, roundTripped.stats.first().baseStat)
    }

    @Test
    fun `PokemonDetail toEntity and back with stat value at maximum 255`() {
        val original = sampleDetail(stats = listOf(PokemonStat("special-attack", 255)))
        val roundTripped = original.toEntity().toDomain()
        assertEquals(255, roundTripped.stats.first().baseStat)
    }

    @Test
    fun `PokemonDetailEntity toDomain deserialises statsJson correctly`() {
        // Manually craft the entity as it would be in Room to verify Moshi round-trip
        val entity = PokemonDetailEntity(
            id = 1,
            name = "bulbasaur",
            imageUrl = null,
            height = 7,
            weight = 69,
            baseExperience = 64,
            statsJson = """[{"name":"hp","baseStat":45},{"name":"attack","baseStat":49}]""",
            abilitiesJson = """["overgrow","chlorophyll"]""",
            typesJson = """["grass","poison"]"""
        )
        val domain = entity.toDomain()

        assertEquals(2, domain.stats.size)
        assertEquals(PokemonStat("hp", 45), domain.stats[0])
        assertEquals(PokemonStat("attack", 49), domain.stats[1])
        assertEquals(listOf("overgrow", "chlorophyll"), domain.abilities)
        assertEquals(listOf("grass", "poison"), domain.types)
    }

    private fun statEntry(name: String, baseStat: Int) = StatEntryResponse(
        baseStat = baseStat,
        effort = 0,
        stat = NamedApiResource(name = name, url = "https://pokeapi.co/api/v2/stat/1/")
    )

    private fun abilityEntry(name: String, isHidden: Boolean, slot: Int) = AbilityEntryResponse(
        ability = NamedApiResource(name = name, url = "https://pokeapi.co/api/v2/ability/65/"),
        isHidden = isHidden,
        slot = slot
    )

    private fun typeEntry(name: String, slot: Int) = TypeEntryResponse(
        slot = slot,
        type = NamedApiResource(name = name, url = "https://pokeapi.co/api/v2/type/12/")
    )

    private fun createDetailResponse(
        id: Int = 1,
        name: String = "bulbasaur",
        height: Int = 7,
        weight: Int = 69,
        baseExperience: Int = 64,
        sprites: SpritesResponse = SpritesResponse(
            frontDefault = "https://example.com/front/$id.png",
            other = OtherSpritesResponse(
                officialArtwork = OfficialArtworkResponse(
                    frontDefault = "https://example.com/artwork/$id.png"
                )
            )
        ),
        stats: List<StatEntryResponse> = listOf(statEntry("hp", 45)),
        abilities: List<AbilityEntryResponse> = listOf(abilityEntry("overgrow", false, 1)),
        types: List<TypeEntryResponse> = listOf(typeEntry("grass", 1))
    ) = DetailResponse(
        id = id,
        name = name,
        height = height,
        weight = weight,
        baseExperience = baseExperience,
        sprites = sprites,
        stats = stats,
        abilities = abilities,
        types = types
    )

    private fun sampleDetail(
        id: Int = 1,
        name: String = "bulbasaur",
        imageUrl: String? = "https://example.com/artwork/1.png",
        height: Int = 7,
        weight: Int = 69,
        baseExperience: Int = 64,
        stats: List<PokemonStat> = listOf(PokemonStat("hp", 45), PokemonStat("attack", 49)),
        abilities: List<String> = listOf("overgrow", "chlorophyll"),
        types: List<String> = listOf("grass", "poison")
    ) = PokemonDetail(
        id = id,
        name = name,
        imageUrl = imageUrl,
        height = height,
        weight = weight,
        baseExperience = baseExperience,
        stats = stats,
        abilities = abilities,
        types = types
    )
}
