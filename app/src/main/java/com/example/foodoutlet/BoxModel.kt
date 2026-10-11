package com.example.foodoutlet

data class GroceryBox(
    val id: String,
    val name: String,
    val description: String,
    val retailValue: Double,
    val price: Double,
    val contents: List<String>
) {
    val savingsPercentage: Int get() = (((retailValue - price) / retailValue) * 100).toInt()
}

val sampleBoxes = listOf(
    GroceryBox(
        id = "1",
        name = "Family Value Box",
        description = "Practical household grocery savings.",
        retailValue = 55.0,
        price = 32.0,
        contents = listOf("2x Dry Pasta (500g)", "1x Pasta Sauce (650ml)", "2x Canned Beans", "Crackers")
    ),
    GroceryBox(
        id = "2",
        name = "Treat Box",
        description = "Snacks, sweets, beverages, and high-enjoyment products.",
        retailValue = 30.0,
        price = 20.0,
        contents = listOf("Cookies", "Chips", "Chocolate", "Juice Boxes")
    ),
    GroceryBox(
        id = "3",
        name = "Pantry Box",
        description = "Pantry stock-up value and everyday essentials.",
        retailValue = 45.0,
        price = 28.0,
        contents = listOf("Flour", "Sugar", "Cereal", "Soup", "Dry Pasta")
    )
)