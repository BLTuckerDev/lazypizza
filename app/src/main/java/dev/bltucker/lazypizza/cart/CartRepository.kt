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

    fun addItem(menuItem: MenuItemDto, quantity: Int = 1) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems[menuItem.id]
            val updatedItem = if (existingItem != null) {
                existingItem.copy(quantity = existingItem.quantity + quantity)
            } else {
                CartItem(menuItem = menuItem, quantity = quantity)
            }
            currentItems + (menuItem.id to updatedItem)
        }
    }

    fun removeItem(menuItemId: String) {
        _cartItems.update { currentItems ->
            currentItems - menuItemId
        }
    }

    fun updateQuantity(menuItemId: String, quantity: Int) {
        if (quantity <= 0) {
            removeItem(menuItemId)
            return
        }

        _cartItems.update { currentItems ->
            val existingItem = currentItems[menuItemId] ?: return@update currentItems
            currentItems + (menuItemId to existingItem.copy(quantity = quantity))
        }
    }

    fun incrementQuantity(menuItemId: String) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems[menuItemId] ?: return@update currentItems
            currentItems + (menuItemId to existingItem.copy(quantity = existingItem.quantity + 1))
        }
    }

    fun decrementQuantity(menuItemId: String) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems[menuItemId] ?: return@update currentItems
            val newQuantity = existingItem.quantity - 1
            if (newQuantity <= 0) {
                currentItems - menuItemId
            } else {
                currentItems + (menuItemId to existingItem.copy(quantity = newQuantity))
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
    }
}
