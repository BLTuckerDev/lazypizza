package dev.bltucker.lazypizza.productdetails

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
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
class ProductDetailsScreenViewModel @Inject constructor(
    private val repository: ProductDetailsRepository,
    private val cartRepository: CartRepository,
    private val modelReducer: ProductDetailsScreenModelReducer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = savedStateHandle.get<String>(PRODUCT_ID_ARG) ?: ""

    @VisibleForTesting
    val mutableModel = MutableStateFlow(modelReducer.createInitialState(productId))
    val observableModel: StateFlow<ProductDetailsScreenModel> = mutableModel

    private var hasStarted = false

    fun onStart() {
        if (hasStarted) {
            return
        }
        hasStarted = true

        loadProductDetails()
    }

    private fun loadProductDetails() {
        viewModelScope.launch {
            try {
                val product = repository.getProductById(productId)
                val toppings = repository.getAvailableToppings()

                if (product != null) {
                    mutableModel.update {
                        modelReducer.updateWithProductData(it, product, toppings)
                    }
                } else {
                    mutableModel.update {
                        modelReducer.updateWithError(it)
                    }
                }
            } catch (e: Exception) {
                mutableModel.update {
                    modelReducer.updateWithError(it)
                }
            }
        }
    }

    fun onToppingClick(toppingId: String) {
        mutableModel.update {
            modelReducer.toggleTopping(it, toppingId)
        }
    }

    fun onIncrementTopping(toppingId: String) {
        mutableModel.update {
            modelReducer.incrementToppingQuantity(it, toppingId)
        }
    }

    fun onDecrementTopping(toppingId: String) {
        mutableModel.update {
            modelReducer.decrementToppingQuantity(it, toppingId)
        }
    }

    fun onAddToCart() {
        val currentState = mutableModel.value
        val product = currentState.product ?: return

        val toppingsTotal = currentState.availableToppings
            .filter { currentState.selectedToppings.containsKey(it.id) }
            .sumOf { it.price * (currentState.selectedToppings[it.id] ?: 0) }

        cartRepository.addItem(
            menuItem = product,
            quantity = 1,
            toppings = currentState.selectedToppings,
            toppingsTotal = toppingsTotal
        )
    }
}
