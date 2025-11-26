package dev.bltucker.lazypizza.productdetails

import dev.bltucker.lazypizza.home.MenuItemDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductDetailsRepository @Inject constructor() {

    suspend fun getProductById(productId: String): MenuItemDto? {
        return getAllProducts().find { it.id == productId }
    }

    suspend fun getAvailableToppings(): List<Topping> {
        return listOf(
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
    }

    private fun getAllProducts(): List<MenuItemDto> {
        return listOf(
            MenuItemDto(
                id = "1",
                name = "Margherita",
                description = "Tomato sauce, Mozzarella, Fresh basil, Olive oil",
                price = 8.99,
                imageUrl = null,
                category = dev.bltucker.lazypizza.home.MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "2",
                name = "Pepperoni",
                description = "Tomato sauce, mozzarella, pepperoni",
                price = 9.99,
                imageUrl = null,
                category = dev.bltucker.lazypizza.home.MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "3",
                name = "Hawaiian",
                description = "Tomato sauce, mozzarella, ham, pineapple",
                price = 10.49,
                imageUrl = null,
                category = dev.bltucker.lazypizza.home.MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "4",
                name = "BBQ Chicken",
                description = "BBQ sauce, mozzarella, grilled chicken, onion, corn",
                price = 11.49,
                imageUrl = null,
                category = dev.bltucker.lazypizza.home.MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "5",
                name = "Four Cheese",
                description = "Mozzarella, gorgonzola, parmesan, ricotta",
                price = 11.99,
                imageUrl = null,
                category = dev.bltucker.lazypizza.home.MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "6",
                name = "Veggie Delight",
                description = "Tomato sauce, mozzarella, mushrooms, olives, bell pepper, onion...",
                price = 9.79,
                imageUrl = null,
                category = dev.bltucker.lazypizza.home.MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "7",
                name = "Meat Lovers",
                description = "Tomato sauce, mozzarella, pepperoni, ham, bacon, sausage",
                price = 12.49,
                imageUrl = null,
                category = dev.bltucker.lazypizza.home.MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "8",
                name = "Spicy Inferno",
                description = "Tomato sauce, mozzarella, spicy salami, jalapeños, red chili pepper, ga...",
                price = 11.29,
                imageUrl = null,
                category = dev.bltucker.lazypizza.home.MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "9",
                name = "Seafood Special",
                description = "Tomato sauce, mozzarella, shrimp, mussels, squid, parsley",
                price = 13.99,
                imageUrl = null,
                category = dev.bltucker.lazypizza.home.MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "10",
                name = "Truffle Mushroom",
                description = "Cream sauce, mozzarella, mushrooms, truffle oil, parmesan",
                price = 12.99,
                imageUrl = null,
                category = dev.bltucker.lazypizza.home.MenuCategory.PIZZA
            )
        )
    }
}
