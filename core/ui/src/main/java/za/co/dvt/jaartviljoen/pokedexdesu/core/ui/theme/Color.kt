package za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── Pokédex Red (accent only) ──
val PokedexRed = Color(0xFFC62828)
val PokedexRedLight = Color(0xFFFF5350)

// ── Pokéball Steel ──
val PokeballSteel = Color(0xFF3E4759)
val PokeballSteelLight = Color(0xFFB0C6FF)

// ── Electric Teal ──
val ElectricTeal = Color(0xFF00796B)
val ElectricTealLight = Color(0xFF5EEDC8)

val LightColorScheme = lightColorScheme(
    primary = PokedexRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD5),
    onPrimaryContainer = Color(0xFF410001),
    secondary = PokeballSteel,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6E3FF),
    onSecondaryContainer = Color(0xFF1A2036),
    tertiary = ElectricTeal,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFA7F3D0),
    onTertiaryContainer = Color(0xFF002019),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF8F8FC),
    onBackground = Color(0xFF1A1A2E),
    surface = Color(0xFFF8F8FC),
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFFE8E8F0),
    onSurfaceVariant = Color(0xFF46464F),
    surfaceContainer = Color(0xFFEFEFF6),
    surfaceContainerHigh = Color(0xFFE8E8F0),
    surfaceContainerHighest = Color(0xFFE0E0E8),
    outline = Color(0xFF767680),
    outlineVariant = Color(0xFFC6C6D0),
    inverseSurface = Color(0xFF2F2F3D),
    inverseOnSurface = Color(0xFFF2F2FA),
    inversePrimary = Color(0xFFFFB4AA),
    surfaceTint = PokedexRed,
    scrim = Color.Black,
)

val DarkColorScheme = darkColorScheme(
    primary = PokedexRedLight,
    onPrimary = Color(0xFF690003),
    primaryContainer = Color(0xFF932118),
    onPrimaryContainer = Color(0xFFFFDAD5),
    secondary = PokeballSteelLight,
    onSecondary = Color(0xFF1A2036),
    secondaryContainer = Color(0xFF2A3A54),
    onSecondaryContainer = Color(0xFFD6E3FF),
    tertiary = ElectricTealLight,
    onTertiary = Color(0xFF00382B),
    tertiaryContainer = Color(0xFF005140),
    onTertiaryContainer = Color(0xFFA7F3D0),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF121218),
    onBackground = Color(0xFFE8E8F0),
    surface = Color(0xFF121218),
    onSurface = Color(0xFFE8E8F0),
    surfaceVariant = Color(0xFF46464F),
    onSurfaceVariant = Color(0xFFC6C6D0),
    surfaceContainer = Color(0xFF1E1E28),
    surfaceContainerHigh = Color(0xFF28283A),
    surfaceContainerHighest = Color(0xFF333345),
    outline = Color(0xFF90909A),
    outlineVariant = Color(0xFF46464F),
    inverseSurface = Color(0xFFE8E8F0),
    inverseOnSurface = Color(0xFF2F2F3D),
    inversePrimary = PokedexRed,
    surfaceTint = PokedexRedLight,
    scrim = Color.Black,
)

private val TypeColorMap = mapOf(
    "normal" to Color(0xFFA8A77A),
    "fire" to Color(0xFFEE8130),
    "water" to Color(0xFF6390F0),
    "electric" to Color(0xFFF7D02C),
    "grass" to Color(0xFF7AC74C),
    "ice" to Color(0xFF96D9D6),
    "fighting" to Color(0xFFC22E28),
    "poison" to Color(0xFFA33EA1),
    "ground" to Color(0xFFE2BF65),
    "flying" to Color(0xFFA98FF3),
    "psychic" to Color(0xFFF95587),
    "bug" to Color(0xFFA6B91A),
    "rock" to Color(0xFFB6A136),
    "ghost" to Color(0xFF735797),
    "dragon" to Color(0xFF6F35FC),
    "dark" to Color(0xFF705746),
    "steel" to Color(0xFFB7B7CE),
    "fairy" to Color(0xFFD685AD),
)

private val DarkTextTypes = setOf(
    "normal", "electric", "grass", "ice", "ground", "flying", "bug", "rock", "steel", "fairy",
)

fun pokemonTypeColor(type: String): Color =
    TypeColorMap[type.lowercase()] ?: Color(0xFFA8A77A)

fun pokemonTypeTextColor(type: String): Color =
    if (type.lowercase() in DarkTextTypes) Color(0xFF1A1A2E) else Color.White
