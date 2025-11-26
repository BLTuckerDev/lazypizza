package dev.bltucker.lazypizza.productdetails

data class Topping(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String?,
    val emoji: String
)
