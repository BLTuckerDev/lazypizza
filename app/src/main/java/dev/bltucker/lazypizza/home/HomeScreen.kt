package dev.bltucker.lazypizza.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import coil3.compose.AsyncImage
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.bltucker.lazypizza.R
import dev.bltucker.lazypizza.common.theme.Grey
import dev.bltucker.lazypizza.common.theme.LazyPizzaTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import dev.bltucker.lazypizza.common.composables.ErrorScreen
import dev.bltucker.lazypizza.common.composables.LoadingScreen

const val HOME_SCREEN_ROUTE = "home"

private data class ScreenActions(
    val onCategorySelected: (MenuCategory) -> Unit,
    val onSearchQueryChanged: (String) -> Unit,
    val onAddToCart: (String) -> Unit,
    val onIncreaseQuantity: (String) -> Unit,
    val onDecreaseQuantity: (String) -> Unit,
    val onRemoveFromCart: (String) -> Unit,
    val onItemClick: (String) -> Unit
)

fun NavGraphBuilder.homeScreen(
    windowSizeClass: WindowWidthSizeClass,
    onNavigateToProductDetails: (String) -> Unit
) {
    composable(route = HOME_SCREEN_ROUTE) {
        val viewModel = hiltViewModel<HomeScreenViewModel>()
        val model = viewModel.observableModel.collectAsStateWithLifecycle()

        val screenActions = remember(viewModel) {
            ScreenActions(
                onCategorySelected = viewModel::onCategorySelected,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onAddToCart = viewModel::onAddToCart,
                onIncreaseQuantity = viewModel::onIncreaseQuantity,
                onDecreaseQuantity = viewModel::onDecreaseQuantity,
                onRemoveFromCart = viewModel::onRemoveFromCart,
                onItemClick = onNavigateToProductDetails
            )
        }

        LifecycleStartEffect(Unit) {
            viewModel.onStart()
            onStopOrDispose { }
        }

        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            model = model.value,
            screenActions = screenActions,
            windowSizeClass = windowSizeClass
        )
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    model: HomeScreenModel,
    screenActions: ScreenActions,
    windowSizeClass: WindowWidthSizeClass
) {
    when {
        model.isLoading -> LoadingScreen(modifier = modifier.fillMaxSize())
        model.isError -> ErrorScreen(modifier = modifier.fillMaxSize())
        else -> HomeScreenContent(
            modifier = modifier,
            model = model,
            screenActions = screenActions,
            windowSizeClass = windowSizeClass
        )
    }
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    model: HomeScreenModel,
    screenActions: ScreenActions,
    windowSizeClass: WindowWidthSizeClass
) {
    val isWideScreen = windowSizeClass >= WindowWidthSizeClass.Expanded
    val columns = if (isWideScreen) 2 else 1

    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        TopBar()

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                HeroSection()
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SearchBar(
                    query = model.searchQuery,
                    onQueryChange = screenActions.onSearchQueryChanged,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryTabs(
                    selectedCategory = model.selectedCategory,
                    onCategorySelected = screenActions.onCategorySelected,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = model.selectedCategory.name.replace("_", " "),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (model.filteredItems.isEmpty() && model.searchQuery.isNotBlank()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptySearchState(
                        query = model.searchQuery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 32.dp)
                    )
                }
            } else {
                items(
                    items = model.filteredItems,
                    key = { it.id }
                ) { item ->
                    MenuItem(
                        item = item,
                        quantity = model.getQuantity(item.id),
                        onItemClick = { screenActions.onItemClick(item.id) },
                        onAddToCart = { screenActions.onAddToCart(item.id) },
                        onIncreaseQuantity = { screenActions.onIncreaseQuantity(item.id) },
                        onDecreaseQuantity = { screenActions.onDecreaseQuantity(item.id) },
                        onRemoveFromCart = { screenActions.onRemoveFromCart(item.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "🍕",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "LazyPizza",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Phone",
                tint = Grey,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "+1 (555) 321-7890",
                style = MaterialTheme.typography.bodyMedium,
                color = Grey
            )
        }
    }
}

@Composable
private fun HeroSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painter = painterResource(R.drawable.lazy_pizza_splash_icon),
            contentDescription = "Pizza Hero",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        placeholder = {
            Text(
                text = "Search for delicious food...",
                color = Grey
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Grey
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Grey
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
private fun CategoryTabs(
    selectedCategory: MenuCategory,
    onCategorySelected: (MenuCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = remember {
        listOf(
            MenuCategory.PIZZA to "Pizza",
            MenuCategory.DRINKS to "Drinks",
            MenuCategory.SAUCES to "Sauces",
            MenuCategory.ICE_CREAM to "Ice Cream"
        )
    }

    ScrollableTabRow(
        selectedTabIndex = categories.indexOfFirst { it.first == selectedCategory },
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = 16.dp,
        indicator = {},
        divider = {}
    ) {
        categories.forEach { (category, label) ->
            Tab(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = label,
                        fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.onSurface,
                unselectedContentColor = Grey
            )
        }
    }
}

@Composable
private fun MenuItem(
    item: MenuItemDto,
    quantity: Int,
    onItemClick: () -> Unit,
    onAddToCart: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onRemoveFromCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUrl != null) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = when (item.category) {
                            MenuCategory.PIZZA -> "🍕"
                            MenuCategory.DRINKS -> "🥤"
                            MenuCategory.SAUCES -> "🧂"
                            MenuCategory.ICE_CREAM -> "🍨"
                        },
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (item.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Grey,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${String.format("%.2f", item.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Box(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                if (quantity == 0) {
                    TextButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                    ) {
                        Text(
                            text = "Add to Cart",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = if (quantity == 1) onRemoveFromCart else onDecreaseQuantity,
                            modifier = Modifier
                                .size(16.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (quantity == 1) Icons.Default.Close else Icons.Default.Remove,
                                contentDescription = if (quantity == 1) "Remove" else "Decrease",
                                tint = Grey,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(
                            onClick = onIncreaseQuantity,
                            modifier = Modifier
                                .size(16.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchState(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔍",
            style = MaterialTheme.typography.displayLarge,
            fontSize = 64.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No results found for your query",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Try searching for \"$query\" in a different category",
            style = MaterialTheme.typography.bodyMedium,
            color = Grey,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val previewModel = HomeScreenModel(
        isLoading = false,
        menuItems = listOf(
            MenuItemDto(
                id = "1",
                name = "Margherita",
                description = "Tomato sauce, mozzarella, fresh basil, olive oil",
                price = 8.99,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "margherita.jpg"
            ),
            MenuItemDto(
                id = "2",
                name = "Pepperoni",
                description = "Tomato sauce, mozzarella, pepperoni",
                price = 9.99,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "pepperoni.jpg"
            )
        ),
        selectedCategory = MenuCategory.PIZZA,
        cartQuantities = mapOf("2" to 2)
    )

    val screenActions = ScreenActions(
        onCategorySelected = {},
        onSearchQueryChanged = {},
        onAddToCart = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
        onRemoveFromCart = {},
        onItemClick = {}
    )

    LazyPizzaTheme {
        HomeScreen(
            model = previewModel,
            screenActions = screenActions,
            windowSizeClass = WindowWidthSizeClass.Compact
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreviewDark() {
    val previewModel = HomeScreenModel(
        isLoading = false,
        menuItems = listOf(
            MenuItemDto(
                id = "1",
                name = "Margherita",
                description = "Tomato sauce, mozzarella, fresh basil, olive oil",
                price = 8.99,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "margherita.jpg"
            ),
            MenuItemDto(
                id = "2",
                name = "Pepperoni",
                description = "Tomato sauce, mozzarella, pepperoni",
                price = 9.99,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "pepperoni.jpg"
            )
        ),
        selectedCategory = MenuCategory.PIZZA,
        cartQuantities = mapOf("2" to 2)
    )

    val screenActions = ScreenActions(
        onCategorySelected = {},
        onSearchQueryChanged = {},
        onAddToCart = {},
        onIncreaseQuantity = {},
        onDecreaseQuantity = {},
        onRemoveFromCart = {},
        onItemClick = {}
    )

    LazyPizzaTheme(darkTheme = true) {
        HomeScreen(
            model = previewModel,
            screenActions = screenActions,
            windowSizeClass = WindowWidthSizeClass.Compact
        )
    }
}
