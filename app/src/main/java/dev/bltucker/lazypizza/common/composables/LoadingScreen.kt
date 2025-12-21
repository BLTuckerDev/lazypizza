package dev.bltucker.lazypizza.common.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.lazypizza.R
import dev.bltucker.lazypizza.common.theme.LazyPizzaTheme

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pizza_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pizza_rotation"
    )

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Companion.Center
    ) {
        Image(
            painter = painterResource(R.drawable.lazy_pizza_splash_icon),
            contentDescription = "Loading",
            modifier = Modifier.Companion
                .size(120.dp)
                .rotate(rotation)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenLightPreview() {
    LazyPizzaTheme(darkTheme = false) {
        LoadingScreen(modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingScreenDarkPreview() {
    LazyPizzaTheme(darkTheme = true) {
        LoadingScreen(modifier = Modifier.fillMaxSize())
    }
}