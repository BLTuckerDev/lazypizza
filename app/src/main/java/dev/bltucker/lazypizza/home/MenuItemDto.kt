package dev.bltucker.lazypizza.home

data class MenuItemDto(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val category: MenuCategory
)

enum class MenuCategory {
    PIZZA,
    DRINKS,
    SAUCES,
    ICE_CREAM
}
