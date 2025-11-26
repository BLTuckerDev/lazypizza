package dev.bltucker.lazypizza.cart

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.lazypizza.home.HomeRepository
import dev.bltucker.lazypizza.home.MenuCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartScreenViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val homeRepository: HomeRepository,
    private val modelReducer: CartScreenModelReducer
) : ViewModel() {

    @VisibleForTesting
    val mutableModel = MutableStateFlow(modelReducer.createInitialState())
    val observableModel: StateFlow<CartScreenModel> = mutableModel

    private var hasStarted = false

    fun onStart() {
        if (hasStarted) {
            return
        }
        hasStarted = true

        observeCartItems()
        loadRecommendedItems()
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            cartRepository.cartItems.collect { cartItemsMap ->
                mutableModel.update {
                    modelReducer.updateWithCartItems(it, cartItemsMap.values.toList())
                }
            }
        }
    }

    private fun loadRecommendedItems() {
        viewModelScope.launch {
            try {
                val allItems = homeRepository.getMenuItems()
                val recommendedItems = allItems
                    .filter { it.category == MenuCategory.SAUCES }
                    .take(3)
                mutableModel.update {
                    modelReducer.updateWithRecommendedItems(it, recommendedItems)
                }
            } catch (e: Exception) {
                // Silently fail for recommended items
            }
        }
    }

    fun onIncrementQuantity(menuItemId: String) {
        cartRepository.incrementQuantity(menuItemId)
    }

    fun onDecrementQuantity(menuItemId: String) {
        cartRepository.decrementQuantity(menuItemId)
    }

    fun onRemoveItem(menuItemId: String) {
        cartRepository.removeItem(menuItemId)
    }

    fun onAddRecommendedItem(menuItemId: String) {
        viewModelScope.launch {
            val item = mutableModel.value.recommendedItems.find { it.id == menuItemId }
            if (item != null) {
                cartRepository.addItem(item, 1)
            }
        }
    }

    fun onClearCart() {
        cartRepository.clearCart()
    }

    fun onProceedToCheckout() {
        // TODO: Navigate to checkout screen
    }
}
