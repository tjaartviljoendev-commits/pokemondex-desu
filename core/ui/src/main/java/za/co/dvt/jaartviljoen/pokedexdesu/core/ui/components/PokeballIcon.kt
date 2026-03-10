package za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.R
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.PokedexDesuTheme

@Composable
fun PokeballIcon(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 48.dp,
) {
    val description = stringResource(R.string.content_desc_pokeball)
    Canvas(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = description },
    ) {
        drawPokeball(color)
    }
}

private fun DrawScope.drawPokeball(fillColor: Color) {
    val diameter = size.minDimension
    val radius = diameter / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    val strokeWidth = diameter * 0.04f

    // Top semicircle (coloured)
    drawArc(
        color = fillColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(diameter, diameter),
    )

    // Bottom semicircle (white)
    drawArc(
        color = Color.White,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(diameter, diameter),
    )

    // Outer ring
    drawCircle(
        color = Color(0xFF333333),
        radius = radius,
        center = center,
        style = Stroke(width = strokeWidth),
    )

    // Horizontal divider line
    drawLine(
        color = Color(0xFF333333),
        start = Offset(center.x - radius, center.y),
        end = Offset(center.x + radius, center.y),
        strokeWidth = strokeWidth * 1.5f,
    )

    // Centre button – outer ring
    val buttonRadius = radius * 0.22f
    drawCircle(
        color = Color(0xFF333333),
        radius = buttonRadius,
        center = center,
    )

    // Centre button – inner fill
    drawCircle(
        color = Color.White,
        radius = buttonRadius * 0.65f,
        center = center,
    )
}

@Preview(showBackground = true)
@Composable
private fun PokeballIconPreview() {
    PokedexDesuTheme {
        PokeballIcon(size = 96.dp)
    }
}
