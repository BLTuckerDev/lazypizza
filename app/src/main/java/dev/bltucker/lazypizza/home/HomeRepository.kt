package dev.bltucker.lazypizza.home

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor() {

    suspend fun getMenuItems(): List<MenuItemDto> {
        return listOf(
            MenuItemDto(
                id = "1",
                name = "Margherita",
                description = "Tomato sauce, mozzarella, fresh basil, olive oil",
                price = 8.99,
                imageUrl = null,
                category = MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "2",
                name = "Pepperoni",
                description = "Tomato sauce, mozzarella, pepperoni",
                price = 9.99,
                imageUrl = null,
                category = MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "3",
                name = "Hawaiian",
                description = "Tomato sauce, mozzarella, ham, pineapple",
                price = 10.49,
                imageUrl = null,
                category = MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "4",
                name = "BBQ Chicken",
                description = "BBQ sauce, mozzarella, grilled chicken, onion, corn",
                price = 11.49,
                imageUrl = null,
                category = MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "5",
                name = "Four Cheese",
                description = "Mozzarella, gorgonzola, parmesan, ricotta",
                price = 11.99,
                imageUrl = null,
                category = MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "6",
                name = "Veggie Delight",
                description = "Tomato sauce, mozzarella, mushrooms, olives, bell pepper, onion...",
                price = 9.79,
                imageUrl = null,
                category = MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "7",
                name = "Meat Lovers",
                description = "Tomato sauce, mozzarella, pepperoni, ham, bacon, sausage",
                price = 12.49,
                imageUrl = null,
                category = MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "8",
                name = "Spicy Inferno",
                description = "Tomato sauce, mozzarella, spicy salami, jalapeños, red chili pepper, ga...",
                price = 11.29,
                imageUrl = null,
                category = MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "9",
                name = "Seafood Special",
                description = "Tomato sauce, mozzarella, shrimp, mussels, squid, parsley",
                price = 13.99,
                imageUrl = null,
                category = MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "10",
                name = "Truffle Mushroom",
                description = "Cream sauce, mozzarella, mushrooms, truffle oil, parmesan",
                price = 12.99,
                imageUrl = null,
                category = MenuCategory.PIZZA
            ),
            MenuItemDto(
                id = "11",
                name = "Mineral Water",
                description = "",
                price = 1.49,
                imageUrl = null,
                category = MenuCategory.DRINKS
            ),
            MenuItemDto(
                id = "12",
                name = "7-Up",
                description = "",
                price = 1.89,
                imageUrl = null,
                category = MenuCategory.DRINKS
            ),
            MenuItemDto(
                id = "13",
                name = "Pepsi",
                description = "",
                price = 1.99,
                imageUrl = null,
                category = MenuCategory.DRINKS
            ),
            MenuItemDto(
                id = "14",
                name = "Orange Juice",
                description = "",
                price = 2.49,
                imageUrl = null,
                category = MenuCategory.DRINKS
            ),
            MenuItemDto(
                id = "15",
                name = "Apple Juice",
                description = "",
                price = 2.29,
                imageUrl = null,
                category = MenuCategory.DRINKS
            ),
            MenuItemDto(
                id = "16",
                name = "Iced Tea (Lemon)",
                description = "",
                price = 2.19,
                imageUrl = null,
                category = MenuCategory.DRINKS
            ),
            MenuItemDto(
                id = "17",
                name = "Garlic Sauce",
                description = "",
                price = 0.59,
                imageUrl = null,
                category = MenuCategory.SAUCES
            ),
            MenuItemDto(
                id = "18",
                name = "BBQ Sauce",
                description = "",
                price = 0.59,
                imageUrl = null,
                category = MenuCategory.SAUCES
            ),
            MenuItemDto(
                id = "19",
                name = "Cheese Sauce",
                description = "",
                price = 0.89,
                imageUrl = null,
                category = MenuCategory.SAUCES
            ),
            MenuItemDto(
                id = "20",
                name = "Spicy Chili Sauce",
                description = "",
                price = 0.59,
                imageUrl = null,
                category = MenuCategory.SAUCES
            ),
            MenuItemDto(
                id = "21",
                name = "Vanilla Ice Cream",
                description = "",
                price = 2.49,
                imageUrl = null,
                category = MenuCategory.ICE_CREAM
            ),
            MenuItemDto(
                id = "22",
                name = "Chocolate Ice Cream",
                description = "",
                price = 2.49,
                imageUrl = null,
                category = MenuCategory.ICE_CREAM
            ),
            MenuItemDto(
                id = "23",
                name = "Strawberry Ice Cream",
                description = "",
                price = 2.49,
                imageUrl = null,
                category = MenuCategory.ICE_CREAM
            ),
            MenuItemDto(
                id = "24",
                name = "Cookies Ice Cream",
                description = "",
                price = 2.79,
                imageUrl = null,
                category = MenuCategory.ICE_CREAM
            ),
            MenuItemDto(
                id = "25",
                name = "Pistachio Ice Cream",
                description = "",
                price = 2.99,
                imageUrl = null,
                category = MenuCategory.ICE_CREAM
            ),
            MenuItemDto(
                id = "26",
                name = "Mango Sorbet",
                description = "",
                price = 2.69,
                imageUrl = null,
                category = MenuCategory.ICE_CREAM
            )
        )
    }
}
