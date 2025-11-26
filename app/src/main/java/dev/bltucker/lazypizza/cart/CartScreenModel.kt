package dev.bltucker.lazypizza.cart

import dev.bltucker.lazypizza.home.MenuItemDto
import javax.inject.Inject

data class CartScreenModel(
    val cartItems: List<CartItem> = emptyList(),
    val recommendedItems: List<MenuItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
) {
    val totalPrice: Double
        get() = cartItems.sumOf { it.totalPrice }

    val itemCount: Int
        get() = cartItems.sumOf { it.quantity }

    val isEmpty: Boolean
        get() = cartItems.isEmpty()

    fun formatPrice(price: Double): String {
        return "$%.2f".format(price)
    }

    fun getFormattedTotalPrice(): String {
        return formatPrice(totalPrice)
    }
}

class CartScreenModelReducer @Inject constructor() {

    fun createInitialState() = CartScreenModel(isLoading = true)

    fun updateWithCartItems(
        previousModel: CartScreenModel,
        cartItems: List<CartItem>
    ): CartScreenModel {
        return previousModel.copy(
            cartItems = cartItems,
            isLoading = false,
            isError = false
        )
    }

    fun updateWithRecommendedItems(
        previousModel: CartScreenModel,
        recommendedItems: List<MenuItemDto>
    ): CartScreenModel {
        return previousModel.copy(
            recommendedItems = recommendedItems
        )
    }

    fun updateWithError(previousModel: CartScreenModel): CartScreenModel {
        return previousModel.copy(
            isLoading = false,
            isError = true
        )
    }
}
