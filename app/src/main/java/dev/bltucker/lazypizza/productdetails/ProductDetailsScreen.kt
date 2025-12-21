package dev.bltucker.lazypizza.productdetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.bltucker.lazypizza.common.composables.LazyPizzaButton
import dev.bltucker.lazypizza.common.composables.ToppingCard
import dev.bltucker.lazypizza.common.theme.Grey
import dev.bltucker.lazypizza.common.theme.LazyPizzaTheme
import dev.bltucker.lazypizza.common.theme.LightGrey
import dev.bltucker.lazypizza.common.theme.Orange
import dev.bltucker.lazypizza.home.MenuCategory
import dev.bltucker.lazypizza.home.MenuItemDto

const val PRODUCT_DETAILS_ROUTE = "product_details/{productId}"
const val PRODUCT_ID_ARG = "productId"

fun createProductDetailsRoute(productId: String): String {
    return "product_details/$productId"
}

fun NavController.navigateToProductDetails(productId: String) {
    navigate(createProductDetailsRoute(productId))
}

private data class ScreenActions(
    val onBackClick: () -> Unit,
    val onToppingClick: (String) -> Unit,
    val onIncrementTopping: (String) -> Unit,
    val onDecrementTopping: (String) -> Unit,
    val onAddToCart: () -> Unit
)

fun NavGraphBuilder.productDetailsScreen(onNavigateBack: () -> Unit) {
    composable(
        route = PRODUCT_DETAILS_ROUTE,
        arguments = listOf(
            navArgument(PRODUCT_ID_ARG) {
                type = NavType.StringType
            }
        )
    ) {
        val viewModel = hiltViewModel<ProductDetailsScreenViewModel>()
        val model = viewModel.observableModel.collectAsStateWithLifecycle()

        val screenActions = remember(viewModel) {
            ScreenActions(
                onBackClick = onNavigateBack,
                onToppingClick = viewModel::onToppingClick,
                onIncrementTopping = viewModel::onIncrementTopping,
                onDecrementTopping = viewModel::onDecrementTopping,
                onAddToCart = {
                    viewModel.onAddToCart()
                    onNavigateBack()
                }
            )
        }

        LifecycleStartEffect(Unit) {
            viewModel.onStart()
            onStopOrDispose { }
        }

        ProductDetailsScreen(
            modifier = Modifier.fillMaxSize(),
            model = model.value,
            screenActions = screenActions
        )
    }
}

@Composable
private fun ProductDetailsScreen(
    modifier: Modifier = Modifier,
    model: ProductDetailsScreenModel,
    screenActions: ScreenActions
) {
    Column(
        modifier = modifier.background(LightGrey)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ProductHeader(
                    modifier = Modifier.fillMaxWidth(),
                    productName = model.product?.name ?: "",
                    productDescription = model.product?.description ?: "",
                    productImageUrl = model.product?.imageUrl,
                    productEmoji = "🍕",
                    onBackClick = screenActions.onBackClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ADD EXTRA TOPPINGS",
                    style = MaterialTheme.typography.labelLarge,
                    color = Grey,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                ToppingsGrid(
                    toppings = model.availableToppings,
                    selectedToppings = model.selectedToppings,
                    onToppingClick = screenActions.onToppingClick,
                    onIncrementTopping = screenActions.onIncrementTopping,
                    onDecrementTopping = screenActions.onDecrementTopping,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        AddToCartBar(
            totalPrice = model.totalPrice,
            onAddToCart = screenActions.onAddToCart,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProductHeader(
    modifier: Modifier = Modifier,
    productName: String,
    productDescription: String,
    productImageUrl: String?,
    productEmoji: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier.background(Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(40.dp)
                    .background(LightGrey, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            if (productImageUrl != null) {
                AsyncImage(
                    model = productImageUrl,
                    contentDescription = productName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = productEmoji,
                    fontSize = 180.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = productName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = productDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = Grey,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ToppingsGrid(
    modifier: Modifier = Modifier,
    toppings: List<Topping>,
    selectedToppings: Map<String, Int>,
    onToppingClick: (String) -> Unit,
    onIncrementTopping: (String) -> Unit,
    onDecrementTopping: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.height(600.dp),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(toppings) { topping ->
            ToppingCardWithEmoji(
                emoji = topping.emoji,
                name = topping.name,
                price = "$${String.format("%.2f", topping.price)}",
                isSelected = selectedToppings.containsKey(topping.id),
                quantity = selectedToppings[topping.id] ?: 0,
                onCardClick = { onToppingClick(topping.id) },
                onIncrementClick = { onIncrementTopping(topping.id) },
                onDecrementClick = { onDecrementTopping(topping.id) }
            )
        }
    }
}

@Composable
private fun ToppingCardWithEmoji(
    modifier: Modifier = Modifier,
    emoji: String,
    name: String,
    price: String,
    isSelected: Boolean,
    quantity: Int,
    onCardClick: () -> Unit,
    onIncrementClick: () -> Unit,
    onDecrementClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Orange else Grey.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Orange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 48.sp
                )
            }

            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = price,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Grey.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onDecrementClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease quantity",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Grey.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onIncrementClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase quantity",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddToCartBar(
    modifier: Modifier = Modifier,
    totalPrice: Double,
    onAddToCart: () -> Unit
) {
    Box(
        modifier = modifier
            .background(Color.White)
            .padding(16.dp)
    ) {
        LazyPizzaButton(
            text = "Add to Cart for $${String.format("%.2f", totalPrice)}",
            onClick = onAddToCart,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailsScreenPreview() {
    val previewProduct = MenuItemDto(
        id = "1",
        name = "Margherita",
        description = "Tomato sauce, Mozzarella, Fresh basil, Olive oil",
        price = 8.99,
        imageUrl = null,
        category = MenuCategory.PIZZA,
        imageName = "Margherita.png"
    )

    val previewToppings = listOf(
        Topping("1", "Bacon", 1.0, null, "🥓"),
        Topping("2", "Extra Cheese", 1.0, null, "🧀"),
        Topping("3", "Corn", 0.50, null, "🌽"),
        Topping("4", "Tomato", 0.50, null, "🍅"),
        Topping("5", "Olives", 0.50, null, "🫒"),
        Topping("6", "Pepperoni", 1.0, null, "🍕")
    )

    val previewModel = ProductDetailsScreenModel(
        productId = "1",
        isLoading = false,
        product = previewProduct,
        availableToppings = previewToppings,
        selectedToppings = mapOf("2" to 1, "6" to 1)
    )

    val screenActions = ScreenActions(
        onBackClick = {},
        onToppingClick = {},
        onIncrementTopping = {},
        onDecrementTopping = {},
        onAddToCart = {}
    )

    LazyPizzaTheme {
        ProductDetailsScreen(
            model = previewModel,
            screenActions = screenActions
        )
    }
}
