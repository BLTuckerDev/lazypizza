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
import dev.bltucker.lazypizza.common.MenuRepository

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
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
                val items = menuRepository.getAllMenuItems()
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
