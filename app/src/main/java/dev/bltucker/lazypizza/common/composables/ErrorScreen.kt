package dev.bltucker.lazypizza.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bltucker.lazypizza.common.theme.Grey
import dev.bltucker.lazypizza.common.theme.LazyPizzaTheme

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Companion.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "😕",
                style = MaterialTheme.typography.displayLarge,
                fontSize = 64.sp
            )

            Text(
                text = "Oops something went wrong",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Companion.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Companion.Center
            )

            Text(
                text = "Please try again later",
                style = MaterialTheme.typography.bodyMedium,
                color = Grey,
                textAlign = TextAlign.Companion.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenLightPreview() {
    LazyPizzaTheme(darkTheme = false) {
        ErrorScreen(modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenDarkPreview() {
    LazyPizzaTheme(darkTheme = true) {
        ErrorScreen(modifier = Modifier.fillMaxSize())
    }
}

