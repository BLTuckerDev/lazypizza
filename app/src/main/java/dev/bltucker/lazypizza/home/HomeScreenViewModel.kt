package dev.bltucker.lazypizza.home

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import dev.bltucker.lazypizza.cart.CartRepository

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val cartRepository: CartRepository,
    private val modelReducer: HomeScreenModelReducer
) : ViewModel() {

    @VisibleForTesting
    val mutableModel = MutableStateFlow(modelReducer.createInitialState())
    val observableModel: StateFlow<HomeScreenModel> = mutableModel

    private var hasStarted = false

    fun onStart() {
        if (hasStarted) {
            return
        }
        hasStarted = true

        loadMenuItems()
        observeCart()
    }

    private fun loadMenuItems() {
        viewModelScope.launch {
            try {
                val items = repository.getMenuItems()
                mutableModel.update {
                    modelReducer.updateWithMenuItems(it, items)
                }
            } catch (e: Exception) {
                mutableModel.update {
                    modelReducer.updateWithError(it)
                }
            }
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.cartItems.collect { cartItems ->
                // Map cart items back to product ID quantities
                // For Home Screen, we aggregate all quantities for a product ID?
                // Or just show quantity for the "base" item?
                // UX: Usually shows total quantity of that product (including variants) or just base.
                // Given the specific "add" button behavior, it's safer to show specific base item quantity
                // OR aggregate. Let's aggregate for now to show user they have *some* of this pizza.
                // Requirement 38: "Tapping changes it to a quantity selector...".
                // If I have 1 Pepperoni (base) and 1 Pepperoni (Extra Cheese), showing "2" on Home Screen might be confusing if +/- only affects base.
                // Let's assume Home Screen only interacts with BASE items (no toppings).
                // So we only count items where ID matches product ID exactly (meaning no toppings suffix).
                
                val quantities = cartItems.values
                    .filter { it.toppings.isEmpty() }
                    .associate { it.menuItem.id to it.quantity }

                mutableModel.update {
                    modelReducer.updateCartQuantities(it, quantities)
                }
            }
        }
    }

    fun onCategorySelected(category: MenuCategory) {
        mutableModel.update {
            modelReducer.updateSelectedCategory(it, category)
        }
    }

    fun onSearchQueryChanged(query: String) {
        mutableModel.update {
            modelReducer.updateSearchQuery(it, query)
        }
    }

    fun onAddToCart(itemId: String) {
        val item = mutableModel.value.menuItems.find { it.id == itemId } ?: return
        cartRepository.addItem(item)
    }

    fun onIncreaseQuantity(itemId: String) {
        cartRepository.incrementQuantity(itemId)
    }

    fun onDecreaseQuantity(itemId: String) {
        cartRepository.decrementQuantity(itemId)
    }

    fun onRemoveFromCart(itemId: String) {
        cartRepository.removeItem(itemId)
    }
}
