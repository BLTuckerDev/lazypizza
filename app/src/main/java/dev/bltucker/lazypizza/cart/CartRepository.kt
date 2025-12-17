package dev.bltucker.lazypizza.cart

import dev.bltucker.lazypizza.home.MenuItemDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor() {

    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<String, CartItem>> = _cartItems

    val totalPrice: Double
        get() = _cartItems.value.values.sumOf { it.totalPrice }

    val itemCount: Int
        get() = _cartItems.value.values.sumOf { it.quantity }

    fun addItem(
        menuItem: MenuItemDto,
        quantity: Int = 1,
        toppings: Map<String, Int> = emptyMap(),
        toppingsTotal: Double = 0.0
    ) {
        val cartItemId = generateCartItemId(menuItem.id, toppings)
        _cartItems.update { currentItems ->
            val existingItem = currentItems[cartItemId]
            val updatedItem = if (existingItem != null) {
                existingItem.copy(quantity = existingItem.quantity + quantity)
            } else {
                CartItem(
                    id = cartItemId,
                    menuItem = menuItem,
                    toppings = toppings,
                    toppingsTotal = toppingsTotal,
                    quantity = quantity
                )
            }
            currentItems + (cartItemId to updatedItem)
        }
    }

    fun removeItem(cartItemId: String) {
        _cartItems.update { currentItems ->
            currentItems - cartItemId
        }
    }

    fun updateQuantity(cartItemId: String, quantity: Int) {
        if (quantity <= 0) {
            removeItem(cartItemId)
            return
        }

        _cartItems.update { currentItems ->
            val existingItem = currentItems[cartItemId] ?: return@update currentItems
            currentItems + (cartItemId to existingItem.copy(quantity = quantity))
        }
    }

    fun incrementQuantity(cartItemId: String) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems[cartItemId] ?: return@update currentItems
            currentItems + (cartItemId to existingItem.copy(quantity = existingItem.quantity + 1))
        }
    }

    fun decrementQuantity(cartItemId: String) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems[cartItemId] ?: return@update currentItems
            val newQuantity = existingItem.quantity - 1
            if (newQuantity <= 0) {
                currentItems - cartItemId
            } else {
                currentItems + (cartItemId to existingItem.copy(quantity = newQuantity))
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }

    private fun generateCartItemId(menuItemId: String, toppings: Map<String, Int>): String {
        if (toppings.isEmpty()) return menuItemId
        val toppingsString = toppings.entries.sortedBy { it.key }
            .joinToString("_") { "${it.key}-${it.value}" }
        return "${menuItemId}_$toppingsString"
    }
}
