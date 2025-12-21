package dev.bltucker.lazypizza.common.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.lazypizza.common.theme.Grey
import dev.bltucker.lazypizza.common.theme.LazyPizzaTheme
import dev.bltucker.lazypizza.common.theme.Orange

enum class ButtonVariant {
    Filled,
    Outlined
}

@Composable
fun LazyPizzaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Filled
) {
    when (variant) {
        ButtonVariant.Filled -> {
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White,
                    disabledContainerColor = Grey.copy(alpha = 0.3f),
                    disabledContentColor = Grey
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        ButtonVariant.Outlined -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Orange,
                    disabledContainerColor = Color.White,
                    disabledContentColor = Grey
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (enabled) Orange else Grey
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LazyPizzaButtonFilledEnabledPreview() {
    LazyPizzaTheme {
        LazyPizzaButton(
            text = "Add to Cart",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LazyPizzaButtonFilledDisabledPreview() {
    LazyPizzaTheme {
        LazyPizzaButton(
            text = "Add to Cart",
            onClick = {},
            enabled = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LazyPizzaButtonOutlinedEnabledPreview() {
    LazyPizzaTheme() {
        Surface(){
            LazyPizzaButton(
                text = "View Details",
                onClick = {},
                variant = ButtonVariant.Outlined,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LazyPizzaButtonOutlinedDisabledPreview() {
    LazyPizzaTheme() {
        LazyPizzaButton(
            text = "View Details",
            onClick = {},
            enabled = false,
            variant = ButtonVariant.Outlined,
            modifier = Modifier.padding(16.dp)
        )
    }
}
