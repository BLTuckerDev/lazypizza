package dev.bltucker.lazypizza.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.bltucker.lazypizza.common.theme.Grey
import dev.bltucker.lazypizza.common.theme.LazyPizzaTheme
import dev.bltucker.lazypizza.common.theme.LightGrey
import dev.bltucker.lazypizza.common.theme.Red

@Composable
fun CartItemCard(
    imageUrl: String?,
    name: String,
    quantity: Int,
    unitPrice: String,
    totalPrice: String,
    onIncrementClick: () -> Unit,
    onDecrementClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    toppings: Map<String, Int> = emptyMap()
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightGrey),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (toppings.isNotEmpty()) {
                    Text(
                        text = toppings.entries.joinToString(", ") { "${it.key} x${it.value}" },
                        style = MaterialTheme.typography.bodySmall,
                        color = Grey
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Grey.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onDecrementClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease quantity",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Grey.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onIncrementClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase quantity",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete item",
                        tint = Red,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = totalPrice,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "$quantity x $unitPrice",
                    style = MaterialTheme.typography.bodySmall,
                    color = Grey
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartItemCardPreview() {
    LazyPizzaTheme {
        CartItemCard(
            imageUrl = null,
            name = "Margherita",
            quantity = 2,
            unitPrice = "$8.99",
            totalPrice = "$17.98",
            onIncrementClick = {},
            onDecrementClick = {},
            onDeleteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartItemCardSinglePreview() {
    LazyPizzaTheme {
        CartItemCard(
            imageUrl = null,
            name = "Pepperoni",
            quantity = 1,
            unitPrice = "$9.99",
            totalPrice = "$9.99",
            onIncrementClick = {},
            onDecrementClick = {},
            onDeleteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartItemCardMultiplePreview() {
    LazyPizzaTheme {
        CartItemCard(
            imageUrl = null,
            name = "Hawaiian",
            quantity = 10,
            unitPrice = "$10.99",
            totalPrice = "$109.90",
            onIncrementClick = {},
            onDecrementClick = {},
            onDeleteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartItemCardPreviewDark() {
    LazyPizzaTheme(darkTheme = true) {
        CartItemCard(
            imageUrl = null,
            name = "Margherita",
            quantity = 2,
            unitPrice = "$8.99",
            totalPrice = "$17.98",
            onIncrementClick = {},
            onDecrementClick = {},
            onDeleteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartItemCardSinglePreviewDark() {
    LazyPizzaTheme(darkTheme = true) {
        CartItemCard(
            imageUrl = null,
            name = "Pepperoni",
            quantity = 1,
            unitPrice = "$9.99",
            totalPrice = "$9.99",
            onIncrementClick = {},
            onDecrementClick = {},
            onDeleteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartItemCardMultiplePreviewDark() {
    LazyPizzaTheme(darkTheme = true) {
        CartItemCard(
            imageUrl = null,
            name = "Hawaiian",
            quantity = 10,
            unitPrice = "$10.99",
            totalPrice = "$109.90",
            onIncrementClick = {},
            onDecrementClick = {},
            onDeleteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
