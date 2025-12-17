package dev.bltucker.lazypizza.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil3.compose.AsyncImage
import dev.bltucker.lazypizza.common.composables.CartItemCard
import dev.bltucker.lazypizza.common.composables.LazyPizzaButton
import dev.bltucker.lazypizza.common.theme.Grey
import dev.bltucker.lazypizza.common.theme.LazyPizzaTheme
import dev.bltucker.lazypizza.common.theme.LightGrey
import dev.bltucker.lazypizza.home.MenuCategory
import dev.bltucker.lazypizza.home.MenuItemDto

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

const val CART_SCREEN_ROUTE = "cart"

fun NavController.navigateToCart() {
    navigate(CART_SCREEN_ROUTE)
}

fun NavGraphBuilder.cartScreen(
    windowSizeClass: WindowWidthSizeClass,
    onNavigateToMenu: () -> Unit
) {
    composable(route = CART_SCREEN_ROUTE) {
        val viewModel = hiltViewModel<CartScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        LifecycleStartEffect(Unit) {
            viewModel.onStart()
            onStopOrDispose { }
        }

        CartScreen(
            modifier = Modifier.fillMaxSize(),
            model = model,
            windowSizeClass = windowSizeClass,
            onNavigateToMenu = onNavigateToMenu,
            onIncrementQuantity = viewModel::onIncrementQuantity,
            onDecrementQuantity = viewModel::onDecrementQuantity,
            onRemoveItem = viewModel::onRemoveItem,
            onAddRecommendedItem = viewModel::onAddRecommendedItem,
            onProceedToCheckout = viewModel::onProceedToCheckout
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartScreen(
    modifier: Modifier = Modifier,
    model: CartScreenModel,
    windowSizeClass: WindowWidthSizeClass,
    onNavigateToMenu: () -> Unit,
    onIncrementQuantity: (String) -> Unit,
    onDecrementQuantity: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    onAddRecommendedItem: (String) -> Unit,
    onProceedToCheckout: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cart",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = LightGrey
    ) { paddingValues ->
        if (model.isEmpty) {
            EmptyCartContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onNavigateToMenu = onNavigateToMenu
            )
        } else {
            CartContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                model = model,
                isWideScreen = windowSizeClass >= WindowWidthSizeClass.Expanded,
                onIncrementQuantity = onIncrementQuantity,
                onDecrementQuantity = onDecrementQuantity,
                onRemoveItem = onRemoveItem,
                onAddRecommendedItem = onAddRecommendedItem,
                onProceedToCheckout = onProceedToCheckout
            )
        }
    }
}

@Composable
private fun CartContent(
    modifier: Modifier = Modifier,
    model: CartScreenModel,
    isWideScreen: Boolean,
    onIncrementQuantity: (String) -> Unit,
    onDecrementQuantity: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    onAddRecommendedItem: (String) -> Unit,
    onProceedToCheckout: () -> Unit
) {
    if (isWideScreen) {
        Row(
            modifier = modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Column: Cart Items
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = model.cartItems,
                    key = { it.menuItem.id }
                ) { cartItem ->
                    CartItemCard(
                        imageUrl = cartItem.menuItem.imageUrl,
                        name = cartItem.menuItem.name,
                        quantity = cartItem.quantity,
                        unitPrice = model.formatPrice(cartItem.menuItem.price),
                        totalPrice = model.formatPrice(cartItem.totalPrice),
                        onIncrementClick = { onIncrementQuantity(cartItem.menuItem.id) },
                        onDecrementClick = { onDecrementQuantity(cartItem.menuItem.id) },
                        onDeleteClick = { onRemoveItem(cartItem.menuItem.id) }
                    )
                }
            }

            // Right Column: Recommendations + Checkout
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Recommendations
                if (model.recommendedItems.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "RECOMMENDED",
                            style = MaterialTheme.typography.labelMedium,
                            color = Grey,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = model.recommendedItems,
                                key = { it.id }
                            ) { menuItem ->
                                RecommendedItemCard(
                                    menuItem = menuItem,
                                    onAddClick = { onAddRecommendedItem(menuItem.id) }
                                )
                            }
                        }
                    }
                }

                // Checkout
                CheckoutSection(
                    totalPrice = model.getFormattedTotalPrice(),
                    onProceedToCheckout = onProceedToCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                )
            }
        }
    } else {
        // Mobile Layout (Original)
        Column(
            modifier = modifier
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = model.cartItems,
                    key = { it.menuItem.id }
                ) { cartItem ->
                    CartItemCard(
                        imageUrl = cartItem.menuItem.imageUrl,
                        name = cartItem.menuItem.name,
                        quantity = cartItem.quantity,
                        unitPrice = model.formatPrice(cartItem.menuItem.price),
                        totalPrice = model.formatPrice(cartItem.totalPrice),
                        onIncrementClick = { onIncrementQuantity(cartItem.menuItem.id) },
                        onDecrementClick = { onDecrementQuantity(cartItem.menuItem.id) },
                        onDeleteClick = { onRemoveItem(cartItem.menuItem.id) }
                    )
                }

                if (model.recommendedItems.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "RECOMMENDED TO ADD TO YOUR ORDER",
                            style = MaterialTheme.typography.labelMedium,
                            color = Grey,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(
                                items = model.recommendedItems,
                                key = { it.id }
                            ) { menuItem ->
                                RecommendedItemCard(
                                    menuItem = menuItem,
                                    onAddClick = { onAddRecommendedItem(menuItem.id) }
                                )
                            }
                        }
                    }
                }
            }

            CheckoutSection(
                totalPrice = model.getFormattedTotalPrice(),
                onProceedToCheckout = onProceedToCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun EmptyCartContent(
    modifier: Modifier = Modifier,
    onNavigateToMenu: () -> Unit
) {
    Box(
        modifier = modifier.background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Your Cart Is Empty",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Head back to the menu and grab a pizza you love.",
                style = MaterialTheme.typography.bodyLarge,
                color = Grey,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            LazyPizzaButton(
                text = "Back to Menu",
                onClick = onNavigateToMenu,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
private fun CartContent(
    modifier: Modifier = Modifier,
    model: CartScreenModel,
    onIncrementQuantity: (String) -> Unit,
    onDecrementQuantity: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    onAddRecommendedItem: (String) -> Unit,
    onProceedToCheckout: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(Color.White),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = model.cartItems,
                key = { it.menuItem.id }
            ) { cartItem ->
                CartItemCard(
                    imageUrl = cartItem.menuItem.imageUrl,
                    name = cartItem.menuItem.name,
                    quantity = cartItem.quantity,
                    unitPrice = model.formatPrice(cartItem.menuItem.price),
                    totalPrice = model.formatPrice(cartItem.totalPrice),
                    onIncrementClick = { onIncrementQuantity(cartItem.menuItem.id) },
                    onDecrementClick = { onDecrementQuantity(cartItem.menuItem.id) },
                    onDeleteClick = { onRemoveItem(cartItem.menuItem.id) }
                )
            }

            if (model.recommendedItems.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "RECOMMENDED TO ADD TO YOUR ORDER",
                        style = MaterialTheme.typography.labelMedium,
                        color = Grey,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = model.recommendedItems,
                            key = { it.id }
                        ) { menuItem ->
                            RecommendedItemCard(
                                menuItem = menuItem,
                                onAddClick = { onAddRecommendedItem(menuItem.id) }
                            )
                        }
                    }
                }
            }
        }

        CheckoutSection(
            totalPrice = model.getFormattedTotalPrice(),
            onProceedToCheckout = onProceedToCheckout,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        )
    }
}

