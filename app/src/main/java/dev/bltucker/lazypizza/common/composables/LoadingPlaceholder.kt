package dev.bltucker.lazypizza.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.bltucker.lazypizza.common.theme.LazyPizzaTheme
import dev.bltucker.lazypizza.common.theme.LightGrey

@Composable
fun LoadingPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Preview(showBackground = true)
@Composable
private fun LoadingPlaceholderPreview() {
    LazyPizzaTheme {
        LoadingPlaceholder()
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPlaceholderPreviewDark() {
    LazyPizzaTheme(darkTheme = true) {
        LoadingPlaceholder()
    }
}
