package dev.bltucker.lazypizza.common

import dev.bltucker.lazypizza.home.MenuCategory
import dev.bltucker.lazypizza.home.MenuItemDto
import dev.bltucker.lazypizza.productdetails.Topping
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepository @Inject constructor(
    private val firebaseStorage: FirebaseStorageWrapper
) {

    suspend fun getAllMenuItems(): List<MenuItemDto> {
        val items = listOf(
            MenuItemDto(
                id = "1",
                name = "Margherita",
                description = "Tomato sauce, mozzarella, fresh basil, olive oil",
                price = 8.99,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "Margherita.png"
            ),
            MenuItemDto(
                id = "2",
                name = "Pepperoni",
                description = "Tomato sauce, mozzarella, pepperoni",
                price = 9.99,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "Pepperoni.png"
            ),
            MenuItemDto(
                id = "3",
                name = "Hawaiian",
                description = "Tomato sauce, mozzarella, ham, pineapple",
                price = 10.49,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "Hawaiian.png"
            ),
            MenuItemDto(
                id = "4",
                name = "BBQ Chicken",
                description = "BBQ sauce, mozzarella, grilled chicken, onion, corn",
                price = 11.49,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "BBQ Chicken.png"
            ),
            MenuItemDto(
                id = "5",
                name = "Four Cheese",
                description = "Mozzarella, gorgonzola, parmesan, ricotta",
                price = 11.99,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "Four Cheese.png"
            ),
            MenuItemDto(
                id = "6",
                name = "Veggie Delight",
                description = "Tomato sauce, mozzarella, mushrooms, olives, bell pepper, onion...",
                price = 9.79,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "Veggie Delight.png"
            ),
            MenuItemDto(
                id = "7",
                name = "Meat Lovers",
                description = "Tomato sauce, mozzarella, pepperoni, ham, bacon, sausage",
                price = 12.49,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "Meat Lovers.png"
            ),
            MenuItemDto(
                id = "8",
                name = "Spicy Inferno",
                description = "Tomato sauce, mozzarella, spicy salami, jalapeños, red chili pepper, ga...",
                price = 11.29,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "Spicy Inferno.png"
            ),
            MenuItemDto(
                id = "9",
                name = "Seafood Special",
                description = "Tomato sauce, mozzarella, shrimp, mussels, squid, parsley",
                price = 13.99,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "Seafood Special.png"
            ),
            MenuItemDto(
                id = "10",
                name = "Truffle Mushroom",
                description = "Cream sauce, mozzarella, mushrooms, truffle oil, parmesan",
                price = 12.99,
                imageUrl = null,
                category = MenuCategory.PIZZA,
                imageName = "Truffle Mushroom.png"
            ),
            MenuItemDto(
                id = "11",
                name = "Mineral Water",
                description = "",
                price = 1.49,
                imageUrl = null,
                category = MenuCategory.DRINKS,
                imageName = "mineral water.png"
            ),
            MenuItemDto(
                id = "12",
                name = "7-Up",
                description = "",
                price = 1.89,
                imageUrl = null,
                category = MenuCategory.DRINKS,
                imageName = "7-up.png"
            ),
            MenuItemDto(
                id = "13",
                name = "Pepsi",
                description = "",
                price = 1.99,
                imageUrl = null,
                category = MenuCategory.DRINKS,
                imageName = "pepsi.png"
            ),
            MenuItemDto(
                id = "14",
                name = "Orange Juice",
                description = "",
                price = 2.49,
                imageUrl = null,
                category = MenuCategory.DRINKS,
                imageName = "orange juice.png"
            ),
            MenuItemDto(
                id = "15",
                name = "Apple Juice",
                description = "",
                price = 2.29,
                imageUrl = null,
                category = MenuCategory.DRINKS,
                imageName = "apple juice.png"
            ),
            MenuItemDto(
                id = "16",
                name = "Iced Tea (Lemon)",
                description = "",
                price = 2.19,
                imageUrl = null,
                category = MenuCategory.DRINKS,
                imageName = "iced tea.png"
            ),
            MenuItemDto(
                id = "17",
                name = "Garlic Sauce",
                description = "",
                price = 0.59,
                imageUrl = null,
                category = MenuCategory.SAUCES,
                imageName = "Garlic Sauce.png"
            ),
            MenuItemDto(
                id = "18",
                name = "BBQ Sauce",
                description = "",
                price = 0.59,
                imageUrl = null,
                category = MenuCategory.SAUCES,
                imageName = "BBQ Sauce.png"
            ),
            MenuItemDto(
                id = "19",
                name = "Cheese Sauce",
                description = "",
                price = 0.89,
                imageUrl = null,
                category = MenuCategory.SAUCES,
                imageName = "Cheese Sauce.png"
            ),
            MenuItemDto(
                id = "20",
                name = "Spicy Chili Sauce",
                description = "",
                price = 0.59,
                imageUrl = null,
                category = MenuCategory.SAUCES,
                imageName = "Spicy Chili Sauce.png"
            ),
            MenuItemDto(
                id = "21",
                name = "Vanilla Ice Cream",
                description = "",
                price = 2.49,
                imageUrl = null,
                category = MenuCategory.ICE_CREAM,
                imageName = "vanilla.png"
            ),
            MenuItemDto(
                id = "22",
                name = "Chocolate Ice Cream",
                description = "",
                price = 2.49,
                imageUrl = null,
                category = MenuCategory.ICE_CREAM,
                imageName = "chocolate.png"
            ),
            MenuItemDto(
                id = "23",
                name = "Strawberry Ice Cream",
                description = "",
                price = 2.49,
                imageUrl = null,
                category = MenuCategory.ICE_CREAM,
                imageName = "strawberry.png"
            ),
            MenuItemDto(
                id = "24",
                name = "Cookies Ice Cream",
                description = "",
                price = 2.79,
                imageUrl = null,
                category = MenuCategory.ICE_CREAM,
                imageName = "cookies.png"
            ),
            MenuItemDto(
                id = "25",
                name = "Pistachio Ice Cream",
                description = "",
                price = 2.99,
                imageUrl = null,
                category = MenuCategory.ICE_CREAM,
                imageName = "pistachio.png"
            ),
            MenuItemDto(
                id = "26",
                name = "Mango Sorbet",
                description = "",
                price = 2.69,
                imageUrl = null,
                category = MenuCategory.ICE_CREAM,
                imageName = "mango sorbet.png"
            )
        )

        return items.map { item ->
            val imageUrl = try {
                fetchImageUrl(item.category, item.imageName)
            } catch (e: Exception) {
                null
            }
            item.copy(imageUrl = imageUrl)
        }
    }

    suspend fun getMenuItemById(itemId: String): MenuItemDto? {
        return getAllMenuItems().find { it.id == itemId }
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
                val imageName = "${topping.name.lowercase().replace(" ", "_")}.png"
                firebaseStorage.getToppingsImage(imageName)
            } catch (e: Exception) {
                null
            }
            topping.copy(imageUrl = imageUrl)
        }
    }

    private suspend fun fetchImageUrl(category: MenuCategory, imageName: String): String {
        return when (category) {
            MenuCategory.PIZZA -> firebaseStorage.getPizzaImage(imageName)
            MenuCategory.DRINKS -> firebaseStorage.getDrinkImage(imageName)
            MenuCategory.SAUCES -> firebaseStorage.getSauceImage(imageName)
            MenuCategory.ICE_CREAM -> firebaseStorage.getIceCreamImage(imageName)
        }
    }
}
