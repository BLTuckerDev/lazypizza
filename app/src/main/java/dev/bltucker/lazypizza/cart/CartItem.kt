package dev.bltucker.lazypizza.cart

import dev.bltucker.lazypizza.home.MenuItemDto

data class CartItem(
    val id: String,
    val menuItem: MenuItemDto,
    val toppings: Map<String, Int> = emptyMap(),
    val toppingsTotal: Double = 0.0,
    val quantity: Int
) {
    val unitPrice: Double
        get() = menuItem.price + toppingsTotal

    val totalPrice: Double
        get() = unitPrice * quantity
}
