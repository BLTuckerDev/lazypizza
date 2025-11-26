package dev.bltucker.lazypizza.productdetails

import dev.bltucker.lazypizza.home.MenuItemDto
import javax.inject.Inject

data class ProductDetailsScreenModel(
    val productId: String? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val product: MenuItemDto? = null,
    val availableToppings: List<Topping> = emptyList(),
    val selectedToppings: Map<String, Int> = emptyMap()
) {
    val basePrice: Double
        get() = product?.price ?: 0.0

    val toppingsPrice: Double
        get() = selectedToppings.entries.sumOf { (toppingId, quantity) ->
            val topping = availableToppings.find { it.id == toppingId }
            (topping?.price ?: 0.0) * quantity
        }

    val totalPrice: Double
        get() = basePrice + toppingsPrice

    fun getToppingQuantity(toppingId: String): Int = selectedToppings[toppingId] ?: 0

    fun isToppingSelected(toppingId: String): Boolean = selectedToppings.containsKey(toppingId)
}

class ProductDetailsScreenModelReducer @Inject constructor() {

    fun createInitialState(productId: String) = ProductDetailsScreenModel(
        productId = productId,
        isLoading = true
    )

    fun updateWithProductData(
        previousModel: ProductDetailsScreenModel,
        product: MenuItemDto,
        toppings: List<Topping>
    ): ProductDetailsScreenModel {
        return previousModel.copy(
            product = product,
            availableToppings = toppings,
            isLoading = false,
            isError = false
        )
    }

    fun updateWithError(previousModel: ProductDetailsScreenModel): ProductDetailsScreenModel {
        return previousModel.copy(
            isLoading = false,
            isError = true
        )
    }

    fun updateToppingQuantity(
        previousModel: ProductDetailsScreenModel,
        toppingId: String,
        quantity: Int
    ): ProductDetailsScreenModel {
        val newSelectedToppings = previousModel.selectedToppings.toMutableMap()
        if (quantity > 0) {
            newSelectedToppings[toppingId] = quantity
        } else {
            newSelectedToppings.remove(toppingId)
        }
        return previousModel.copy(selectedToppings = newSelectedToppings)
    }

    fun toggleTopping(
        previousModel: ProductDetailsScreenModel,
        toppingId: String
    ): ProductDetailsScreenModel {
        val newSelectedToppings = previousModel.selectedToppings.toMutableMap()
        if (previousModel.isToppingSelected(toppingId)) {
            newSelectedToppings.remove(toppingId)
        } else {
            newSelectedToppings[toppingId] = 1
        }
        return previousModel.copy(selectedToppings = newSelectedToppings)
    }

    fun incrementToppingQuantity(
        previousModel: ProductDetailsScreenModel,
        toppingId: String
    ): ProductDetailsScreenModel {
        val currentQuantity = previousModel.getToppingQuantity(toppingId)
        return updateToppingQuantity(previousModel, toppingId, currentQuantity + 1)
    }

    fun decrementToppingQuantity(
        previousModel: ProductDetailsScreenModel,
        toppingId: String
    ): ProductDetailsScreenModel {
        val currentQuantity = previousModel.getToppingQuantity(toppingId)
        if (currentQuantity > 1) {
            return updateToppingQuantity(previousModel, toppingId, currentQuantity - 1)
        } else {
            return updateToppingQuantity(previousModel, toppingId, 0)
        }
    }
}
