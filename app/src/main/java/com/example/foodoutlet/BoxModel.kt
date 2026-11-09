package com.example.foodoutlet

data class GroceryBox(
    val id: String,
    val name: String,
    val description: String,
    val retailValue: Double,
    val price: Double,
    val contents: List<String>,
    val tags: List<String>,
    val imageUrl: String
) {
    val savingsPercentage: Int get() = (((retailValue - price) / retailValue) * 100).toInt()
}

val sampleBoxes = listOf(
    GroceryBox(
        id = "1",
        name = "Family Value Box (Nov 8 - 14)",
        description = "Last week we featured rice, but this week it's all about premium pasta! Includes 2x Dry Pasta, 1x Organic Sauce, and 2x Black Beans.",
        retailValue = 22.0,
        price = 14.50,
        contents = listOf("2x Premium Dry Pasta (500g)", "1x Organic Pasta Sauce (650ml)", "2x Canned Black Beans"),
        tags = listOf("This Week's Pick", "Family Size"),
        imageUrl = "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=600&q=80"
    ),
    GroceryBox(
        id = "2",
        name = "Treat Box (Nov 8 - 14)",
        description = "New snacks this week! We replaced last week's popcorn with kettle-cooked chips and added a dark chocolate bar.",
        retailValue = 18.0,
        price = 11.00,
        contents = listOf("Kettle Cooked Chips", "Dark Chocolate Bar", "Organic Juice Boxes (6-pack)"),
        tags = listOf("Sweet Tooth", "Weekend"),
        imageUrl = "https://images.unsplash.com/photo-1599599810769-bcde5a160d32?auto=format&fit=crop&w=600&q=80"
    ),
    GroceryBox(
        id = "3",
        name = "Pantry Box (Nov 8 - 14)",
        description = "Stock up on new essentials! This week features 1kg Organic Flour, Cane Sugar, and Morning Cereal to keep your cupboards full.",
        retailValue = 20.0,
        price = 12.50,
        contents = listOf("Organic Flour (1kg)", "Cane Sugar (1kg)", "Morning Cereal"),
        tags = listOf("Essentials", "Stock Up"),
        imageUrl = "https://images.unsplash.com/photo-1606859191214-25806e8e2423?auto=format&fit=crop&w=600&q=80"
    )
)

val vegBoxes = listOf(
    GroceryBox(
        id = "v1",
        name = "Plant-Based Box (Nov 8 - 14)",
        description = "Swapped last week's lentils for Organic Quinoa! Perfect for meal prepping. Includes Vegan Pesto and Almond Milk.",
        retailValue = 24.0,
        price = 15.00,
        contents = listOf("2x Organic Quinoa (500g)", "1x Vegan Pesto", "Almond Milk (1L)"),
        tags = listOf("Vegan", "This Week"),
        imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=600&q=80"
    ),
    GroceryBox(
        id = "v2",
        name = "Green Treat Box (Nov 8 - 14)",
        description = "Freshly updated! Swapped fruit bars with vegan brownie bites. Also includes Kale Chips and a 4-pack of Kombucha.",
        retailValue = 17.0,
        price = 10.50,
        contents = listOf("Vegan Brownie Bites", "Kale Chips", "Kombucha (4-pack)"),
        tags = listOf("Plant-Based", "Guilt-Free"),
        imageUrl = "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?auto=format&fit=crop&w=600&q=80"
    ),
    GroceryBox(
        id = "v3",
        name = "Earth Pantry Box (Nov 8 - 14)",
        description = "New sustainable staples. Featuring Gluten-Free Oats, Chickpea Pasta, and Chia Seeds for a healthy week.",
        retailValue = 19.0,
        price = 11.50,
        contents = listOf("Gluten-Free Oats", "Chickpea Pasta", "Chia Seeds"),
        tags = listOf("Sustainable", "Clean Eating"),
        imageUrl = "https://images.unsplash.com/photo-1543362906-acfc16c67564?auto=format&fit=crop&w=600&q=80"
    )
)