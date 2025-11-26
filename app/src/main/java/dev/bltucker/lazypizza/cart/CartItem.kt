package dev.bltucker.lazypizza.cart

import dev.bltucker.lazypizza.home.MenuItemDto

data class CartItem(
    val menuItem: MenuItemDto,
    val quantity: Int
) {
    val totalPrice: Double
        get() = menuItem.price * quantity
}
