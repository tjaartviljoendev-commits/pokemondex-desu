package za.co.dvt.jaartviljoen.pokedexdesu.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import za.co.dvt.jaartviljoen.pokedexdesu.R
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components.PokeballIcon

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    val offsetY = remember { Animatable(-300f) }
    val scale = remember { Animatable(1f) }
    val flashAlpha = remember { Animatable(0f) }
    val appName = stringResource(R.string.app_name)
    var visibleCharCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow)
        )

        delay(200L)
        scale.animateTo(1.3f, tween(durationMillis = 400))
        flashAlpha.animateTo(0.8f, tween(durationMillis = 150))
        flashAlpha.animateTo(0f, tween(durationMillis = 150))

        scale.animateTo(1f, tween(durationMillis = 200))

        for (i in 1..appName.length) {
            visibleCharCount = i
            delay(60L)
        }

        delay(600L)
        onSplashComplete()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PokeballIcon(
                modifier = Modifier
                    .offset(y = offsetY.value.dp)
                    .scale(scale.value),
                size = 120.dp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = appName.take(visibleCharCount),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(flashAlpha.value)
                .background(Color.White)
        )
    }
}