@Composable
private fun RecommendedItemCard(
    menuItem: MenuItemDto,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(width = 140.dp, height = 180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightGrey
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = menuItem.imageUrl,
                    contentDescription = menuItem.name,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = menuItem.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$%.2f".format(menuItem.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to cart",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckoutSection(
    totalPrice: String,
    onProceedToCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyPizzaButton(
            text = "Proceed to Checkout ($totalPrice)",
            onClick = onProceedToCheckout,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyCartScreenPreview() {
    LazyPizzaTheme {
        CartScreen(
            model = CartScreenModel(
                cartItems = emptyList(),
                isLoading = false
            ),
            windowSizeClass = WindowWidthSizeClass.Compact,
            onNavigateToMenu = {},
            onIncrementQuantity = {},
            onDecrementQuantity = {},
            onRemoveItem = {},
            onAddRecommendedItem = {},
            onProceedToCheckout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartScreenWithItemsPreview() {
    LazyPizzaTheme {
        val cartItems = listOf(
            CartItem(
                id = "1",
                menuItem = MenuItemDto(
                    id = "1",
                    name = "Margherita",
                    description = "Tomato sauce, mozzarella, fresh basil, olive oil",
                    price = 10.99,
                    imageUrl = null,
                    category = MenuCategory.PIZZA
                ),
                quantity = 2
            ),
            CartItem(
                id = "13",
                menuItem = MenuItemDto(
                    id = "13",
                    name = "Pepsi",
                    description = "",
                    price = 1.99,
                    imageUrl = null,
                    category = MenuCategory.DRINKS
                ),
                quantity = 2
            ),
            CartItem(
                id = "24",
                menuItem = MenuItemDto(
                    id = "24",
                    name = "Cookies Ice Cream",
                    description = "",
                    price = 1.49,
                    imageUrl = null,
                    category = MenuCategory.ICE_CREAM
                ),
                quantity = 1
            )
        )

        val recommendedItems = listOf(
            MenuItemDto(
                id = "18",
                name = "BBQ Sauce",
                description = "",
                price = 0.59,
                imageUrl = null,
                category = MenuCategory.SAUCES
            ),
            MenuItemDto(
                id = "17",
                name = "Garlic Sauce",
                description = "",
                price = 0.59,
                imageUrl = null,
                category = MenuCategory.SAUCES
            )
        )

        CartScreen(
            model = CartScreenModel(
                cartItems = cartItems,
                recommendedItems = recommendedItems,
                isLoading = false
            ),
            windowSizeClass = WindowWidthSizeClass.Compact,
            onNavigateToMenu = {},
            onIncrementQuantity = {},
            onDecrementQuantity = {},
            onRemoveItem = {},
            onAddRecommendedItem = {},
            onProceedToCheckout = {}
        )
    }
}
