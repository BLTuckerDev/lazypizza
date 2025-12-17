package dev.bltucker.lazypizza.home

import javax.inject.Inject

data class HomeScreenModel(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val menuItems: List<MenuItemDto> = emptyList(),
    val selectedCategory: MenuCategory = MenuCategory.PIZZA,
    val searchQuery: String = "",
    val cartQuantities: Map<String, Int> = emptyMap()
) {
    val filteredItems: List<MenuItemDto>
        get() = menuItems.filter { it.category == selectedCategory }
            .filter { item ->
                if (searchQuery.isBlank()) {
                    true
                } else {
                    item.name.contains(searchQuery, ignoreCase = true) ||
                            item.description.contains(searchQuery, ignoreCase = true)
                }
            }

    fun getQuantity(itemId: String): Int = cartQuantities[itemId] ?: 0
}

class HomeScreenModelReducer @Inject constructor() {

    fun createInitialState() = HomeScreenModel(isLoading = true)

    fun updateWithMenuItems(
        previousModel: HomeScreenModel,
        menuItems: List<MenuItemDto>
    ): HomeScreenModel {
        return previousModel.copy(
            menuItems = menuItems,
            isLoading = false,
            isError = false
        )
    }

    fun updateWithError(previousModel: HomeScreenModel): HomeScreenModel {
        return previousModel.copy(
            isLoading = false,
            isError = true
        )
    }

    fun updateSelectedCategory(
        previousModel: HomeScreenModel,
        category: MenuCategory
    ): HomeScreenModel {
        return previousModel.copy(selectedCategory = category)
    }

    fun updateSearchQuery(
        previousModel: HomeScreenModel,
        query: String
    ): HomeScreenModel {
        return previousModel.copy(searchQuery = query)
    }

    fun updateItemQuantity(
        previousModel: HomeScreenModel,
        itemId: String,
        quantity: Int
    ): HomeScreenModel {
        val newQuantities = previousModel.cartQuantities.toMutableMap()
        if (quantity > 0) {
            newQuantities[itemId] = quantity
        } else {
            newQuantities.remove(itemId)
        }
        return previousModel.copy(cartQuantities = newQuantities)
    }

    fun updateCartQuantities(
        previousModel: HomeScreenModel,
        quantities: Map<String, Int>
    ): HomeScreenModel {
        return previousModel.copy(cartQuantities = quantities)
    }
}
