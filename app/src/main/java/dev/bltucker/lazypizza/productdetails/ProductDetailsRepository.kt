package dev.bltucker.lazypizza.productdetails

import dev.bltucker.lazypizza.common.FirebaseStorageWrapper
import dev.bltucker.lazypizza.common.MenuRepository
import dev.bltucker.lazypizza.home.MenuItemDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductDetailsRepository @Inject constructor(
    private val menuRepository: MenuRepository,
    private val firebaseStorage: FirebaseStorageWrapper
) {

    suspend fun getProductById(productId: String): MenuItemDto? {
        return menuRepository.getMenuItemById(productId)
    }

    suspend fun getAvailableToppings(): List<Topping> {
        val toppings = listOf(
            Topping(
                id = "topping_1",
                name = "Bacon",
                price = 1.0,
                imageUrl = null,
                emoji = "🥓"
            ),
            Topping(
                id = "topping_2",
                name = "Extra Cheese",
                price = 1.0,
                imageUrl = null,
                emoji = "🧀"
            ),
            Topping(
                id = "topping_3",
                name = "Corn",
                price = 0.50,
                imageUrl = null,
                emoji = "🌽"
            ),
            Topping(
                id = "topping_4",
                name = "Tomato",
                price = 0.50,
                imageUrl = null,
                emoji = "🍅"
            ),
            Topping(
                id = "topping_5",
                name = "Olives",
                price = 0.50,
                imageUrl = null,
                emoji = "🫒"
            ),
            Topping(
                id = "topping_6",
                name = "Pepperoni",
                price = 1.0,
                imageUrl = null,
                emoji = "🍕"
            ),
            Topping(
                id = "topping_7",
                name = "Mushrooms",
                price = 0.50,
                imageUrl = null,
                emoji = "🍄"
            ),
            Topping(
                id = "topping_8",
                name = "Basil",
                price = 0.50,
                imageUrl = null,
                emoji = "🌿"
            ),
            Topping(
                id = "topping_9",
                name = "Pineapple",
                price = 1.0,
                imageUrl = null,
                emoji = "🍍"
            ),
            Topping(
                id = "topping_10",
                name = "Onion",
                price = 0.50,
                imageUrl = null,
                emoji = "🧅"
            ),
            Topping(
                id = "topping_11",
                name = "Chili Peppers",
                price = 0.50,
                imageUrl = null,
                emoji = "🌶️"
            ),
            Topping(
                id = "topping_12",
                name = "Spinach",
                price = 0.50,
                imageUrl = null,
                emoji = "🥬"
            )
        )

        return toppings.map { topping ->
            val imageUrl = try {
                val imageName = "${topping.name.lowercase().replace(" ", "_")}.jpg"
                firebaseStorage.getToppingsImage(imageName)
            } catch (e: Exception) {
                null
            }
            topping.copy(imageUrl = imageUrl)
        }
    }
}
